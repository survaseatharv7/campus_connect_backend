package com.campusnexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRegistrationRequest {
    @NotBlank(message = "Name is required")
    private String guestName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String guestEmail;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotBlank(message = "City is required")
    private String city;
}
