package com.example.clothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminController {
    // hiển thị dashboard
    @GetMapping("/admin/dashboard")
    public String viewDashboard(){
        return "admin/dashboard";
    }
    // hiển thị trang thêm sản phẩm
    @GetMapping("/admin/addProduct")
    public String viewAddProduct(){
        return "admin/addProduct";
    }
    // quản lý trang sản phẩm
    @GetMapping("/admin/manageProduct")
    public String viewManageProduct(){
        return "admin/manageProduct";
    }
    // hiển trị trang quản lý khách hàng 
    @GetMapping("/admin/manageCustomer")
    public String manageCustomer() {
        return "admin/manageCustomer";
    }
    // hiển trị trang quản lý đơn hàng
    @GetMapping("/admin/manageOrder")
    public String viewManageOrder(){
        return "admin/manageOrder";
    }

    
}
