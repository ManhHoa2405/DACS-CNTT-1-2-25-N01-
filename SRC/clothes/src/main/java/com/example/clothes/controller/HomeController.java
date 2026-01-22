package com.example.clothes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        // Kiểm tra xem đã đăng nhập chưa (nếu cần bảo mật)
        // if (session.getAttribute("currentUser") == null) {
        //     return "redirect:/account/login"; // Chưa đăng nhập thì đá về login
        // }
        
        
        // Trả về file templates/user/homePage.html

        // 1. Lấy danh sách ÁO
    List<Product> listAo = productService.getProductsForUser(null, "Áo", null, null);
    if(listAo.size() > 3) listAo = listAo.subList(0, 3); // Chỉ lấy 4 cái
    model.addAttribute("listAo", listAo);
        
    
    List<Product> listQuan = productService.getProductsForUser(null, "Quần", null, null);
    if(listQuan.size() > 3) listQuan = listQuan.subList(0, 3);
    model.addAttribute(" listQuan", listQuan);

    List<Product> lisChanVay = productService.getProductsForUser(null, "Chân Váy", null, null);
    if(lisChanVay.size() > 3) lisChanVay = lisChanVay.subList(0, 3);
    model.addAttribute("lisChanVay", lisChanVay);

    List<Product> listDam = productService.getProductsForUser(null, "Đầm", null, null);
    if(listDam.size() > 3) listDam = listDam.subList(0, 3);
    model.addAttribute("listDam",listDam);

    return "user/homePage"; 
    }

    // 
}
