package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Size(min = 4, max = 60) String username,
        @NotBlank @Size(min = 8, max = 80) String password,
        @NotBlank String fullName,
        @Email @NotBlank String email,
        String phone,
        String dorm,
        @NotNull UserRole role
) {}
