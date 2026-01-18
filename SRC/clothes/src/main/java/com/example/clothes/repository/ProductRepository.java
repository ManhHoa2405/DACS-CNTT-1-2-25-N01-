package com.example.clothes.repository;
import com.example.clothes.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {
   // Thêm: LEFT JOIN FETCH p.category
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "LEFT JOIN FETCH p.category " +  // <--- THÊM DÒNG NÀY
           "WHERE p.name LIKE %:keyword%")
    List<Product> searchByNameWithVariants(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "LEFT JOIN FETCH p.category")    // <--- THÊM DÒNG NÀY
    List<Product> findAllWithVariants();


    // Câu lệnh lọc đa năng (Tên + Danh mục + Trạng thái) + Tối ưu tốc độ (JOIN FETCH)
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "LEFT JOIN FETCH p.category " +
           "WHERE (:keyword IS NULL OR p.name LIKE %:keyword%) " +
           "AND (:categoryName IS NULL OR p.category.name LIKE %:categoryName%) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Product> filterProducts(@Param("keyword") String keyword, 
                                 @Param("categoryName") String categoryName,
                                 @Param("status") Boolean status);
    Product findByid(Integer id);
}
