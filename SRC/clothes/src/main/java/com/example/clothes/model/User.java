package com.example.clothes.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.CascadeType;
import com.example.clothes.model.ShippingAddress;
import com.example.clothes.model.Order;
import com.example.clothes.model.Cart;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // quan he voi cac entity khac (neu co) se duoc them o day
    // ví dụ quan hệ 1-n với ShippingAddress
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ShippingAddress> shippingAddresses;

    // ví dụ quan hệ 1-n với Order
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Order> orders;

    // quan hệ 1-1 với Cart
    @OneToOne(mappedBy = "user")
    private Cart cart;

}
