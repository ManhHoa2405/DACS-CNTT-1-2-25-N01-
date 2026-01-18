package com.example.clothes.controller;
import com.example.clothes.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.example.clothes.model.User;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
   
    @Autowired
    private UserService userService;

    // hiển thị đăng nhập
    @GetMapping("/account/login")
    public String ViewLogin(){
        return "account/login";
    }

    // hiển thị đăng ký
    @GetMapping("/account/register")
    public String ViewRegister(){
        return "account/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            // Lúc này 'user' đã có: name, phone, email, password (thô)
            // Ô nhập lại mật khẩu đã bị loại bỏ ngay từ HTML nên không ảnh hưởng gì
            
            userService.registerUser(user);
            
            return "redirect:account/login"; // Đăng ký xong chuyển qua đăng nhập
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage()); // Gửi lỗi về lại form (ví dụ: Email trùng)
            return "account/register";
        }
    }

    @PostMapping("/account/login") 
    public String loginUser(@RequestParam String email, 
                            @RequestParam String password, 
                            HttpSession session, 
                            Model model) {
        try {
            // Gọi Service kiểm tra
            User user = userService.login(email, password);

            // Đăng nhập thành công -> Lưu vào Session
            session.setAttribute("currentUser", user);

            // Chuyển hướng về trang chủ (hoặc trang admin tùy role)
            return "redirect:/user/homePage"; 

        } catch (Exception e) {
            e.printStackTrace();
            // Đăng nhập thất bại -> Báo lỗi ra view
            model.addAttribute("error", e.getMessage());
            return "account/login";
        }
    }
    
    // Hứng đường dẫn localhost:8080/user/homePage
    @GetMapping("/user/homePage")
    public String showHomePage(HttpSession session, Model model) {
        // Kiểm tra xem đã đăng nhập chưa (nếu cần bảo mật)
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/account/login"; // Chưa đăng nhập thì đá về login
        }
        
        // Trả về file templates/user/homePage.html
        return "user/homePage"; 
    }
    // Đăng xuất
    @GetMapping("/account/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/account/login";
    }
    
}
