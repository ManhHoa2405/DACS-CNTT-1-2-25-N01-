package com.example.clothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/admin/mangeCustomer")
    public String manageCustomer() {
        return "admin/manageCustomer";
    }
}
