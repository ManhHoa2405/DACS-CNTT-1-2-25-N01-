package com.example.clothes.repository;
import com.example.clothes.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Thêm dòng này để tìm theo tên
    Category findByName(String name);
}
