package com.medicore.service;

import com.medicore.dto.AuthDtos;

public interface AuthService {
    AuthDtos.JwtResponse login(AuthDtos.LoginRequest request);
    void registerPatientUser(AuthDtos.RegisterRequest request);
}
