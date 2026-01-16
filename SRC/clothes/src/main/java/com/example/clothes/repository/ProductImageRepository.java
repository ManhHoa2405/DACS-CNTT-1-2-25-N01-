package com.example.clothes.repository;
import com.example.clothes.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
}
