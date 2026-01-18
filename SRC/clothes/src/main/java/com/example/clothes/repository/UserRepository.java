package com.example.clothes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clothes.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findByEmail(String email);
}