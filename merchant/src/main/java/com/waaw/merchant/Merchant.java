package com.waaw.merchant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Data
@Entity(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name="business_name")
    private String businessName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
    @Column(name="tax_id")
    private String taxId;
    @Column(name="business_license")
    private String businessLicense;
    private String website;
    private String description;
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private String category;
}