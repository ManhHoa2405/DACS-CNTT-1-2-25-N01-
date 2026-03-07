package com.example.clothes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.clothes.model.User;
import com.example.clothes.model.Order;
import com.example.clothes.model.OrderStatus;
import com.example.clothes.service.UserService;
import com.example.clothes.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class UserController {
   
    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepo;
    // --- ĐĂNG NHẬP & ĐĂNG KÝ ---

    @GetMapping("/account/login")
    public String ViewLogin(){
        return "account/login";
    }

    @GetMapping("/account/register")
    public String ViewRegister(){
        return "account/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/account/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "account/register";
        }
    }

    @PostMapping("/account/login") 
    public String loginUser(@RequestParam String email, 
                            @RequestParam String password, 
                            HttpSession session, 
                            Model model) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("currentUser", user);
            //kiểm tra Email phân quyềnfindByEmail
            if("admin@gmail.com".equalsIgnoreCase(user.getEmail())){
                return "redirect:/admin/dashboard"; 
            }else{
                return "redirect:/user/homePage"; 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "account/login";
        }
    }
    
    // --- QUÊN MẬT KHẨU ---

    @GetMapping("/account/forgot-password")
    public String viewForgotPassword() {
        return "account/forgot_password";
    }

    @PostMapping("/account/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            userService.updateResetToken(email);
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
            User user = userService.resetPassword(otp, password);
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
        
        // SỬA Ở ĐÂY: Trả về thư mục account/change_password vì file HTML nằm ở đó
        return "account/change_password"; 
    }

    // SỬA Ở ĐÂY: Sửa URL thành /user/change-password để khớp với file HTML
    @PostMapping("/user/change-password")
    public String processChangePassword(@RequestParam String oldPassword, 
                                        @RequestParam String newPassword, 
                                        @RequestParam String confirmPassword,
                                        HttpSession session, 
                                        RedirectAttributes redirectAttributes) { // Bỏ Model, dùng RedirectAttributes
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/account/login";

        try {
            if(!newPassword.equals(confirmPassword)){
                throw new Exception("Mật khẩu nhập lại không khớp!");
            }
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);
            
            // Nếu thành công, thông báo xanh ở trang chủ
           redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/user/change-password";
            
        } catch (Exception e) {
            // Nếu có lỗi, dùng flash báo đỏ và redirect lại trang đổi mk
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/change-password";
        }
    }
    
    // --- THÔNG TIN TÀI KHOẢN (PROFILE) ---

    @GetMapping("/user/profile")
    public String viewProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/account/login"; 
        }
        return "user/profile"; 
    }

    @PostMapping("/user/profile/update")
    public String updateProfile(@RequestParam String name, 
                                @RequestParam String phone, 
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/account/login";

        try {
            User updatedUser = userService.updateProfile(currentUser.getId(), name, phone);
            session.setAttribute("currentUser", updatedUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/user/profile";
    }

    // --- ĐĂNG XUẤT ---
    
    @GetMapping("/account/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/account/login";
    }


    @GetMapping("/user/orderProduct")
    public String viewOrderProduct(
        @RequestParam(required = false ) String status,
        HttpSession session,
        Model model
    ){
        // 1 kiểm tra đăng nhập 
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser == null){
            return "redirect:/account/login";
        }

        // 2 tabs 
        List<Order> orders;
        if(status != null && !status.isEmpty()){
            orders = orderRepo.findByUserAndStatusOrderByCreateAtDesc(currentUser, OrderStatus.valueOf(status));
        }else{
            orders = orderRepo.findByUserOrderByCreateAtDesc(currentUser);
        }

        // 3 truyền ra dữ liệu 
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);

        return "user/orderProduct";
    }

    @PostMapping("/user/order/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId, // Đã đổi Integer thành Long ở đây
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {
                                
        // 1. Kiểm tra đăng nhập
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/account/login";
        }

        // 2. Tìm đơn hàng trong Database (Giờ truyền Long vào sẽ hết báo lỗi đỏ)
        Order order = orderRepo.findById(orderId).orElse(null);

        // 3. Kiểm tra bảo mật: Đơn hàng phải tồn tại VÀ phải đúng là của user đang đăng nhập
        if (order != null && order.getUser().getId().equals(currentUser.getId())) {
            
            // 4. Kiểm tra điều kiện: Chỉ cho phép hủy nếu đang ở trạng thái PENDING
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepo.save(order);
                // Gửi thông báo thành công ra màn hình
                redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");
            } else {
                // Gửi thông báo lỗi nếu cố tình hack
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy vì đơn đã được xử lý!");
            }
        }

        // 5. Quay lại trang danh sách đơn hàng
        return "redirect:/user/orderProduct";
    }
        
}