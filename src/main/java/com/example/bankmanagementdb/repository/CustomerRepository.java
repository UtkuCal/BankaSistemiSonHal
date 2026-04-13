package com.example.bankmanagementdb.repository;

import com.example.bankmanagementdb.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Integer kullandık çünkü ID tipini int yapalım demiştin.
}
