package com.yourpackage.controller; // change package as needed

import com.yourpackage.model.Admin;
import com.yourpackage.service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    @Autowired
    private Service service;

    // 🔐 Admin Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(
            @RequestBody Admin admin) {

        return service.validateAdmin(admin);
    }
}