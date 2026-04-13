package com.example.bankmanagementdb.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Depositor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int accountId;
    private int customerId;

    // Boş Constructor (Hibernate için şart)
    public Depositor() {}
}