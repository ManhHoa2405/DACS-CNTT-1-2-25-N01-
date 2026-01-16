package com.example.clothes.controller;
import com.example.clothes.service.ProductService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.example.clothes.DTO.ProductDTO; // Nhớ import DTO
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class AdminController {
    // hiển thị dashboard
    @Autowired
    private ProductService productService;
    @GetMapping("/admin/dashboard")
    public String viewDashboard(){
        return "admin/dashboard";
    }
    // hiển thị trang thêm sản phẩm
    // @GetMapping("/admin/addProduct")
    // public String viewAddProduct(){
    //     return "admin/addProduct";
    // }

    @GetMapping("/admin/addProduct")
    public String viewAddProduct(Model model){ // Thêm Model vào tham số
        // Tạo một cái giỏ rỗng để Form HTML điền vào
        model.addAttribute("productDTO", new ProductDTO()); 
        return "admin/addProduct";
    }
    // xử lý thêm sản phẩm
    @PostMapping("/admin/addProductSubmit")
    public String addProductSubmit(@ModelAttribute ProductDTO productDTO,Model model, RedirectAttributes redirectAttributes) {
        try {
            // Gọi Service để lưu vào Database và lưu ảnh
            productService.addProduct(productDTO);
            
            // Lưu xong thì quay lại trang thêm mới (hoặc trang danh sách)
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/admin/addProduct"; 
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "admin/addProduct";
        }
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
