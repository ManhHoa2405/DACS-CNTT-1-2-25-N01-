package com.example.clothes.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.clothes.model.Cart;
import com.example.clothes.model.CartItem;
import com.example.clothes.model.ProductVariant;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant variant);

    List<CartItem> findByCart(Cart cart);
}
