package com.example.bankmanagementdb.controller;

import com.example.bankmanagementdb.model.dto.DepositorDTO;
import com.example.bankmanagementdb.service.DepositorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/depositor")
public class DepositorController {

    private final DepositorService depositorService;

    public DepositorController(DepositorService depositorService) {
        this.depositorService = depositorService;
    }

    @GetMapping("/delete/{id}")
    public String deleteDepositor(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            depositorService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Bağlantı başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/depositor/list";
    }

    @GetMapping("/list")
    public String listDepositors(Model model) {
        model.addAttribute("depositors", depositorService.getAll());
        return "depositor-list";
    }

    @GetMapping("/link")
    public String showLinkForm(Model model) {
        model.addAttribute("depositorDTO", new DepositorDTO());
        return "depositor-link";
    }

    @PostMapping("/link")
    public String linkAccountAndCustomer(@ModelAttribute DepositorDTO dto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            depositorService.link(dto.getCustomerId(), dto.getAccountId());
            System.out.println("LOG: Müşteri hesaba bağlandı. İşlemi yapan: " + authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Kayıt başarıyla oluşturuldu.");
        } catch (IllegalArgumentException ex) {
            // Kural ihlali durumunda hatayı ekrana gönderir
            System.err.println("KURAL İHLALİ: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            // Beklenmedik durumlarda hatayı ekrana gönderir
            redirectAttributes.addFlashAttribute("error", "Beklenmedik bir hata oluştu: " + ex.getMessage());
        }
        return "redirect:/depositor/list";
    }
}