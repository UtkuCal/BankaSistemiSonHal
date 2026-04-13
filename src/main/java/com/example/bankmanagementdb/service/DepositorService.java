package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.exception.ResourceNotFoundException;
import com.example.bankmanagementdb.model.dto.DepositorDTO;
import com.example.bankmanagementdb.model.entity.Depositor;
import com.example.bankmanagementdb.repository.AccountRepository;
import com.example.bankmanagementdb.repository.CustomerRepository;
import com.example.bankmanagementdb.repository.DepositorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepositorService {
    // Mevduat sahibi, müşteri ve hesap verilerine erişim için gerekli repository'ler
    private final DepositorRepository depositorRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    /**
     * Tüm mevduat sahiplerini listeler ve DTO'ya dönüştürür.
     */
    public List<DepositorDTO> getAll() {
        return depositorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * ID'ye göre tek bir mevduat sahibi kaydını getirir.
     */
    public DepositorDTO getById(int id) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kayıt bulunamadı ID: " + id));
        return toDTO(d);
    }

    /**
     * Müşteriyi bir hesaba bağlar. Hesabın başka birine ait olup olmadığını kontrol eder.
     */
    public DepositorDTO link(int customerId, int accountId) {
        // KURAL KONTROLÜ: Bu hesap ID'si başka biri tarafından alınmış mı?
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

    /**
     * Mevcut bir eşleşmeyi günceller.
     */
    public DepositorDTO update(int id, DepositorDTO dto) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek kayıt bulunamadı ID: " + id));

        d.setCustomerId(dto.getCustomerId());
        d.setAccountId(dto.getAccountId());
        Depositor updated = depositorRepository.save(d);
        System.out.println("LOG: Depositor (Eşleşme) güncellendi ID: " + updated.getId());
        return toDTO(updated);
    }

    /**
     * Bir mevduat sahibi eşleşmesini siler.
     */
    public void delete(int id) {
        Depositor d = depositorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek eşleşme bulunamadı ID: " + id));
        depositorRepository.delete(d);
        System.out.println("LOG: Depositor (Eşleşme) silindi ID: " + id);
    }

    /**
     * Entity nesnesini HTML şablonunun beklediği DTO formatına çevirir.
     * Müşteri ismini ve hesap numarasını ilgili repository'lerden çeker.
     */
    private DepositorDTO toDTO(Depositor d) {
        DepositorDTO dto = new DepositorDTO();
        dto.setCustomerId(d.getCustomerId());
        dto.setAccountId(d.getAccountId());

        // HTML listesinde görünmesi gereken Müşteri İsmi
        customerRepository.findById(d.getCustomerId())
                .ifPresent(customer -> dto.setCustomerName(customer.getName()));

        // HTML listesinde görünmesi gereken Hesap Numarası/ID
        // Burada entity'deki accountId'yi String olarak accountNumber alanına atıyoruz.
        dto.setAccountNumber(String.valueOf(d.getAccountId()));

        return dto;
    }
}