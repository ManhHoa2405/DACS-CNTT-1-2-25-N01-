package com.example.clothes.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.clothes.model.Cart;
import com.example.clothes.model.CartItem;
import com.example.clothes.model.ProductVariant;
import com.example.clothes.model.User;
import com.example.clothes.repository.CartItemRepository;
import com.example.clothes.repository.CartRepository;
import com.example.clothes.repository.ProductVariantRepository;
import com.example.clothes.repository.UserRepository; // 1. Import UserRepository

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository; // 2. Inject UserRepository

    // 1️⃣ Lấy hoặc tạo cart (Giữ nguyên logic nhận User Object)
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            });
    }

    // 2️⃣ Thêm sản phẩm (SỬA Ở ĐÂY)
    @Transactional
    public void addToCart(String email, Integer variantId, int quantity) {
        // B1: Tìm User từ Email
        User user = userRepository.findByEmail(email);

        // B2: Gọi hàm lấy giỏ hàng (truyền User object vào)
        Cart cart = getOrCreateCart(user);

        ProductVariant variant = productVariantRepository
                .findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        CartItem item = cartItemRepository
                .findByCartAndProductVariant(cart, variant)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductVariant(variant);
            item.setQuantity(quantity);
            item.setIsSelected(true);
        } else {    
            item.setQuantity(item.getQuantity() + quantity);
        }

        cartItemRepository.save(item);
    }

    // ... (Các hàm getCartItems, updateQuantity, toggleSelect giữ nguyên)
    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCart(cart);
    }

    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }
    
    public void updateSelectStatus(Long cartItemId, boolean selected) {
       CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();
       item.setIsSelected(selected);
       cartItemRepository.save(item);
    }
}