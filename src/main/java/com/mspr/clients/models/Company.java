package com.mspr.clients.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class Company {
    private String companyName;
    private String email;
    private String phoneNumber;

    public Company() {
    }

    public Company(String companyName, String email, String phoneNumber) {
        this.companyName = companyName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
