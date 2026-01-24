package com.example.clothes.model;

import java.math.BigDecimal;

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
import com.example.clothes.model.Cart;
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne
   @JoinColumn(name = "cart_id", nullable = false)
   private Cart cart;

   @ManyToOne
   @JoinColumn(name = "product_variant_id", nullable = false)
   private ProductVariant productVariant;
  @Column(nullable = false)
private Integer quantity;


   @Column(name = "is_selected")
   private Boolean isSelected;

}
