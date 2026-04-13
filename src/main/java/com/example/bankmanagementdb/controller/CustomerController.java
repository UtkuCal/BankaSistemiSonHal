package com.example.bankmanagementdb.controller;

import com.example.bankmanagementdb.model.dto.CustomerDTO;
import com.example.bankmanagementdb.service.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private static final String UPLOAD_DIR = "uploads/"; // Resimlerin kaydedileceği klasör

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Müşteri Listesi Sayfası
    @GetMapping("/list")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAll());
        return "customer-list"; // customer-list.html dosyasını çağırır
    }

    // Sadece ADMIN görebilir (Security konfigürasyonunda ayarlandıysa)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        return "customer-add"; // customer-add.html
    }

    // Müşteri Silme İşlemi
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id) {
        customerService.delete(id); // Servis üzerinden silme işlemini çağırır
        return "redirect:/customer/list";
    }

    // Müşteri Düzenleme Formunu Açma
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        CustomerDTO dto = customerService.getById(id);
        model.addAttribute("customerDTO", dto);
        // Yeni ekleme sayfasıyla aynı formu (customer-add.html) kullanabiliriz
        return "customer-add";
    }

    @PostMapping("/add")
    public String addCustomer(@ModelAttribute CustomerDTO dto,
                              @RequestParam("imageFile") MultipartFile file,
                              Authentication authentication) {

        try {
            if (!file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                dto.setProfileImage(fileName);
            }

            // DÜZELTME: ID kontrolü yaparak ekleme mi yoksa güncelleme mi olduğunu belirleyin
            if (dto.getId() > 0) {
                // Mevcut müşteriyi güncelle
                customerService.update(dto.getId(), dto);
                System.out.println("LOG: Müşteri güncellendi. İşlemi yapan: " + authentication.getName());
            } else {
                // Yeni müşteri ekle
                customerService.save(dto);
                System.out.println("LOG: Yeni müşteri oluşturuldu. İşlemi yapan: " + authentication.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/customer/list";
    }
}