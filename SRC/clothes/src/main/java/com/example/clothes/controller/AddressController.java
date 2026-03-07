package com.example.clothes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.clothes.model.ShippingAddress;
import com.example.clothes.model.User;
import com.example.clothes.repository.ShippingAddressRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AddressController {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    // 1. API: Thêm địa chỉ mới
    @PostMapping("/user/address/add")
    @ResponseBody
    public String addAddress(@RequestParam("receiveName") String receiveName,
                             @RequestParam("receivePhone") String receivePhone,
                             @RequestParam("province") String province,
                             @RequestParam("district") String district,
                             @RequestParam("ward") String ward,
                             @RequestParam("addressDetail") String addressDetail,
                             HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) return "error_login";

            ShippingAddress addr = new ShippingAddress();
            addr.setUser(currentUser);
            addr.setReceiveName(receiveName);
            addr.setReceivePhone(receivePhone);
            addr.setProvince(province);
            addr.setDistrict(district);
            addr.setWard(ward);
            addr.setAddressDetail(addressDetail);
            
            // Đặt làm mặc định khi tạo mới
            addr.setIsDefault(true); 

            // Đặt các địa chỉ cũ thành không mặc định (false)
            List<ShippingAddress> oldAddresses = shippingAddressRepository.findByUser(currentUser);
            for (ShippingAddress old : oldAddresses) {
                old.setIsDefault(false);
            }
            shippingAddressRepository.saveAll(oldAddresses);
            shippingAddressRepository.save(addr);

            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // 2. API: Đặt làm địa chỉ mặc định
    @PostMapping("/user/address/set-default")
    @ResponseBody
    public String setDefaultAddress(@RequestParam("addressId") Long addressId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) return "error_login";

            List<ShippingAddress> addresses = shippingAddressRepository.findByUser(currentUser);
            for (ShippingAddress addr : addresses) {
                if (addr.getId().equals(addressId)) {
                    addr.setIsDefault(true);
                } else {
                    addr.setIsDefault(false);
                }
            }
            shippingAddressRepository.saveAll(addresses);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // 3. API: Xóa địa chỉ
    @PostMapping("/user/address/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAddress(@RequestParam("addressId") Long addressId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) return ResponseEntity.status(401).body("error_login");
            
            shippingAddressRepository.deleteById(addressId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error");
        }
    }
}