package com.example.clothes.service;

import com.example.clothes.model.User;
import com.example.clothes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    // Inject cái Bean "máy băm" đã tạo ở file SecurityConfig vào đây
    @Autowired
    private PasswordEncoder passwordEncoder; 

    public void registerUser(User user) throws Exception {
        
        // 1. Check trùng Email
        // (Giả sử trong UserRepository bạn đã có hàm findByEmail)
        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new Exception("Email này đã được sử dụng, vui lòng chọn email khác!");
        }

        // 2. Mã hóa mật khẩu
        // Form gửi lên là "123456" -> Băm thành "$2a$10$Xk9..."
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 3. Set dữ liệu mặc định hệ thống (Chống hack quyền Admin)
    
    
      

        // 4. Lưu vào Database
        userRepo.save(user);
    }

    public User login(String email, String password) throws Exception {
        // 1. Tìm user theo email
        User user = userRepo.findByEmail(email);
        
        // 2. Nếu không tìm thấy user -> Báo lỗi
        if (user == null) {
            throw new Exception("Email không tồn tại!");
        }

        // 3. Kiểm tra mật khẩu (QUAN TRỌNG)
        // password: mật khẩu thô người dùng nhập (123456)
        // user.getPassword(): mật khẩu đã băm trong DB ($2a$10$...)
        // Hàm matches() sẽ tự giải mã và so sánh
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        // 4. (Tùy chọn) Kiểm tra xem tài khoản có bị khóa không
        // if (!user.getStatus()) {
        //    throw new Exception("Tài khoản này đã bị khóa!");
        // }

        // Nếu mọi thứ ok thì trả về user
        return user;
    }
}