package com.example.clothes.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dg6atuiqb"); // Thay bằng cái bạn vừa copy
        config.put("api_key", "123541933554838");       // Thay bằng cái bạn vừa copy
        config.put("api_secret", "11VQu7Mn6_3Nw5IAUbTqBS_AJIo"); // Thay bằng cái bạn vừa copy
        config.put("secure", "true");
        return new Cloudinary(config);
    }
}