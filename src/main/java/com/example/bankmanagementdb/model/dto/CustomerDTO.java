package com.example.bankmanagementdb.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {
    private int id;
    private String name;
    private String address;
    private String city;
    private String profileImage;
    private String role;
}