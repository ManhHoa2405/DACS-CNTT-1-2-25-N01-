package com.example.clothes.controller;
import com.example.clothes.service.ProductService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.example.clothes.DTO.ProductDTO; // Nhớ import DTO
import com.example.clothes.model.Product;
import com.example.clothes.model.ProductVariant;
import com.example.clothes.repository.ProductRepository;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class AdminController {
    // hiển thị dashboard
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepo;
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
    public String viewManageProduct(
        @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String categoryName,
                                @RequestParam(required = false) Boolean status,
                                Model model
    ){
        // // 1. Gọi Service lấy danh sách (có tìm kiếm hoặc không)
        // List<Product> products = productService.getAllProducts(keyword);
        
        // // 2. Gửi danh sách sang HTML
        // model.addAttribute("products", products);
        
        // // 3. Trả về file HTML: templates/admin/manageProduct.html
        // return "admin/manageProduct";

        // Xử lý chuỗi rỗng (nếu người dùng chọn "Tất cả" thì giá trị là chuỗi rỗng "")
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (categoryName != null && categoryName.trim().isEmpty()) categoryName = null;
        
        // Gọi hàm lọc ở Repository
        List<Product> list = productRepo.filterProducts(keyword, categoryName, status);
        
        model.addAttribute("products", list);
        
        return "admin/manageProduct";
    }

    // NHIỆM VỤ 2: API nhận dữ liệu từ nút "Lưu" (Ajax)
    // Link này được gọi ngầm bởi Javascript khi bấm nút ✔
    // File: AdminController.java

    // 3. API CẬP NHẬT SKU (Sửa số lượng)
    @PostMapping("/admin/api/update-sku")
    @ResponseBody
    public ResponseEntity<?> updateSku(@RequestBody Map<String, Object> payload) {
        try {
            // Lấy dữ liệu từ JSON gửi lên
            Integer id = Integer.parseInt(payload.get("id").toString());
            Integer newStock = Integer.parseInt(payload.get("stock").toString());

            // Gọi Service để update (Bạn cần đảm bảo Service có hàm này)
            productService.updateVariantStock(id, newStock);
            
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // 1. API THÊM SKU MỚI
    @PostMapping("/admin/api/add-sku") 
    @ResponseBody
    public ResponseEntity<?> addSku(@RequestBody Map<String, Object> payload) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (payload.get("productId") == null || payload.get("size") == null || payload.get("stock") == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin (productId, size, stock)!");
            }

            Integer productId = Integer.parseInt(payload.get("productId").toString());
            String size = payload.get("size").toString();
            Integer stock = Integer.parseInt(payload.get("stock").toString());

            // Gọi Service (Bạn đã viết hàm này rồi, giờ chỉ việc gọi thôi)
            ProductVariant newVariant = productService.addVariant(productId, size, stock);
            
            // Trả về ID mới để JS vẽ lên bảng
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm thành công");
            response.put("newId", newVariant.getId());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi thêm: " + e.getMessage());
        }
    }

    // 2. API XÓA SKU
    @PostMapping("/admin/api/delete-sku") // 👈 Nhớ phải có /admin ở đầu
    @ResponseBody
    public ResponseEntity<?> deleteSku(@RequestBody Map<String, Object> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id").toString());
            
            // Gọi Service xóa
            productService.deleteVariant(id);
            
            return ResponseEntity.ok("Đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa: " + e.getMessage());
        }
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
