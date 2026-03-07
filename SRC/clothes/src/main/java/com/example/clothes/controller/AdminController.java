package com.example.clothes.controller;
import java.io.IOException;
import java.util.HashMap;
import java.util.List; // Nhớ import DTO
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import com.example.clothes.DTO.ProductDTO;
import com.example.clothes.model.Product;
import com.example.clothes.model.Order;
import com.example.clothes.model.OrderStatus;
import com.example.clothes.model.ProductVariant;
import com.example.clothes.repository.OrderRepository;
import com.example.clothes.repository.ProductRepository;
import com.example.clothes.service.ProductService;
@Controller
public class AdminController {
    // hiển thị dashboard
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/admin/dashboard")
    public String viewDashboard(
        @RequestParam(defaultValue = "0") int page,
        Model model
    ){
        // Thống kê dữ liệu cho dashboard
        model.addAttribute("succeessOrders",orderRepo.countByStatus(OrderStatus.DELIVERED));
        model.addAttribute("sumOrdersDelivered",orderRepo.sumTotalAmountByStatus(OrderStatus.DELIVERED));
        model.addAttribute("totalCusotomer",orderRepo.countDistinctUsersByStatus(OrderStatus.DELIVERED));
        model.addAttribute("totalProducts", orderRepo.sumProductQuanityByStatus(OrderStatus.DELIVERED));

        // Truyền dữ liệu ra view
        // List<Order> orders;
        // orders = orderRepo.findAllByOrderByCreateAtDesc();
        // model.addAttribute("orders", orders);
        // phân trang
        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> orderPage = orderRepo.findAllByOrderByCreateAtDesc(pageable);

        model.addAttribute("orders", orderPage.getContent()); // Lấy danh sách đơn hàng của trang hiện tại
        model.addAttribute("currentPage", page); // số trang hiện tại
        model.addAttribute("totalPages",orderPage.getTotalPages());  // tổng số trang


        return "admin/dashboard";
    }
    

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
                                @RequestParam(defaultValue = "0") int page,
                                Model model
    ){
        // Xử lý chuỗi rỗng (nếu người dùng chọn "Tất cả" thì giá trị là chuỗi rỗng "")
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (categoryName != null && categoryName.trim().isEmpty()) categoryName = null;
        
        int pageSize = 5; 
        Pageable pageable = PageRequest.of(page, pageSize);

        // Gọi hàm lọc ở Repository
        Page<Product> productPage = productRepo.filterProducts(keyword, categoryName, status,pageable);
        
        model.addAttribute("productPage", productPage);
        
        return "admin/manageProduct";
    }

    

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

    // Trong AdminController.java
    // API MỚI: CẬP NHẬT SẢN PHẨM KÈM UPLOAD FILE ẢNH
    @PostMapping("/admin/api/update-product-with-files")
    @ResponseBody
    public ResponseEntity<?> updateProductWithFiles(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("status") Boolean status,
            // Nhận danh sách file upload (có thể rỗng nếu người dùng không chọn ảnh mới)
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        try {
            // Gọi hàm Service mới để xử lý
            productService.updateProductInfoWithFiles(id, name, price, status, imageFiles);
            return ResponseEntity.ok("Cập nhật thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }
    

    // 7. API DELETE PRODUCT (Xóa sản phẩm cha)
    @PostMapping("/admin/api/delete-product")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Object> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id").toString());
            productService.deleteProduct(id);
            return ResponseEntity.ok("Đã xóa");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa: " + e.getMessage());
        }
    }
    
    // hiển trị trang quản lý khách hàng 
    //@GetMapping("/admin/manageCustomer")
    //public String manageCustomer() {
      //  return "admin/manageCustomer";
    //}
    // hiển trị trang quản lý đơn hàng
    @GetMapping("/admin/manageOrder")
    
    public String viewManageOrder(
        @RequestParam(required = false ) String status,
        @RequestParam(required = false ) String keyword,
        
        Model model
    ){
        // 1 thống kê số lượng cho 4 ô vuông (Tổng đơn hàng, chờ xử lý, đang giao, thành công)

        model.addAttribute("totalOrders", orderRepo.count());
        model.addAttribute("pendingOrders", orderRepo.countByStatus(OrderStatus.PENDING));
        model.addAttribute("confirmOrders", orderRepo.countByStatus(OrderStatus.CONFIRMED));
        model.addAttribute("shippingOrders", orderRepo.countByStatus(OrderStatus.SHIPPING));
        model.addAttribute("successOrders", orderRepo.countByStatus(OrderStatus.DELIVERED));

        //  2 tìm kiếm 
        List<Order> orders;

        // 
        if(keyword != null && keyword.trim().isEmpty()) keyword = null; 
        if(status != null && status.trim().isEmpty()) status = null;

        if(keyword != null){
            orders = orderRepo.findByReceiveNameContainingOrReceivePhoneContainingOrderByCreateAtDesc(keyword, keyword);
        }else if (status != null) {
            orders = orderRepo.findByStatusOrderByCreateAtDesc(OrderStatus.valueOf(status));
        } else {
            orders = orderRepo.findAllByOrderByCreateAtDesc(); // Lấy tất cả nếu không có filter
        }

        // 3. Truyền dữ liệu ra view
        model.addAttribute("orders",orders);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        return "admin/manageOrder";
    }
    
    @PostMapping("/admin/manageOrderUpdate")
    public String updateOrderStatus(
        @RequestParam("orderId") Long orderId,
        @RequestParam("status") String status
    ){
        Order order  = orderRepo.findById(orderId).orElse(null);
        if(order != null){
            order.setStatus(OrderStatus.valueOf(status));
            orderRepo.save(order);
        }
        return "redirect:/admin/manageOrder";
    }
}
