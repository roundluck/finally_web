package edu.ntu.maintenance.config;

import edu.ntu.maintenance.entity.*;
import edu.ntu.maintenance.repository.AppUserRepository;
import edu.ntu.maintenance.repository.MaintenanceRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner seedUsers(AppUserRepository userRepository,
                                MaintenanceRequestRepository requestRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            AppUser manager = new AppUser();
            manager.setUsername("dorm_manager");
            manager.setPassword(passwordEncoder.encode("Password!23"));
            manager.setFullName("Dorm Manager");
            manager.setEmail("manager@dorm.edu");
            manager.setPhone("+65-1111-1111");
            manager.setDorm("North Hill");
            manager.setRole(UserRole.MANAGER);
            manager = userRepository.save(manager);

            AppUser technician = new AppUser();
            technician.setUsername("tech_mario");
            technician.setPassword(passwordEncoder.encode("Password!23"));
            technician.setFullName("Mario Tan");
            technician.setEmail("mario.tan@dorm.edu");
            technician.setPhone("+65-2222-2222");
            technician.setDorm("Nanyang Crescent");
            technician.setRole(UserRole.TECHNICIAN);
            technician = userRepository.save(technician);

            AppUser alice = new AppUser();
            alice.setUsername("alicelee");
            alice.setPassword(passwordEncoder.encode("Password!23"));
            alice.setFullName("Alice Lee");
            alice.setEmail("alice.lee@u.edu");
            alice.setPhone("+65-3333-3333");
            alice.setDorm("North Hill");
            alice.setRole(UserRole.STUDENT);
            alice = userRepository.save(alice);

            AppUser brian = new AppUser();
            brian.setUsername("briantan");
            brian.setPassword(passwordEncoder.encode("Password!23"));
            brian.setFullName("Brian Tan");
            brian.setEmail("brian.tan@u.edu");
            brian.setPhone("+65-4444-4444");
            brian.setDorm("North Hill");
            brian.setRole(UserRole.STUDENT);
            brian = userRepository.save(brian);

            MaintenanceRequest leakyTap = new MaintenanceRequest();
            leakyTap.setTitle("Leaky bathroom tap");
            leakyTap.setDescription("Water dripping for two days causing puddles");
            leakyTap.setDorm("North Hill Block 5");
            leakyTap.setRoom("05-123");
            leakyTap.setCategory("Plumbing");
            leakyTap.setPriority(PriorityLevel.MEDIUM);
            leakyTap.setStatus(RequestStatus.ASSIGNED);
            leakyTap.setPreferredEntryTime(OffsetDateTime.now().plusDays(1));
            leakyTap.setStudent(alice);
            leakyTap.setManager(manager);
            leakyTap.setTechnician(technician);
            requestRepository.save(leakyTap);

            MaintenanceRequest corridorLight = new MaintenanceRequest();
            corridorLight.setTitle("Corridor light flickering");
            corridorLight.setDescription("Level 12 corridor light near study lounge flickers intermittently");
            corridorLight.setDorm("North Hill Block 8");
            corridorLight.setRoom("12-Common");
            corridorLight.setCategory("Electrical");
            corridorLight.setPriority(PriorityLevel.HIGH);
            corridorLight.setStatus(RequestStatus.UNDER_REVIEW);
            corridorLight.setStudent(brian);
            corridorLight.setManager(manager);
            requestRepository.save(corridorLight);
        };
    }
}
