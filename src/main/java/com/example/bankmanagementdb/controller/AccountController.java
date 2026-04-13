package com.example.bankmanagementdb.controller;

import com.example.bankmanagementdb.model.dto.AccountDTO;
import com.example.bankmanagementdb.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/list")
    public String listAccounts(Model model, Authentication authentication) {
        // Kullanıcının ADMIN rolü olup olmadığını kontrol et
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // ADMIN ise tüm hesapları görsün
            model.addAttribute("accounts", accountService.getAll());
        } else {
            // USER ise CustomUserDetailsService'de atadığımız ID'yi al ve sadece kendi hesaplarını getir
            int loggedInCustomerId = Integer.parseInt(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByCustomerId(loggedInCustomerId));
        }

        return "account-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("accountDTO", new AccountDTO());
        return "account-add";
    }

    @PostMapping("/add")
    public String addAccount(@ModelAttribute AccountDTO accountDTO) {
        accountService.save(accountDTO);
        return "redirect:/account/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        model.addAttribute("accountDTO", accountService.getById(id));
        return "account-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAccount(@PathVariable int id, @ModelAttribute AccountDTO dto, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Eğer kullanıcı ADMIN değilse, hesaba sahip olup olmadığını kontrol et
        if (!isAdmin) {
            int loggedInCustomerId = Integer.parseInt(authentication.getName());

            // Kullanıcının kendi hesapları arasında güncellenmek istenen ID var mı?
            boolean isOwner = accountService.getAccountsByCustomerId(loggedInCustomerId)
                    .stream()
                    .anyMatch(acc -> acc.getId() == id);

            if (!isOwner) {
                // Başkasının hesabını güncellemeye çalışıyorsa hata ver veya listeye geri gönder
                return "redirect:/account/list?error=unauthorized";
            }
        }

        accountService.update(id, dto);
        return "redirect:/account/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable int id) {
        accountService.delete(id);
        return "redirect:/account/list";
    }
}