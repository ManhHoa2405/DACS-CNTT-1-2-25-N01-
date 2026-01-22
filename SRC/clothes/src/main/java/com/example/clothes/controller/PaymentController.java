package com.example.clothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {
    @GetMapping("/user/payment")
    public String getPaymentPage() {
        return "/user/payment";
    }
}
