package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.exception.ResourceNotFoundException;
import com.example.bankmanagementdb.model.dto.AccountDTO;
import com.example.bankmanagementdb.model.entity.Account;
import com.example.bankmanagementdb.model.entity.Depositor;
import com.example.bankmanagementdb.repository.AccountRepository;
import com.example.bankmanagementdb.repository.DepositorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final DepositorRepository depositorRepository; // Yeni eklendi

    public List<AccountDTO> getAll() {
        return accountRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AccountDTO getById(int id) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap bulunamadı ID: " + id));
        return toDTO(a);
    }

    // Yeni eklenen metot: Sadece müşteriye ait olan hesapları getirir
    public List<AccountDTO> getAccountsByCustomerId(int customerId) {
        // Müşteriye ait hesap ID'lerini bul
        List<Integer> accountIds = depositorRepository.findByCustomerId(customerId)
                .stream()
                .map(Depositor::getAccountId)
                .collect(Collectors.toList());

        // Bu ID'lere ait hesapları getir ve DTO'ya çevir
        return accountRepository.findAllById(accountIds)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void save(AccountDTO dto) {
        Account a = new Account();
        a.setBranch(dto.getBranch());
        a.setBalance(dto.getBalance());
        accountRepository.save(a);
    }

    public void update(int id, AccountDTO dto) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek hesap bulunamadı ID: " + id));

        a.setBranch(dto.getBranch());
        a.setBalance(dto.getBalance()); // Bakiyeyi günceller
        accountRepository.save(a);
    }

    public void delete(int id) {
        accountRepository.deleteById(id);
    }

    private AccountDTO toDTO(Account a) {
        AccountDTO dto = new AccountDTO();
        dto.setId(a.getId());
        dto.setBranch(a.getBranch());
        dto.setBalance(a.getBalance());
        return dto;
    }
}