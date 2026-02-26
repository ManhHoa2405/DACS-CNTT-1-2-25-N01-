package com.example.clothes.service;

import com.example.clothes.model.User;
import com.example.clothes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Date;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    // Inject cái Bean "máy băm" đã tạo ở file SecurityConfig vào đây
    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private JavaMailSender mailSender; // Công cụ gửi mail

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

   // --- CẬP NHẬT THÔNG TIN CÁ NHÂN ---
    public User updateProfile(Long userId, String name, String phone) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("Không tìm thấy tài khoản!"));
        user.setName(name);
        user.setPhone(phone);
        return userRepo.save(user); // Lưu và trả về User mới để cập nhật Session
    }
   
    // 1. Tạo và gửi mã OTP 6 số
    public void updateResetToken(String email) throws Exception {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            // Random mã 6 số
            Random random = new Random();
            int otpNum = 100000 + random.nextInt(900000);
            String otp = String.valueOf(otpNum);

            user.setResetToken(otp);
            user.setTokenExpiration(LocalDateTime.now().plusMinutes(5)); // Hết hạn sau 5 phút
            userRepo.save(user);

            // Gửi email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Mã xác nhận khôi phục mật khẩu - Modimal");
            message.setText("Chào " + user.getName() + ",\n\n"
                    + "Mã xác nhận 6 số để đặt lại mật khẩu của bạn là: " + otp + "\n"
                    + "Mã này sẽ hết hạn sau 5 phút.\n\n"
                    + "Trân trọng,\nĐội ngũ Modimal.");
            mailSender.send(message);

        } else {
            throw new Exception("Không tìm thấy người dùng với email này.");
        }
    }

    // 2. Xác nhận OTP và đặt lại mật khẩu
    public User resetPassword(String token, String newPassword) throws Exception {
        User user = userRepo.findByResetToken(token);
        if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new Exception("Mã xác nhận không hợp lệ hoặc đã hết hạn!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        return userRepo.save(user); // Trả về user để tự động đăng nhập
    }

    // 3. Đổi mật khẩu chủ động
    public void changePassword(Long userId, String oldPassword, String newPassword) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User không tồn tại"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("Mật khẩu cũ không đúng!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

}

