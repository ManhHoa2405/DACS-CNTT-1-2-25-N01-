package com.example.clothes.repository;
import com.example.clothes.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    
}