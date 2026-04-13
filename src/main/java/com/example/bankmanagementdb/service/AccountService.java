package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.exception.ResourceNotFoundException;
import com.example.bankmanagementdb.model.dto.AccountDTO;
import com.example.bankmanagementdb.model.entity.Account;
import com.example.bankmanagementdb.model.entity.Depositor;
import com.example.bankmanagementdb.repository.AccountRepository;
import com.example.bankmanagementdb.repository.DepositorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final DepositorRepository depositorRepository;

    public List<AccountDTO> getAll() {
        return accountRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AccountDTO getById(int id) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap bulunamadı ID: " + id));
        return toDTO(a);
    }

    public List<AccountDTO> getAccountsByCustomerId(int customerId) {
        List<Integer> accountIds = depositorRepository.findByCustomerId(customerId)
                .stream()
                .map(Depositor::getAccountId)
                .collect(Collectors.toList());

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
        a.setBalance(dto.getBalance());
        accountRepository.save(a);
    }

    public void delete(int id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public void transferMoney(int fromAccountId, int toAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer miktarı 0'dan büyük olmalıdır.");
        }
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("Aynı hesaba transfer yapamazsınız.");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Gönderici hesap bulunamadı (ID: " + fromAccountId + ")"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Alıcı hesap bulunamadı (ID: " + toAccountId + ")"));

        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Yetersiz bakiye. Mevcut bakiye: " + fromAccount.getBalance());
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    private AccountDTO toDTO(Account a) {
        AccountDTO dto = new AccountDTO();
        dto.setId(a.getId());
        dto.setBranch(a.getBranch());
        dto.setBalance(a.getBalance());
        return dto;
    }
}