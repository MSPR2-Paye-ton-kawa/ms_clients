package com.mspr.clients.models;

import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.Company;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTest {
    @Test
     void testClientAllArgsConstructor() {
        Address address = new Address();
        Company company = new Company();
        Client client = new Client("username", "firstname", "lastname", address, company);

        assertThat(client).isNotNull();
        assertThat(client.getUsername()).isEqualTo("username");
        assertThat(client.getFirstname()).isEqualTo("firstname");
        assertThat(client.getLastname()).isEqualTo("lastname");
        assertThat(client.getAddress()).isEqualTo(address);
        assertThat(client.getCompany()).isEqualTo(company);
    }

    @Test
    void testClientSettersAndGetters() {
        Address address = new Address();
        Company company = new Company();
        Client client = new Client();

        client.setId(1L);
        client.setUsername("username");
        client.setFirstname("firstname");
        client.setLastname("lastname");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        client.setAddress(address);
        client.setCompany(company);

        assertThat(client).isNotNull();
        assertThat(client.getId()).isEqualTo(1L);
        assertThat(client.getUsername()).isEqualTo("username");
        assertThat(client.getFirstname()).isEqualTo("firstname");
        assertThat(client.getLastname()).isEqualTo("lastname");
        assertThat(client.getAddress()).isEqualTo(address);
        assertThat(client.getCompany()).isEqualTo(company);
    }

    @Test
    void testClientToString() {
        Address address = new Address();
        Company company = new Company();
        Client client = new Client(1L, "username", "firstname", "lastname", LocalDateTime.now(), LocalDateTime.now(), address, company);

        String expected = "Client{id='1', username='username', firstname='firstname', lastname='lastname', address=" + address + ", company=" + company + "}";
        assertThat(client.toString()).hasToString(expected);
    }
}
