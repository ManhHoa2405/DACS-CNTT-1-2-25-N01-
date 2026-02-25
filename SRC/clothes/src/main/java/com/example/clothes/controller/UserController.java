package com.example.clothes.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.clothes.model.User;
import com.example.clothes.service.UserService;

import jakarta.servlet.http.HttpSession;

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

    // --- QUÊN MẬT KHẨU ---

    @GetMapping("/account/forgot-password")
    public String viewForgotPassword() {
        return "account/forgot_password";
    }

    @PostMapping("/account/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            userService.updateResetToken(email);
            // Gửi mail thành công -> Chuyển thẳng sang trang nhập OTP
            return "redirect:/account/reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "account/forgot_password";
        }
    }

    @GetMapping("/account/reset-password")
    public String viewResetPassword(Model model) {
        return "account/reset_password";
    }

    @PostMapping("/account/reset-password")
    public String processResetPassword(@RequestParam String otp, 
                                       @RequestParam String password, 
                                       @RequestParam String confirmPassword, 
                                       HttpSession session, Model model) {
        try {
            if(!password.equals(confirmPassword)){
                throw new Exception("Mật khẩu nhập lại không khớp!");
            }
            // Đổi mật khẩu và lấy thông tin user
            User user = userService.resetPassword(otp, password);
            
            // Tự động đăng nhập luôn
            session.setAttribute("currentUser", user);
            return "redirect:/user/homePage";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "account/reset_password";
        }
    }

    // --- ĐỔI MẬT KHẨU (Dành cho user đã đăng nhập) ---

    @GetMapping("/user/change-password")
    public String viewChangePassword(HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/account/login";
        return "user/change_password"; // Giao diện đổi mật khẩu
    }

    @PostMapping("/user/change-password")
    public String processChangePassword(@RequestParam String oldPassword, 
                                        @RequestParam String newPassword, 
                                        @RequestParam String confirmPassword,
                                        HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/account/login";

        try {
            if(!newPassword.equals(confirmPassword)){
                throw new Exception("Mật khẩu nhập lại không khớp!");
            }
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);
            return "redirect:/user/homePage";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/change_password";
        }
    }
    
    // Đăng xuất
    @GetMapping("/account/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/account/login";
    }
    
}
