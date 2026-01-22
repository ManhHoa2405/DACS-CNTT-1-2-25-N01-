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
    // List<Product> listAo = productService.getProductsForUser(null, "Áo", null, null);
    // if(listAo.size() > 3) listAo = listAo.subList(0, 3); // Chỉ lấy 4 cái
    // model.addAttribute("listAo", listAo);
        
    
    // List<Product> listQuan = productService.getProductsForUser(null, "Quần", null, null);
    // if(listQuan.size() > 3) listQuan = listQuan.subList(0, 3);
    // model.addAttribute("listQuan",listQuan);

    // List<Product> lisChanVay = productService.getProductsForUser(null, "Chân Váy", null, null);
    // if(lisChanVay.size() > 3) lisChanVay = lisChanVay.subList(0, 3);
    // model.addAttribute("lisChanVay", lisChanVay);

    // List<Product> listDam = productService.getProductsForUser(null, "Đầm", null, null);
    // if(listDam.size() > 3) listDam = listDam.subList(0, 3);
    // model.addAttribute("listDam",listDam);

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
    // 
}
