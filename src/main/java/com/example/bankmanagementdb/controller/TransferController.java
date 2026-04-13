package com.example.bankmanagementdb.controller;

import com.example.bankmanagementdb.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    private final AccountService accountService;

    public TransferController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String showTransferForm(Model model, Authentication authentication) {
        int loggedInCustomerId = Integer.parseInt(authentication.getName());
        model.addAttribute("myAccounts", accountService.getAccountsByCustomerId(loggedInCustomerId));
        return "transfer";
    }

    @PostMapping
    public String processTransfer(@RequestParam int fromAccountId,
                                  @RequestParam int toAccountId,
                                  @RequestParam double amount,
                                  RedirectAttributes redirectAttributes,
                                  Authentication authentication) {
        try {
            int loggedInCustomerId = Integer.parseInt(authentication.getName());
            boolean isOwner = accountService.getAccountsByCustomerId(loggedInCustomerId)
                    .stream()
                    .anyMatch(acc -> acc.getId() == fromAccountId);

            if (!isOwner) {
                redirectAttributes.addFlashAttribute("error", "Size ait olmayan bir hesaptan işlem yapamazsınız.");
                return "redirect:/transfer";
            }

            accountService.transferMoney(fromAccountId, toAccountId, amount);
            redirectAttributes.addFlashAttribute("success", "Para transferi başarıyla gerçekleştirildi.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transfer";
    }
}