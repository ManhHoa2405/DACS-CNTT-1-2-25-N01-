package com.example.clothes.repository;

import com.example.clothes.model.Order;
import com.example.clothes.model.OrderStatus;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // tính tổng đơn hàng theo trạng thái status
    long countByStatus(OrderStatus status);
    //  lấy tất cả đơn hàng , sắp xếp theo thời gian 
    List<Order> findAllByOrderByCreateAtDesc();
    List<Order> findByStatusOrderByCreateAtDesc(OrderStatus status);
    // tìm kiếm đơn hàng theo tên người nhận hoặc số điện thoại
    // Thay thế hàm tìm kiếm cũ bằng hàm này:
List<Order> findByReceiveNameContainingOrReceivePhoneContainingOrderByCreateAtDesc(String receiveName, String receivePhone);
}