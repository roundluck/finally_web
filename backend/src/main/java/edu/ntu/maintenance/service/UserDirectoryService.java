package edu.ntu.maintenance.service;

import edu.ntu.maintenance.dto.UserResponse;
import edu.ntu.maintenance.entity.UserRole;

import java.util.List;

public interface UserDirectoryService {
    List<UserResponse> findByRole(UserRole role);
}
