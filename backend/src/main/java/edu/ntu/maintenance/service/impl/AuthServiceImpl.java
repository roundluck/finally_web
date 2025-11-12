package edu.ntu.maintenance.service.impl;

import edu.ntu.maintenance.controller.mapper.UserMapper;
import edu.ntu.maintenance.dto.*;
import edu.ntu.maintenance.entity.AppUser;
import edu.ntu.maintenance.exception.BadRequestException;
import edu.ntu.maintenance.exception.ResourceNotFoundException;
import edu.ntu.maintenance.repository.AppUserRepository;
import edu.ntu.maintenance.security.JwtService;
import edu.ntu.maintenance.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getRole(), user.getFullName(), user.getId());
    }

    @Override
    public UserResponse register(RegisterUserRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already registered");
        }
        if (appUserRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setDorm(request.dorm());
        user.setRole(request.role());
        appUserRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse getProfile(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }
}
