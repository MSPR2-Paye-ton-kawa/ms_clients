package com.mspr.clients.models.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {
    @NotBlank(message = "Street is mandatory")
    private String street;

    @NotBlank(message = "Postal code is mandatory")
    private String postalCode;

    @NotBlank(message = "City is mandatory")
    private String city;
}
