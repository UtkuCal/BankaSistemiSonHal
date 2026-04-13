// BankManagementDb/src/main/java/com/example/bankmanagementdb/model/dto/DepositorDTO.java
package com.example.bankmanagementdb.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositorDTO {
    private int accountId;
    private int customerId;

    private String customerName;
    private String accountNumber;
}