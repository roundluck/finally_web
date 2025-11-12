package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.UserRole;

public record AuthResponse(
        String token,
        UserRole role,
        String fullName,
        Long userId
) {}
