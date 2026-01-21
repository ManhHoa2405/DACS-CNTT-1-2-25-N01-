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
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.ArrayList;
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



    // lấy toàn bộ sản phẩm
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
    

    
    //  NẾU THIẾU CẢ HÀM XÓA THÌ THÊM LUÔN:
    public void deleteVariant(Integer variantId) {
        if (variantRepo.existsById(variantId)) {
            variantRepo.deleteById(variantId);
        } else {
            throw new RuntimeException("Không tìm thấy SKU để xóa!");
        }
    }

    // Trong ProductService.java
    @Transactional // Quan trọng: Đảm bảo lưu DB và lưu file đồng bộ
    public void updateProductInfoWithFiles(Integer id, String name, Double price, Boolean status, List<MultipartFile> imageFiles) throws IOException {
        // 1. Tìm sản phẩm
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SP"));

        // 2. Cập nhật thông tin cơ bản
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));
        p.setStatus(status);

        // 3. XỬ LÝ ẢNH UPLOAD (Nếu có chọn ảnh mới)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            
            // A. Xóa sạch ảnh cũ (orphanRemoval=true trong Model sẽ lo việc xóa trong DB)
            if (p.getImages() != null) {
                p.getImages().clear();
            } else {
                p.setImages(new ArrayList<>());
            }

            // B. Chuẩn bị thư mục lưu ảnh (dựa theo tên sản phẩm)
            String productSlug = toSlug(p.getName());
            Path uploadPath = Paths.get(BASE_UPLOAD_DIR + productSlug);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // C. Duyệt và lưu từng file ảnh mới
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                    
                    // Lưu file vật lý vào ổ cứng server
                    try (InputStream inputStream = file.getInputStream()) {
                        Path filePath = uploadPath.resolve(originalFilename);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                         throw new IOException("Lỗi lưu file: " + originalFilename, e);
                    }

                    // Tạo đối tượng ảnh mới và thêm vào danh sách của sản phẩm
                    ProductImage newImg = new ProductImage();
                    newImg.setProduct(p);
                    // Tạo đường dẫn web để truy cập ảnh sau này
                    newImg.setImageUrl("/images/products/" + productSlug + "/" + originalFilename);
                    
                    p.getImages().add(newImg);
                }
            }
        }

        // 4. Lưu sản phẩm (Hibernate sẽ tự động lưu các ảnh mới)
        productRepo.save(p);
    }
    
    // Hàm xóa sản phẩm
    public void deleteProduct(Integer id) {
        if (productRepo.existsById(id)) {
            // Lưu ý: Nếu Database có ràng buộc (Foreign Key), bạn cần cài đặt CascadeType.ALL 
            // trong Entity Product hoặc xóa variants bằng tay trước.
            productRepo.deleteById(id);
        } else {
            throw new RuntimeException("K tim thay SP");
        }
    }

    public List<Product> getProductsForUser(String keyword, String categoryName, String size, String sortType) {
    // 1. Xử lý sắp xếp
        Sort sort = Sort.unsorted(); // Mặc định không sắp xếp
        
        if (sortType != null) {
            switch (sortType) {
                case "price_asc":
                    sort = Sort.by("price").ascending(); // Giá tăng dần
                    break;
                case "price_desc":
                    sort = Sort.by("price").descending(); // Giá giảm dần
                    break;
                case "newest":
                    sort = Sort.by("id").descending(); // ID lớn nhất = Mới nhất
                    break;
            }
        }

        // 2. Gọi Repository
        return productRepo.filterProductsUser(keyword, categoryName, size, sort);
    }
    // hiển thị cho bên chi tiết sp 
    public List<Product> getLatestProducts() {
    return productRepo.findTop2ByOrderByIdDesc();
    }
    

    public Product getProductById(Integer id) {
        // return productRepo.findProductWithDetails(id);
        Product p = productRepo.findProductWithDetails(id);
        
        // Kích hoạt tải dữ liệu (chỉ tốn thêm 2 câu lệnh SQL siêu nhẹ)
        if(p != null) {
            p.getVariants().size();
            p.getImages().size();
        }
        return p;
    }

    public List<Product> getRelatedProducts(Integer categoryId, Integer currentProductId) {
        return productRepo.findByCategoryIdAndIdNot(categoryId, currentProductId)
                          .stream().limit(4).toList();
    }
}
