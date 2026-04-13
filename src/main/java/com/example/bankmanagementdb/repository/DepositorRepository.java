package com.example.bankmanagementdb.repository;

import com.example.bankmanagementdb.model.entity.Depositor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DepositorRepository extends JpaRepository<Depositor, Integer> {
    // Belirli bir müşteriye ait kayıtları bulmak için
    List<Depositor> findByCustomerId(int CustomerId);
    // Belirli bir hesaba ait kayıtları bulmak için
    List<Depositor> findByAccountId(int AccountId);

    // YENİ: Hesap numarasının kullanımda olup olmadığını kontrol eden metot
    boolean existsByAccountId(int accountId);
}
