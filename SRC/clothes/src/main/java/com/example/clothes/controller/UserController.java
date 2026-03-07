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
import com.example.clothes.service.OrderService;
import com.example.clothes.service.UserService;
import com.example.clothes.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class UserController {
   
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

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

    // --- ĐỔI MẬT KHẨU ---

    @GetMapping("/user/change-password")
    public String viewChangePassword(HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/account/login";
        return "account/change_password"; 
    }

    @PostMapping("/user/change-password")
    public String processChangePassword(@RequestParam String oldPassword, 
                                        @RequestParam String newPassword, 
                                        @RequestParam String confirmPassword,
                                        HttpSession session, 
                                        RedirectAttributes redirectAttributes) { 
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/account/login";

        try {
            if(!newPassword.equals(confirmPassword)){
                throw new Exception("Mật khẩu nhập lại không khớp!");
            }
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/user/change-password";
            
        } catch (Exception e) {
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

    // ========================================================
    // PHẦN XỬ LÝ ĐƠN HÀNG (ĐÃ GỘP CODE CỦA BẠN VÀ NHÓM CHUẨN XÁC)
    // ========================================================

    // 1. Quản lý đơn hàng cũ (của bạn) - Trả về toàn bộ danh sách để Javascript xử lý phân trang
    @GetMapping("/user/purchase")
    public String viewPurchaseHistory(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/account/login";
        }
        List<Order> listOrders = orderService.findByUser(currentUser); 
        model.addAttribute("listOrders", listOrders);
        return "user/purchase"; 
    }

    // 2. Hủy đơn hàng cũ (của bạn)
    @PostMapping("/user/purchase/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(orderId);
            redirectAttributes.addFlashAttribute("cancelSuccess", true);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/purchase"; 
    }

    // 3. Quản lý đơn hàng mới (của bạn cùng nhóm)
    @GetMapping("/user/orderProduct")
    public String viewOrderProduct(
        @RequestParam(required = false ) String status,
        HttpSession session,
        Model model
    ){
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser == null){
            return "redirect:/account/login";
        }

        List<Order> orders;
        if(status != null && !status.isEmpty()){
            orders = orderRepo.findByUserAndStatusOrderByCreateAtDesc(currentUser, OrderStatus.valueOf(status));
        }else{
            orders = orderRepo.findByUserOrderByCreateAtDesc(currentUser);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);

        return "user/orderProduct";
    }

    // 4. Hủy đơn hàng mới (của bạn cùng nhóm)
    @PostMapping("/user/order/cancel")
    public String cancelOrderTeam(@RequestParam("orderId") Long orderId, 
                                  HttpSession session, 
                                  RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/account/login";
        }

        Order order = orderRepo.findById(orderId).orElse(null);

        if (order != null && order.getUser().getId().equals(currentUser.getId())) {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepo.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy vì đơn đã được xử lý!");
            }
        }
        return "redirect:/user/orderProduct";
    }
}