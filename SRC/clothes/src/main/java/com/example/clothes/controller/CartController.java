package com.example.clothes.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.example.clothes.model.CartItem;
import com.example.clothes.model.User;
import com.example.clothes.service.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/cart")
public class CartController {

    private final CartService cartService;

    // 1️⃣ XEM GIỎ HÀNG
    @GetMapping
    public String viewCart(Model model, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/account/login";
        }

        // --- XỬ LÝ LINK "TRỞ LẠI" ---
        String referrer = request.getHeader("Referer");
        
        if (referrer != null && !referrer.contains("/user/cart") && !referrer.contains("/account")) {
            session.setAttribute("backLink", referrer);
        }
        
        if (session.getAttribute("backLink") == null) {
            session.setAttribute("backLink", "/user/homePage");
        }
        
        model.addAttribute("backLink", session.getAttribute("backLink"));
        // -----------------------------

        List<CartItem> items = cartService.getCartItems(user);

        BigDecimal total = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsSelected()))  
                .map(item ->
                        item.getProductVariant()
                            .getProduct()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "user/cart";
    }

    // 2️⃣ THÊM VÀO GIỎ HÀNG (SỬA ĐỔI ĐỂ TRẢ VỀ JSON)
    @PostMapping("/add")
    @ResponseBody // Quan trọng: Trả về JSON để JS xử lý, không chuyển trang
    public ResponseEntity<?> addToCart(
            @RequestParam("variantId") Integer variantId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        
        // Nếu chưa đăng nhập, trả về mã lỗi 401 để JS bắt và chuyển hướng
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized"); 
        }

        try {
            cartService.addToCart(user.getEmail(), variantId, quantity);
            
            // Trả về JSON thông báo thành công
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Thêm vào giỏ hàng thành công!");
            
            // Nếu bạn muốn update số lượng trên icon giỏ hàng ngay lập tức:
            // response.put("cartCount", cartService.getCartItems(user).size());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 3️⃣ CẬP NHẬT SỐ LƯỢNG (Tăng/Giảm/Xóa)
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("cartItemId") Long cartItemId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        if (session.getAttribute("currentUser") == null) {
            return "redirect:/account/login";
        }

        cartService.updateQuantity(cartItemId, quantity);
        
        return "redirect:/user/cart";
    }

    // 4️⃣ XỬ LÝ CHECKBOX (CHỌN MUA)
    @PostMapping("/select")
    @ResponseBody 
    public ResponseEntity<?> selectCartItem(
            @RequestParam("cartItemId") Long cartItemId,
            @RequestParam(value = "selected", defaultValue = "false") boolean selected,
            HttpSession session) {

        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        cartService.updateSelectStatus(cartItemId, selected);

        return ResponseEntity.ok().body("Success");
    }
}