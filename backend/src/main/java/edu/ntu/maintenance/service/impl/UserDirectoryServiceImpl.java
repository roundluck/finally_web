package edu.ntu.maintenance.service.impl;

import edu.ntu.maintenance.controller.mapper.UserMapper;
import edu.ntu.maintenance.dto.UserResponse;
import edu.ntu.maintenance.entity.UserRole;
import edu.ntu.maintenance.repository.AppUserRepository;
import edu.ntu.maintenance.service.UserDirectoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDirectoryServiceImpl implements UserDirectoryService {

    private final AppUserRepository userRepository;

    public UserDirectoryServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> findByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
