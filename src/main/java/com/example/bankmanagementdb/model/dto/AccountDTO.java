package com.example.bankmanagementdb.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AccountDTO {
    private int id;
    private String branch;
    private double balance;
}