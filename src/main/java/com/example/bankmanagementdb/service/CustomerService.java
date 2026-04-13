package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.exception.ResourceNotFoundException;
import com.example.bankmanagementdb.model.dto.CustomerDTO;
import com.example.bankmanagementdb.model.entity.Customer;
import com.example.bankmanagementdb.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAll() {
        return customerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CustomerDTO getById(int id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı ID: " + id));
        return toDTO(c);
    }

    public CustomerDTO save(CustomerDTO dto) {
        Customer c = new Customer();
        c.setName(dto.getName());
        c.setAddress(dto.getAddress());
        c.setCity(dto.getCity());
        c.setProfileImage(dto.getProfileImage());

        // Rol ve Şifre Ataması
        String role = (dto.getRole() != null && !dto.getRole().isEmpty()) ? dto.getRole().toUpperCase() : "USER";
        c.setRole(role);

        if ("ADMIN".equals(role)) {
            c.setPassword("admin123");
        } else {
            c.setPassword("1234");
        }

        Customer saved = customerRepository.save(c);
        System.out.println("LOG: Müşteri oluşturuldu ID: " + saved.getId());
        return toDTO(saved);
    }

    public CustomerDTO update(int id, CustomerDTO dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek müşteri bulunamadı ID: " + id));

        c.setName(dto.getName());
        c.setAddress(dto.getAddress());
        c.setCity(dto.getCity());
        c.setProfileImage(dto.getProfileImage());

        // Rol değişikliği varsa şifreyi de role göre güncelliyoruz
        String newRole = (dto.getRole() != null && !dto.getRole().isEmpty()) ? dto.getRole().toUpperCase() : "USER";
        if (!newRole.equals(c.getRole())) {
            c.setRole(newRole);
            if ("ADMIN".equals(newRole)) {
                c.setPassword("admin123");
            } else {
                c.setPassword("1234");
            }
        }

        Customer updated = customerRepository.save(c);
        System.out.println("LOG: Müşteri güncellendi ID: " + updated.getId());
        return toDTO(updated);
    }

    public void delete(int id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek müşteri bulunamadı ID: " + id));
        customerRepository.delete(c);
        System.out.println("LOG: Müşteri silindi ID: " + id);
    }

    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setAddress(c.getAddress());
        dto.setCity(c.getCity());
        dto.setProfileImage(c.getProfileImage());
        dto.setRole(c.getRole()); // DTO'ya rol ataması yapıldı
        return dto;
    }
}