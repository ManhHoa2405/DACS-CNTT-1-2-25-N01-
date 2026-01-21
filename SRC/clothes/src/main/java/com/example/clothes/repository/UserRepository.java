package com.example.clothes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.clothes.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findByEmail(String email);  

    @Query("SELECT u FROM User u WHERE " +
           "u.name LIKE %?1% OR " +
           "u.email LIKE %?1% OR " +
           "u.phone LIKE %?1%")
    List<User> searchUserByKeyword(String keyword);
}
