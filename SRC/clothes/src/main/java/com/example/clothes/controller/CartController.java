package com.example.clothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class CartController {
        @GetMapping("/user/cart")
        public String getMethodName() {
            return "/user/cart";
        }
       
}
