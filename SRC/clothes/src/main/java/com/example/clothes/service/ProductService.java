package com.example.clothes.service;

import com.example.clothes.DTO.ProductDTO;
import com.example.clothes.model.*;
import com.example.clothes.repository.CategoryRepository;
import com.example.clothes.repository.ProductImageRepository;
import com.example.clothes.repository.ProductRepository;
import com.example.clothes.repository.ProductVariantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.List;


@Service
public class ProductService {

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductVariantRepository variantRepo;
    @Autowired private ProductImageRepository imageRepo;
    @Autowired private CategoryRepository categoryRepo;

    // Đường dẫn lưu ảnh vào source code (để đẩy lên Git)
    private final String BASE_UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/products/";

    @Transactional // Đảm bảo lưu tất cả thành công, nếu lỗi 1 cái thì rollback hết
    public void addProduct(ProductDTO dto) throws IOException {
        
        // --- 1. XỬ LÝ DANH MỤC (CATEGORY) ---
        Category category = categoryRepo.findByName(dto.getCategoryName());
        if (category == null) {
            // Nếu chưa có thì tạo mới
            category = new Category();
            category.setName(dto.getCategoryName());
            category = categoryRepo.save(category);
        }

        // --- 2. XỬ LÝ SẢN PHẨM (PRODUCT) ---
        Product product = new Product();
        product.setName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category); // Gán category vừa tìm được/tạo mới
        product.setStatus(true);       // Mặc định là đang bán
        product.setCreatedAt(java.time.LocalDateTime.now());
        
        Product savedProduct = productRepo.save(product);

        // --- 3. XỬ LÝ BIẾN THỂ (VARIANTS - SIZE & STOCK) ---
        // Kiểm tra xem người dùng có nhập size không
        if (dto.getSizes() != null && dto.getQuantities() != null) {
            for (int i = 0; i < dto.getSizes().size(); i++) {
                String size = dto.getSizes().get(i);
                Integer stock = dto.getQuantities().get(i);

                // Chỉ lưu nếu size có nhập chữ (tránh dòng trống)
                if (size != null && !size.trim().isEmpty()) {
                    ProductVariant variant = new ProductVariant();
                    variant.setProduct(savedProduct); // Liên kết với Product
                    variant.setSize(size);
                    variant.setStock(stock != null ? stock : 0);
                    variantRepo.save(variant);
                }
            }
        }

        // --- 4. XỬ LÝ ẢNH (IMAGES) ---
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            // Tạo tên folder chuẩn (VD: "Áo Thun" -> "ao-thun")
            String productSlug = toSlug(savedProduct.getName());
            Path uploadPath = Paths.get(BASE_UPLOAD_DIR + productSlug);

            // Tạo thư mục nếu chưa có
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                    
                    // Lưu file vào ổ cứng (src/main/resources/...)
                    try (InputStream inputStream = file.getInputStream()) {
                        Path filePath = uploadPath.resolve(originalFilename);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new IOException("Lỗi lưu file: " + originalFilename, e);
                    }

                    // Lưu đường dẫn vào Database
                    ProductImage image = new ProductImage();
                    image.setProduct(savedProduct);
                    image.setImageUrl("/images/products/" + productSlug + "/" + originalFilename);
                    imageRepo.save(image);
                }
            }
        }
    }

    // Hàm tiện ích: Chuyển tiếng Việt có dấu thành không dấu (để đặt tên folder)
    public static String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().replaceAll("đ", "d");
    }

    public List<Product> getAllProducts(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            // Gọi hàm tối ưu có JOIN FETCH
            return productRepo.searchByNameWithVariants(keyword);
        }
        // Gọi hàm tối ưu có JOIN FETCH
        return productRepo.findAllWithVariants();
    }

    // Cập nhật tồn kho (Stock)
    // Hàm cập nhật số lượng tồn kho
    public void updateVariantStock(Integer variantId, Integer newStock) {
        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU!"));
        
        variant.setStock(newStock);
        variantRepo.save(variant);
    }

    public ProductVariant addVariant(Integer productId, String size, Integer stock) {
        // 1. Tìm sản phẩm cha
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

        // 2. Tạo SKU mới
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSize(size);
        variant.setStock(stock);

        // 3. Lưu và trả về
        return variantRepo.save(variant);
    }
    
    // 👇 NẾU THIẾU CẢ HÀM XÓA THÌ THÊM LUÔN:
    public void deleteVariant(Integer variantId) {
        if (variantRepo.existsById(variantId)) {
            variantRepo.deleteById(variantId);
        } else {
            throw new RuntimeException("Không tìm thấy SKU để xóa!");
        }
    }
}
