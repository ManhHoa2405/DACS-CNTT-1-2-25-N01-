package com.example.clothes.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
@Entity
@Table(name = "shipping_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receive_name", nullable = false, length = 100)
    private String receiveName;

    @Column(name = "receive_phone", nullable = false, length = 20)
    private String receivePhone;
    
    @Column(length = 255, nullable = false)
    private String province;

    @Column(length = 255, nullable = false)
    private String district;

    @Column(length = 255, nullable = false)
    private String ward;

    @Column(name = "address_detail", length = 255, nullable = false)
    private String addressDetail;

    @Column(name = "is_default")
    private Boolean isDefault;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
