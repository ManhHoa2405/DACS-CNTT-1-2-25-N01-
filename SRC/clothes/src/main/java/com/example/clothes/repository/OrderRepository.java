package com.example.clothes.repository;

import com.example.clothes.model.Order;
import com.example.clothes.model.OrderStatus;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // tính tổng đơn hàng theo trạng thái status
    long countByStatus(OrderStatus status);

    // long sumByStatus(OrderStatus status);
    // Tính tổng doanh thu của tất cả đơn hàng có trạng thái đã giao hàng (DELIVERED)
    @Query("SELECT SUM(o.totalAmount) FROM Order o Where o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
    // đếm số lượng khách hàng đã đặt hàng có trạng thái đã giao hàng (DELIVERED)
    @Query("SELECT COUNT(DISTINCT o.user) FROM Order o WHERE o.status = :status")
    long countDistinctUsersByStatus(@Param("status") OrderStatus status);

    //
    @Query("SELECT SUM(oi.quantity) FROM Order o JOIN o.orderItems oi WHERE o.status = :status")
    Long sumProductQuanityByStatus(@Param("status") OrderStatus status);




    //  lấy tất cả đơn hàng , sắp xếp theo thời gian 
    List<Order> findAllByOrderByCreateAtDesc();
    List<Order> findByStatusOrderByCreateAtDesc(OrderStatus status);
    // tìm kiếm đơn hàng theo tên người nhận hoặc số điện thoại
    // Thay thế hàm tìm kiếm cũ bằng hàm này:
    List<Order> findByReceiveNameContainingOrReceivePhoneContainingOrderByCreateAtDesc(String receiveName, String receivePhone);


    // phân trang 
    Page<Order> findAllByOrderByCreateAtDesc(Pageable pageable);
}