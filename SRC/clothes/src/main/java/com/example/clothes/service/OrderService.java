package com.example.clothes.service;

import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ShippingAddressRepository shippingAddressRepository; 

    // 1. Hàm tạo đơn hàng
    @Transactional
    public Order placeOrder(User user, String paymentMethod) {
        try {
            // ... (Phần lấy cartItems, tính tiền, tạo Order, Address giữ nguyên) ...
            List<CartItem> cartItems = cartService.getCartItems(user).stream()
                    .filter(item -> Boolean.TRUE.equals(item.getIsSelected()))
                    .toList();

            if (cartItems.isEmpty()) return null;

            BigDecimal totalAmount = cartItems.stream()
                    .map(item -> item.getProductVariant().getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = new Order();
            order.setUser(user);
            order.setCreateAt(LocalDateTime.now());
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.PENDING);

            List<ShippingAddress> addresses = shippingAddressRepository.findByUser(user);
            ShippingAddress shipAddr = addresses.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                    .findFirst()
                    .orElse(addresses.isEmpty() ? null : addresses.get(0));

            if (shipAddr != null) {
                order.setReceiveName(shipAddr.getReceiveName());
                order.setReceivePhone(shipAddr.getReceivePhone());
                order.setProvince(shipAddr.getProvince());
                order.setDistrict(shipAddr.getDistrict());
                order.setWard(shipAddr.getWard());
                order.setAddressDetail(shipAddr.getAddressDetail());
            } else {
                return null;
            }

            Order savedOrder = orderRepository.save(order);

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProductVariant(cartItem.getProductVariant());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getProductVariant().getProduct().getPrice());
                orderItemRepository.save(orderItem);
            }

            Payment payment = new Payment();
            payment.setOrder(savedOrder);
            payment.setMethod(paymentMethod);
            payment.setStatus("PENDING"); 
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // --- SỬA LỖI Ở ĐÂY ---
            // Chỉ xóa giỏ hàng NẾU là Tiền Mặt (CASH). 
            // Nếu là Online, giữ lại để phòng trường hợp thanh toán thất bại/hủy.
            if ("CASH".equals(paymentMethod)) {
                cartItemRepository.deleteAll(cartItems);
            }

            return savedOrder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Hàm xác nhận thanh toán (Khi bấm nút "Tôi đã thanh toán")
    @Transactional // Thêm Transactional để đảm bảo tính nhất quán
    public void confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order != null) {
            // 1. Cập nhật trạng thái thanh toán
            if (order.getPayment() != null) {
                Payment payment = order.getPayment();
                payment.setStatus("COMPLETED");
                paymentRepository.save(payment);
            }
            
            // 2. Xóa sản phẩm khỏi giỏ hàng TẠI THỜI ĐIỂM NÀY
            // Lưu ý: Cần lấy lại cart items tương ứng với order items
            // Hoặc đơn giản là xóa các item đang được chọn trong giỏ của user này
            User user = order.getUser();
            List<CartItem> selectedItems = cartService.getCartItems(user).stream()
                    .filter(item -> Boolean.TRUE.equals(item.getIsSelected()))
                    .toList();
            
            cartItemRepository.deleteAll(selectedItems);
        }
    }
}