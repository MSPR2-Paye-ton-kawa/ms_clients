package com.mspr.clients.models;

import com.mspr.clients.models.entities.Company;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyTest {
    @Test
    void testCompanyAllArgsConstructor() {
        Company company = new Company("name", "email@email.com", "090011223344");

        assertThat(company).isNotNull();
        assertThat(company.getCompanyName()).isEqualTo("name");
        assertThat(company.getEmail()).isEqualTo("email@email.com");
        assertThat(company.getPhoneNumber()).isEqualTo("090011223344");
    }

    @Test
    void testCompanySettersAndGetters() {
        Company company = new Company();
        company.setCompanyName("name");
        company.setEmail("email@email.com");
        company.setPhoneNumber("090011223344");

        assertThat(company).isNotNull();
        assertThat(company.getCompanyName()).isEqualTo("name");
        assertThat(company.getEmail()).isEqualTo("email@email.com");
        assertThat(company.getPhoneNumber()).isEqualTo("090011223344");
    }
}
