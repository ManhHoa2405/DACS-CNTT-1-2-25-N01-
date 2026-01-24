package com.example.clothes.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import com.example.clothes.model.CartItem;
import com.example.clothes.model.User;
import com.example.clothes.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/cart")
public class CartController {

    private final CartService cartService;

    // 1️⃣ XEM GIỎ HÀNG
    // 1️⃣ XEM GIỎ HÀNG
    @GetMapping
    public String viewCart(Model model, HttpSession session, HttpServletRequest request) { // Thêm HttpServletRequest

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/account/login";
        }

        // --- XỬ LÝ LINK "TRỞ LẠI" ---
        String referrer = request.getHeader("Referer");
        String currentBackLink = (String) session.getAttribute("backLink");

        // Logic: Chỉ cập nhật link Back nếu người dùng đến từ trang khác (không phải reload chính trang cart)
        // Và không lấy link từ trang login/register
        if (referrer != null && !referrer.contains("/user/cart") && !referrer.contains("/account")) {
            session.setAttribute("backLink", referrer);
        }
        
        // Nếu không có link nào trong session (ví dụ gõ trực tiếp URL), mặc định về trang chủ
        if (session.getAttribute("backLink") == null) {
            session.setAttribute("backLink", "/user/homePage");
        }
        
        // Đẩy link ra view
        model.addAttribute("backLink", session.getAttribute("backLink"));
        // -----------------------------

        // Lấy danh sách cart item (Code cũ của bạn)
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

    // 2️⃣ THÊM VÀO GIỎ HÀNG
    @PostMapping("/add")
    public String addToCart(
            @RequestParam("variantId") Integer variantId,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/account/login";
        }

        try {
            cartService.addToCart(user.getEmail(), variantId, quantity);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Thêm vào giỏ hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/user/cart";
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

        // Nếu quantity = 0 thì service tự động xóa
        cartService.updateQuantity(cartItemId, quantity);
        
        return "redirect:/user/cart";
    }

    // 4️⃣ XỬ LÝ CHECKBOX (CHỌN MUA) -> SỬA LỖI 404 CỦA BẠN
    @PostMapping("/select")
    @ResponseBody // Quan trọng: Trả về dữ liệu text thay vì file HTML
    public ResponseEntity<?> selectCartItem(
            @RequestParam("cartItemId") Long cartItemId,
            @RequestParam(value = "selected", defaultValue = "false") boolean selected,
            HttpSession session) {

        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Gọi service cập nhật DB
        cartService.updateSelectStatus(cartItemId, selected);

        // Trả về OK để JS biết đã thành công
        return ResponseEntity.ok().body("Success");
    }
}