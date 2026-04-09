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
public class AssignPrincipalRequest {

    @NotBlank(message = "User email is required")
    @Email(message = "Must be a valid email")
    private String userEmail;
}