package com.example.bankmanagementdb.service;

import com.example.bankmanagementdb.model.entity.Customer;
import com.example.bankmanagementdb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String idString) throws UsernameNotFoundException {
        try {
            // Login formundan gelen ID string olduğu için int'e çeviriyoruz
            int customerId = Integer.parseInt(idString);

            // Veritabanından ID'ye göre müşteriyi buluyoruz
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new UsernameNotFoundException("Müşteri bulunamadı ID: " + idString));

            // Müşterinin rolü boşsa varsayılan olarak "USER" veriyoruz
            String role = (customer.getRole() != null && !customer.getRole().isEmpty()) ? customer.getRole() : "USER";

            // Spring Security'nin anlayacağı User nesnesini döndürüyoruz
            return User.builder()
                    // DİKKAT: Artık customer.getName() değil, ID'sini atıyoruz.
                    .username(String.valueOf(customer.getId()))
                    .password(customer.getPassword())
                    .roles(role)
                    .build();

        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Geçersiz Müşteri ID formatı: " + idString);
        }
    }
}