package edu.ntu.maintenance.controller.mapper;

import edu.ntu.maintenance.dto.UserResponse;
import edu.ntu.maintenance.entity.AppUser;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(AppUser user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getDorm(),
                user.getRole()
        );
    }
}
