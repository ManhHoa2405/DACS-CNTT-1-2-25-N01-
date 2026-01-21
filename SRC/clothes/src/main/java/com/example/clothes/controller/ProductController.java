package com.example.clothes.controller;

import com.example.clothes.model.Product;
import com.example.clothes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;


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

    @GetMapping("/user/detailProduct/{id}")
    public String viewDetailProduct(Model model,@RequestParam(name="categoryName", required = false) String categoryName, @PathVariable("id") Integer id){
        Product product = productService.getProductById(id);
        // 2. Gửi sản phẩm sang View
        model.addAttribute("p", product);

        // 3. (Tùy chọn) Lấy danh sách sản phẩm liên quan (Cùng danh mục)
        List<Product> relatedProducts = productService.getRelatedProducts(product.getCategory().getId(), id);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("currentCategory", categoryName);
        return "/user/detailProduct";
    }
}