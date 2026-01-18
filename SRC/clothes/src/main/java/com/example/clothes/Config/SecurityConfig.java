package com.example.clothes.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Tạo Bean cho PasswordEncoder (Đây chính là cái máy băm mật khẩu)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình quyền truy cập (Cho phép mọi người vào trang đăng ký/đăng nhập)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt bảo mật CSRF để test cho dễ
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll() // Cho phép truy cập TẤT CẢ các link (không cần đăng nhập)
            )
            .formLogin(login -> login.disable()) // Tắt form login mặc định của Spring
            .logout(logout -> logout.disable());
            
        return http.build();
    }
}