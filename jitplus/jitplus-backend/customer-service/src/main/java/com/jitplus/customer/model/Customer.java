package com.jitplus.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private boolean consent;
    
    @Column(nullable = false, unique = true, updatable = false)
    private String qrToken;

    public Customer() {
    }

    public Customer(String phoneNumber, String name, String email, boolean consent) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.consent = consent;
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.qrToken == null || this.qrToken.isEmpty()) {
            this.qrToken = java.util.UUID.randomUUID().toString();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }
    
    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }
}
