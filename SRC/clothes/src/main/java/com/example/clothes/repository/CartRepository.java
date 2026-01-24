package com.example.clothes.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.clothes.model.Cart;
import com.example.clothes.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
