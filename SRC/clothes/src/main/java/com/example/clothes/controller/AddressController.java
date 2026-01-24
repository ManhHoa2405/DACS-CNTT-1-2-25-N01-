package com.example.clothes.controller;

import com.example.clothes.model.ShippingAddress;
import com.example.clothes.model.User;
import com.example.clothes.repository.ShippingAddressRepository;
import com.example.clothes.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    // 1. API THÊM ĐỊA CHỈ MỚI
    @PostMapping("/user/address/add")
    @ResponseBody
    public ResponseEntity<?> addAddress(@RequestBody ShippingAddress addressData, HttpSession session) {
        User sessionUser = (User) session.getAttribute("currentUser");
        
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        try {
            User user = userRepository.findById(sessionUser.getId().intValue()).orElseThrow();
            
            addressData.setUser(user);
            
            // Logic: Nếu chưa có địa chỉ nào thì cái đầu tiên là Default
            boolean isFirstAddress = shippingAddressRepository.findByUser(user).isEmpty();
            if (isFirstAddress || Boolean.TRUE.equals(addressData.getIsDefault())) {
                // Nếu cái mới là default, bỏ default của các cái cũ đi (nếu cần kỹ hơn)
                resetDefaultAddress(user);
                addressData.setIsDefault(true);
            } else {
                addressData.setIsDefault(false);
            }

            ShippingAddress savedAddress = shippingAddressRepository.save(addressData);
            
            // Trả về Map để tránh lỗi vòng lặp JSON (User <-> Address)
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedAddress.getId());
            response.put("receiveName", savedAddress.getReceiveName());
            response.put("receivePhone", savedAddress.getReceivePhone());
            response.put("province", savedAddress.getProvince());
            response.put("district", savedAddress.getDistrict());
            response.put("ward", savedAddress.getWard());
            response.put("addressDetail", savedAddress.getAddressDetail());
            response.put("isDefault", savedAddress.getIsDefault());

            return ResponseEntity.ok(response); 
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error saving address: " + e.getMessage());
        }
    }

    // 2. API ĐẶT ĐỊA CHỈ MẶC ĐỊNH (MỚI)
    @PostMapping("/user/address/set-default")
    @ResponseBody
    public ResponseEntity<?> setDefaultAddress(@RequestParam Long addressId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null) return ResponseEntity.status(401).build();

        User user = userRepository.findById(sessionUser.getId().intValue()).orElseThrow();
        List<ShippingAddress> addresses = shippingAddressRepository.findByUser(user);

        for (ShippingAddress addr : addresses) {
            // Chỉ set true cho ID được chọn, các cái khác thành false
            addr.setIsDefault(addr.getId().equals(addressId));
            shippingAddressRepository.save(addr);
        }
        return ResponseEntity.ok().body("Set default success");
    }

    // 3. API XÓA ĐỊA CHỈ (MỚI)
    @PostMapping("/user/address/delete")
    @ResponseBody
    public ResponseEntity<?> deleteAddress(@RequestParam Long addressId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null) return ResponseEntity.status(401).build();

        // Tìm địa chỉ và kiểm tra xem có đúng là của User đang đăng nhập không
        return shippingAddressRepository.findById(addressId)
                .map(addr -> {
                    if (addr.getUser().getId().equals(sessionUser.getId())) {
                        shippingAddressRepository.delete(addr);
                        return ResponseEntity.ok().body("Deleted");
                    } else {
                        return ResponseEntity.status(403).body("Unauthorized");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Hàm phụ trợ để reset default (tránh trường hợp có 2 cái default cùng lúc)
    private void resetDefaultAddress(User user) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUser(user);
        for (ShippingAddress addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                shippingAddressRepository.save(addr);
            }
        }
    }
}