package com.medicore.config;

import com.medicore.entity.Role;
import com.medicore.entity.User;
import com.medicore.repository.RoleRepository;
import com.medicore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrapRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${app.admin.bootstrap.username:admin}")
    private String adminUsername;

    @Value("${app.admin.bootstrap.password:Admin@Medicore2026!}")
    private String adminPassword;

    @Value("${app.admin.bootstrap.email:admin@medicore.com}")
    private String adminEmail;

    @Value("${app.admin.bootstrap.full-name:System Administrator}")
    private String adminFullName;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Initialize Core Roles if not exist
        List<String> roleNames = Arrays.asList(
                "ROLE_ADMIN",
                "ROLE_DOCTOR",
                "ROLE_RECEPTIONIST",
                "ROLE_PHARMACIST",
                "ROLE_ACCOUNTANT",
                "ROLE_PATIENT"
        );

        for (String roleName : roleNames) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description("System role for " + roleName)
                        .build();
                roleRepository.save(role);
                log.info("Initialized role: {}", roleName);
            }
        }

        // 2. Initialize or Update Admin Account with fresh BCrypt hash
        if (bootstrapEnabled) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

            User admin = userRepository.findByUsername(adminUsername).orElse(null);
            if (admin == null) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);

                admin = User.builder()
                        .username(adminUsername)
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .fullName(adminFullName)
                        .phone("1800-MEDICORE")
                        .isActive(true)
                        .roles(roles)
                        .build();
                userRepository.save(admin);
                log.info("First Administrator created: {}", adminUsername);
            } else {
                // Ensure password matches bootstrap configuration
                admin.setPasswordHash(passwordEncoder.encode(adminPassword));
                admin.setIsActive(true);
                userRepository.save(admin);
                log.info("Administrator password synced: {}", adminUsername);
            }

            // 3. Create Sample Doctor Account for Doctor Portal testing if not exists
            if (!userRepository.existsByUsername("doctor")) {
                Role docRole = roleRepository.findByName("ROLE_DOCTOR")
                        .orElseThrow(() -> new IllegalStateException("ROLE_DOCTOR not found"));
                Set<Role> docRoles = new HashSet<>();
                docRoles.add(docRole);

                User docUser = User.builder()
                        .username("doctor")
                        .email("doctor@medicore.com")
                        .passwordHash(passwordEncoder.encode("Doctor@Medicore2026!"))
                        .fullName("Dr. Aryan Sharma (Cardiologist)")
                        .phone("9876543211")
                        .isActive(true)
                        .roles(docRoles)
                        .build();
                userRepository.save(docUser);
                log.info("Sample Doctor account created: doctor / Doctor@Medicore2026!");
            }

            // 4. Create Sample Pharmacist Account for Pharmacy POS testing if not exists
            if (!userRepository.existsByUsername("pharmacist")) {
                Role pharmRole = roleRepository.findByName("ROLE_PHARMACIST")
                        .orElseThrow(() -> new IllegalStateException("ROLE_PHARMACIST not found"));
                Set<Role> pharmRoles = new HashSet<>();
                pharmRoles.add(pharmRole);

                User pharmUser = User.builder()
                        .username("pharmacist")
                        .email("pharmacy@medicore.com")
                        .passwordHash(passwordEncoder.encode("Pharmacy@Medicore2026!"))
                        .fullName("Pharmacist Rajesh Verma")
                        .phone("9876543212")
                        .isActive(true)
                        .roles(pharmRoles)
                        .build();
                userRepository.save(pharmUser);
                log.info("Sample Pharmacist account created: pharmacist / Pharmacy@Medicore2026!");
            }
        }
    }
}
