package com.example.bankmanagementdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Ana sayfa isteğini karşılar
    @GetMapping("/")
    public String index() {
        return "index"; // Templates/index.html dosyasını arar
    }

    // Giriş sayfası isteğini karşılar
    @GetMapping("/login")
    public String login() {
        return "login"; // Templates/login.html dosyasını arar
    }
}