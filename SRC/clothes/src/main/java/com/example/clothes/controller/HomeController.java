package com.example.clothes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.clothes.model.Product;
import com.example.clothes.service.ProductService;

import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;
    @GetMapping("/user/homePage")
    public String showHomePage(HttpSession session, Model model) {
 

    // 1. Lấy Top 3 Áo mới nhất (Truyền tham số "newest" vào sort)
        model.addAttribute("listAo", getTop3Products("Áo"));

        // 2. Lấy Top 3 Quần mới nhất
        model.addAttribute("listQuan", getTop3Products("Quần"));

        // 3. Lấy Top 3 Chân Váy mới nhất
        model.addAttribute("listChanVay", getTop3Products("Chân váy")); // Sửa lỗi chính tả "Chân Váy" cho khớp DB

        // 4. Lấy Top 3 Đầm mới nhất
        model.addAttribute("listDam", getTop3Products("Đầm"));

    return "user/homePage"; 
    }
    private List<Product> getTop3Products(String categoryName) {
        // Tham số thứ 4 là "newest" (để báo hiệu Service cần sắp xếp)
        List<Product> products = productService.getProductsForUser(null, categoryName, null, "newest");
        
        // Dùng Stream API để lấy 3 cái đầu tiên một cách an toàn
        // (Không lo lỗi IndexOutOfBounds nếu list chỉ có 1 hoặc 2 sản phẩm)
        return products.stream().limit(3).toList();
    }
    @GetMapping("/user/searchProduct")
        public String searchProduct(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "categoryName", required = false) String categoryName,
             @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "sort", required = false) String sort,
            Model model
        ){
            // List<Product> searchResults = productService.getAllProducts(keyword);
    
            List<Product> searchResults = productService.getProductsForUser(keyword, categoryName, size, sort);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("keyword", keyword); 
            return "user/searchProduct";
        }
    // 
}
