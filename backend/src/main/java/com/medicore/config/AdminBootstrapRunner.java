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
        // Initialize Core Roles if not exist
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

        // Initialize First Admin if enabled and not exists
        if (bootstrapEnabled && !userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .fullName(adminFullName)
                    .phone("1800-MEDICORE")
                    .isActive(true)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            log.info("First Administrator securely bootstrapped with username: {}", adminUsername);
        }
    }
}
