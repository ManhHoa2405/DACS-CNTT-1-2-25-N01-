package com.example.clothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
   
    // hiển thị đăng nhập
    @GetMapping("/account/login")
    public String ViewLogin(){
        return "account/login";
    }

    // hiển thị đăng ký
    @GetMapping("/account/register")
    public String ViewRegister(){
        return "account/register";
    }
}
