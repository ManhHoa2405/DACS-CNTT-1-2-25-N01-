package com.example.clothes.repository;

import com.example.clothes.model.Order;
import com.example.clothes.model.OrderStatus;
import com.example.clothes.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Của bạn:
    List<Order> findByUser(User user);

    // Lấy đơn hàng của User có phân trang (5 đơn/trang)
    Page<Order> findByUserOrderByCreateAtDesc(User user, Pageable pageable);

    // Của bạn cùng nhóm (dành cho Admin):
    long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o Where o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT COUNT(DISTINCT o.user) FROM Order o WHERE o.status = :status")
    long countDistinctUsersByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(oi.quantity) FROM Order o JOIN o.orderItems oi WHERE o.status = :status")
    Long sumProductQuanityByStatus(@Param("status") OrderStatus status);

    List<Order> findAllByOrderByCreateAtDesc();
    
    List<Order> findByStatusOrderByCreateAtDesc(OrderStatus status);
    
    List<Order> findByReceiveNameContainingOrReceivePhoneContainingOrderByCreateAtDesc(String receiveName, String receivePhone);

    Page<Order> findAllByOrderByCreateAtDesc(Pageable pageable);

    List<Order> findByUserOrderByCreateAtDesc(User user);

    List<Order> findByUserAndStatusOrderByCreateAtDesc(User user, OrderStatus status);
}