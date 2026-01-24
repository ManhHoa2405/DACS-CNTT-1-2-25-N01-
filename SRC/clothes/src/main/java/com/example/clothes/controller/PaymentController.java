package com.example.clothes.controller;

import com.example.clothes.model.CartItem;
import com.example.clothes.model.Order;
import com.example.clothes.model.ShippingAddress;
import com.example.clothes.model.User;
import com.example.clothes.repository.ShippingAddressRepository;
import com.example.clothes.service.CartService;
import com.example.clothes.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final CartService cartService;
    private final ShippingAddressRepository shippingAddressRepository;
    private final OrderService orderService; 

    // 1️⃣ HIỂN THỊ TRANG THANH TOÁN
    @GetMapping("/user/payment")
    public String getPaymentPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/account/login";

        List<CartItem> allItems = cartService.getCartItems(user);
        List<CartItem> selectedItems = allItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsSelected()))
                .toList();

        if (selectedItems.isEmpty()) return "redirect:/user/cart";

        BigDecimal total = selectedItems.stream()
                .map(item -> item.getProductVariant().getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy địa chỉ
        List<ShippingAddress> userAddresses = shippingAddressRepository.findByUser(user);
        ShippingAddress defaultAddress = userAddresses.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                .findFirst()
                .orElse(userAddresses.isEmpty() ? null : userAddresses.get(0));

        model.addAttribute("address", defaultAddress); 
        model.addAttribute("hasAddress", defaultAddress != null);
        model.addAttribute("userAddresses", userAddresses); 

        model.addAttribute("items", selectedItems);
        model.addAttribute("total", total);
        model.addAttribute("shippingFee", BigDecimal.ZERO); 
        model.addAttribute("grandTotal", total);

        return "user/payment"; 
    }

    // 2️⃣ TRANG THÔNG BÁO KẾT QUẢ
    @GetMapping("/user/paymentNoti")
    public String getPaymentNotiPage(@RequestParam("status") boolean status, Model model) {
        model.addAttribute("status", status);
        return "user/paymentNoti";
    }

    // 3️⃣ TRANG QUÉT QR (Nhận thêm Order ID)
    @GetMapping("/user/payment/qr")
    public String getQrPage(Model model, 
                            @RequestParam("method") String method,
                            @RequestParam("orderId") Long orderId) {
        model.addAttribute("method", method);
        model.addAttribute("orderId", orderId);
        return "user/payment_qr"; 
    }

    // 4️⃣ XỬ LÝ ĐẶT HÀNG
    @PostMapping("/user/payment/checkout")
    public String checkout(HttpSession session, @RequestParam("paymentMethod") String paymentMethod) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/account/login";

        // Gọi service tạo đơn
        Order createdOrder = orderService.placeOrder(user, paymentMethod);

        if (createdOrder == null) {
            return "redirect:/user/paymentNoti?status=false"; // Lỗi tạo đơn
        }

        // Nếu là Online -> Chuyển sang trang QR kèm Order ID
        if ("MOMO".equals(paymentMethod) || "ZALOPAY".equals(paymentMethod)) {
             return "redirect:/user/payment/qr?method=" + paymentMethod + "&orderId=" + createdOrder.getId();
        }

        // Nếu là Tiền mặt -> Redirect về Noti status=true (JS sẽ chặn để hiện modal)
        return "redirect:/user/paymentNoti?status=true";
    }

    // 5️⃣ XÁC NHẬN THANH TOÁN (Từ trang QR gọi vào)
    @GetMapping("/user/payment/confirm")
    public String confirmPayment(@RequestParam("orderId") Long orderId) {
        orderService.confirmPayment(orderId);
        return "redirect:/user/paymentNoti?status=true";
    }
}