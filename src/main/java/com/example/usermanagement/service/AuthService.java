package com.example.usermanagement.service;

import com.example.usermanagement.dto.TokenResponse;
import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;

public interface AuthService {
  TokenResponse authenticate(LoginRequest request);
  void register(RegisterRequest request);
}
