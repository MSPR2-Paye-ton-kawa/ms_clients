package com.mspr.clients.dto;

import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.Company;

import java.time.LocalDateTime;

public record ClientDTO(
        Long id,
        String username,
        String firstname,
        String lastname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Address address,
        Company company) {
    public static ClientDTO fromModel(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getUsername(),
                client.getFirstname(),
                client.getLastname(),
                client.getCreatedAt(),
                client.getUpdatedAt(),
                client.getAddress(),
                client.getCompany());
    }
}
