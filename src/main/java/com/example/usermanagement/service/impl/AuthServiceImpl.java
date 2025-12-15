package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.TokenResponse;
import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;
import com.example.usermanagement.model.RefreshToken;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtProvider;
import com.example.usermanagement.service.AuthService;
import com.example.usermanagement.service.RefreshTokenService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RefreshTokenService refreshTokenService;

  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         JwtProvider jwtProvider,
                         RefreshTokenService refreshTokenService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
    this.refreshTokenService = refreshTokenService;
  }

  @Override
  public TokenResponse authenticate(LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );

    String accessToken = jwtProvider.generateToken(auth.getName());
    User user = userRepository.findByUsername(auth.getName()).orElseThrow();
    RefreshToken rt = refreshTokenService.createRefreshToken(user);
    return new TokenResponse(accessToken, rt.getToken());
  }

  @Override
  public void register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("username already exists");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("email already exists");
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setFullName(request.getFullName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRoles(Set.of("ROLE_USER"));
    userRepository.save(user);
  }
}
