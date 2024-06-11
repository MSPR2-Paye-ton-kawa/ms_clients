package com.mspr.clients.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientCreateRequest(
        @NotBlank(message = "Username is mandatory")
        String username,
        @NotBlank(message = "Firstname is mandatory")
        String firstname,
        @NotBlank(message = "Lastname is mandatory")
        String lastname,
        @NotBlank(message = "Street is mandatory")
        String street,
        @NotBlank(message = "Postal code is mandatory")
        String postalCode,
        @NotBlank(message = "City is mandatory")
        String city,
        String companyName,
        @Email(message = "Email should be valid")
        String email,
        String phoneNumber
) {
}
