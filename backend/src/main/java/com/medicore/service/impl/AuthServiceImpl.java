package com.medicore.service.impl;

import com.medicore.dto.AuthDtos;
import com.medicore.entity.AuditLog;
import com.medicore.entity.Role;
import com.medicore.entity.User;
import com.medicore.exception.BadRequestException;
import com.medicore.repository.AuditLogRepository;
import com.medicore.repository.RoleRepository;
import com.medicore.repository.UserRepository;
import com.medicore.security.JwtUtils;
import com.medicore.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuthDtos.JwtResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Audit Log for successful login
        auditLogRepository.save(AuditLog.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .action("LOGIN")
                .resourceType("AUTH")
                .resourceId(user.getId().toString())
                .details("User successfully logged in")
                .build());

        return new AuthDtos.JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), roles);
    }

    @Override
    @Transactional
    public void registerPatientUser(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new IllegalStateException("ROLE_PATIENT not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(patientRole);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .isActive(true)
                .roles(roles)
                .build();

        userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .action("REGISTER")
                .resourceType("USER")
                .resourceId(user.getId().toString())
                .details("New patient user self-registered")
                .build());
    }
}
