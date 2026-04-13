package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.exception.ResourceNotFoundException;
import com.example.bankmanagementdb.model.dto.DepositorDTO;
import com.example.bankmanagementdb.model.entity.Depositor;
import com.example.bankmanagementdb.repository.AccountRepository;
import com.example.bankmanagementdb.repository.CustomerRepository;
import com.example.bankmanagementdb.repository.DepositorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepositorService {

    private final DepositorRepository depositorRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public List<DepositorDTO> getAll() {
        return depositorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DepositorDTO getById(int id) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kayıt bulunamadı ID: " + id));
        return toDTO(d);
    }

    @Transactional // Veritabanı tutarlılığını sağlamak için eklendi
    public DepositorDTO link(int customerId, int accountId) {
        // ID'lerin veritabanında gerçekten var olup olmadığını kontrol ediyoruz
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Hata: " + customerId + " ID'li bir müşteri bulunamadı!");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Hata: " + accountId + " ID'li bir hesap bulunamadı!");
        }
        if (depositorRepository.existsByAccountId(accountId)) {
            throw new IllegalArgumentException("İşlem Başarısız: " + accountId + " ID'li hesap halihazırda başka bir müşteriye aittir!");
        }

        Depositor d = new Depositor();
        d.setCustomerId(customerId);
        d.setAccountId(accountId);

        Depositor saved = depositorRepository.save(d);
        System.out.println("LOG: Müşteri " + customerId + ", Hesap " + accountId + " ile başarıyla eşleştirildi. ID: " + saved.getId());
        return toDTO(saved);
    }

    public DepositorDTO update(int id, DepositorDTO dto) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek kayıt bulunamadı ID: " + id));

        d.setCustomerId(dto.getCustomerId());
        d.setAccountId(dto.getAccountId());
        Depositor updated = depositorRepository.save(d);
        return toDTO(updated);
    }

    public void delete(int id) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek eşleşme bulunamadı ID: " + id));
        depositorRepository.delete(d);
        System.out.println("LOG: Depositor (Eşleşme) silindi ID: " + id);
    }

    private DepositorDTO toDTO(Depositor d) {
        DepositorDTO dto = new DepositorDTO();

        dto.setId(d.getId()); // SİLME İŞLEMİNİN ÇALIŞMASI İÇİN ASIL ID EKLENDİ
        dto.setCustomerId(d.getCustomerId());
        dto.setAccountId(d.getAccountId());

        customerRepository.findById(d.getCustomerId())
                .ifPresent(customer -> dto.setCustomerName(customer.getName()));

        dto.setAccountNumber(String.valueOf(d.getAccountId()));

        return dto;
    }
}