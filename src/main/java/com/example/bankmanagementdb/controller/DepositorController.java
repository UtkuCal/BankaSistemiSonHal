package com.example.bankmanagementdb.controller;

import com.example.bankmanagementdb.model.dto.DepositorDTO;
import com.example.bankmanagementdb.service.DepositorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/depositor")
public class DepositorController {

    private final DepositorService depositorService;

    public DepositorController(DepositorService depositorService) {
        this.depositorService = depositorService;
    }

    @GetMapping("/delete/{id}")
    public String deleteDepositor(@PathVariable int id) {
        depositorService.delete(id);
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
    public String linkAccountAndCustomer(@ModelAttribute DepositorDTO dto, Authentication authentication) {
        try {
            depositorService.link(dto.getCustomerId(), dto.getAccountId());
            System.out.println("LOG: Müşteri hesaba bağlandı. İşlemi yapan: " + authentication.getName());
        } catch (IllegalArgumentException ex) {
            // Hata olursa işlemi logla (Project 3 istisnalar kuralı)
            System.err.println("KURAL İHLALİ: " + ex.getMessage());
        }

        return "redirect:/depositor/list";
    }
}