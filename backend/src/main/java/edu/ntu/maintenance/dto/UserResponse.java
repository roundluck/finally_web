package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.UserRole;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String dorm,
        UserRole role
) {}
