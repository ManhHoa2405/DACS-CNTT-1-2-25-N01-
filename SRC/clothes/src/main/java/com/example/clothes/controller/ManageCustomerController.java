package com.example.clothes.controller; // Đổi package cho đúng dự án của bạn

import java.util.List;            // Import đúng model User của bạn

import org.springframework.beans.factory.annotation.Autowired; // Import đúng Repo
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.clothes.model.User;
import com.example.clothes.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class ManageCustomerController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/manageCustomer")
    public String showPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        
        List<User> listUsers;

        // 1. Xử lý tìm kiếm
        if (keyword != null && !keyword.isEmpty()) {
            // Gọi hàm vừa thêm ở Bước 1
            listUsers = userRepository.searchUserByKeyword(keyword);
        } else {
            // Lấy tất cả user
            listUsers = userRepository.findAll();
        }

        System.out.println("==================================");
        System.out.println("TỔNG SỐ USER TÌM THẤY: " + listUsers.size());
        if(!listUsers.isEmpty()) {
            System.out.println("Tên User đầu tiên: " + listUsers.get(0).getName());
        }
        System.out.println("==================================");

        // 2. Tính toán thống kê
        long totalUsers = userRepository.count();
        
        // Đếm số người mua (Buyer)
        // Logic: Lọc ra những user có danh sách orders không rỗng
        long buyerUsers = 0;
        if (listUsers != null) {
            buyerUsers = listUsers.stream()
                .filter(u -> u.getOrders() != null && !u.getOrders().isEmpty())
                .count();
        }

        // 3. Đẩy dữ liệu ra giao diện (HTML)
        model.addAttribute("customers", listUsers);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("buyerUsers", buyerUsers);
        model.addAttribute("keyword", keyword); // Giữ lại từ khóa tìm kiếm

        return "admin/manageCustomer"; // Trả về file manageCustomer.html
    }
}