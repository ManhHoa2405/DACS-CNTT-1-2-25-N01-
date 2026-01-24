package com.example.clothes.repository;

import com.example.clothes.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.clothes.model.User;

import java.util.List;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
        // Thêm dòng này để sửa lỗi "The method findByUser(User) is undefined"
    List<ShippingAddress> findByUser(User user);
}