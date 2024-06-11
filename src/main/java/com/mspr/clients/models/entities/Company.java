package com.mspr.clients.models.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.*;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Company {
    private String companyName;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;
}
