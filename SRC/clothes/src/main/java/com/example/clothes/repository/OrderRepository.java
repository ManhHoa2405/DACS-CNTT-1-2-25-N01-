package com.example.clothes.repository;

import com.example.clothes.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.clothes.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUser(User user);
}