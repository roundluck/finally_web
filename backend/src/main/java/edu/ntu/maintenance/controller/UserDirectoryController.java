package edu.ntu.maintenance.controller;

import edu.ntu.maintenance.dto.UserResponse;
import edu.ntu.maintenance.entity.UserRole;
import edu.ntu.maintenance.service.UserDirectoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserDirectoryController {

    private final UserDirectoryService directoryService;

    public UserDirectoryController(UserDirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<UserResponse> listByRole(@RequestParam UserRole role) {
        return directoryService.findByRole(role);
    }
}
