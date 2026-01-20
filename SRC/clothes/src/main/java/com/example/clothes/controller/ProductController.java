package com.example.clothes.controller;

import com.example.clothes.model.Product;
import com.example.clothes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Đường dẫn: http://localhost:8080/user/showProduct
     * Nhiệm vụ: Lấy danh sách sản phẩm từ Database và hiển thị lên file shop.html
     */
    // @GetMapping("/user/showProduct")
    // public String showProductPage(Model model, 
    //                               @RequestParam(name = "keyword", required = false) String keyword,
    //                               @RequestParam(name = "categoryName", required = false) String categoryName
    //                             ) {
        
    //     // 1. Gọi Service để lấy danh sách sản phẩm
    //     // (Nếu có keyword thì tìm kiếm, không thì lấy tất cả - logic này nằm trong Service bạn đã gửi)
    //     // List<Product> products = productService.getAllProducts(keyword);
    //    List<Product> products = productService.getProductsForUser(keyword, categoryName);

    //     // 2. Đưa list sản phẩm vào Model với tên biến là "products"
    //     // Bên HTML sẽ hứng bằng: th:each="p : ${products}"
    //     model.addAttribute("products", products);

    //     // 3. (Tùy chọn) Gửi lại keyword ra view để giữ lại text trong ô tìm kiếm
    //     model.addAttribute("keyword", keyword);
    //     model.addAttribute("currentCategory", categoryName);

    //     // 4. Trả về tên file view (shop.html nằm trong thư mục templates)
    //     // Lưu ý: Không cần đuôi .html, Thymeleaf tự hiểu

    //     List<Product> latestList = productService.getLatestProducts();
    //     model.addAttribute("latestProducts", latestList);
    //     return "/user/showProduct"; 
    // }

    @GetMapping("/user/showProduct")
    public String showProductPage(Model model, 
                                @RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "categoryName", required = false) String categoryName,
                                // 👇 THÊM 2 THAM SỐ NÀY
                                @RequestParam(name = "size", required = false) String size,
                                @RequestParam(name = "sort", required = false) String sort) {
        
        List<Product> products = productService.getProductsForUser(keyword, categoryName, size, sort);

        model.addAttribute("products", products);
        model.addAttribute("latestProducts", productService.getLatestProducts());

        // 👇 Gửi lại các tham số ra View để giữ trạng thái (Active)
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentCategory", categoryName);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);

        return "/user/showProduct";
    }
}