package edu.ntu.maintenance.controller;

import edu.ntu.maintenance.dto.*;
import edu.ntu.maintenance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public UserResponse profile(@AuthenticationPrincipal UserDetails principal) {
        return authService.getProfile(principal.getUsername());
    }
}
