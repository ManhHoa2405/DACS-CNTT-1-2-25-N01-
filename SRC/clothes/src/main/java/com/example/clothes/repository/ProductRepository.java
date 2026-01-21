package com.example.clothes.repository;
import com.example.clothes.model.Product;

import java.util.List;
import org.springframework.data.domain.Sort;
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
    // 4. HÀM LỌC CHO USER (Đã sửa lỗi MultipleBagFetchException)
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants v " + // ✅ GIỮ FETCH Ở ĐÂY (Vì cần lọc size và hiển thị size)
           "LEFT JOIN p.images i " +         // ❌ BỎ CHỮ 'FETCH' Ở ĐÂY ĐI (Chỉ để LEFT JOIN thôi)
           "WHERE (:keyword IS NULL OR p.name LIKE %:keyword%) " +
           "AND (:categoryName IS NULL OR p.category.name LIKE %:categoryName%) " +
           "AND (:size IS NULL OR v.size = :size) " + 
           "AND p.status = true") 
    List<Product> filterProductsUser(@Param("keyword") String keyword, 
                                     @Param("categoryName") String categoryName,
                                     @Param("size") String size,
                                     Sort sort);

    // Kiểm tra luôn hàm ADMIN, nếu có 2 cái FETCH thì cũng sửa tương tự:
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +   // ✅ Giữ FETCH Variants
           "LEFT JOIN p.images " +           // ❌ BỎ FETCH Images
           "LEFT JOIN FETCH p.category " +
           "WHERE (:keyword IS NULL OR p.name LIKE %:keyword%) " +
           "AND (:categoryName IS NULL OR p.category.name LIKE %:categoryName%) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Product> filterProducts(@Param("keyword") String keyword, 
                                 @Param("categoryName") String categoryName,
                                 @Param("status") Boolean status);
    Product findByid(Integer id);
    List<Product> findTop2ByOrderByIdDesc();

//    @Query("SELECT p FROM Product p " +
//            "LEFT JOIN  p.variants " + 
//            "LEFT JOIN p.images " + // <--- BỎ CHỮ 'FETCH' Ở ĐÂY
//            "WHERE p.id = :id")
//     Product findProductWithDetails(@Param("id") Integer id);

       @Query("SELECT p FROM Product p WHERE p.id = :id")
       Product findProductWithDetails(@Param("id") Integer id);

    List<Product> findByCategoryIdAndIdNot(Integer categoryId, Integer id);
}
