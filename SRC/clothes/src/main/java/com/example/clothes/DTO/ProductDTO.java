package com.example.clothes.DTO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
@Data // Lombok tự sinh Getter/Setter
public class ProductDTO {
    private String productName;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private List<String> sizes;
    private List<Integer> quantities;
    private List<MultipartFile> images;
}