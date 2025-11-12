package edu.ntu.maintenance.service;

import edu.ntu.maintenance.dto.*;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    UserResponse register(RegisterUserRequest request);

    UserResponse getProfile(String username);
}
