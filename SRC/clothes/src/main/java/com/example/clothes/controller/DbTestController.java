// package com.example.clothes.controller;

// import java.sql.Connection;

// import javax.sql.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
// @RestController
// public class DbTestController {
//    @Autowired
//    private DataSource dataSource;
//    @GetMapping("/dbtest")
//     public String TestDbConnection(){
//         try(Connection connection = dataSource.getConnection()){
//             return"connected to database: " + connection.getCatalog();
//         }catch(Exception e){
//             e.printStackTrace();
//             return "failed to connect to database";
//         }
//     }

// }
