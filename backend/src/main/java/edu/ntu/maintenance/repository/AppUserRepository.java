package edu.ntu.maintenance.repository;

import edu.ntu.maintenance.entity.AppUser;
import edu.ntu.maintenance.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AppUser> findByRole(UserRole role);
}
