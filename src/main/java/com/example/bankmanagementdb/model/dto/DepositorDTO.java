package com.example.bankmanagementdb.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositorDTO {
    private int id; // Silme işleminin doğru çalışması için asıl ID eklendi

    private int accountId;
    private int customerId;

    private String customerName;
    private String accountNumber;
}