package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.model.RefreshToken;
import com.example.usermanagement.repository.RefreshTokenRepository;
import com.example.usermanagement.security.JwtProvider;
import com.example.usermanagement.service.RefreshTokenService;
import com.example.usermanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProvider jwtProvider;

  public AuthController(AuthService authService,
                        RefreshTokenService refreshTokenService,
                        RefreshTokenRepository refreshTokenRepository,
                        JwtProvider jwtProvider) {
    this.authService = authService;
    this.refreshTokenService = refreshTokenService;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtProvider = jwtProvider;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.status(201).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    RefreshToken rt = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    refreshTokenService.verifyExpiration(rt);
    String username = rt.getUser().getUsername();
    String accessToken = jwtProvider.generateToken(username);
    // Optionally create a new refresh token / rotate:
    RefreshToken newRt = refreshTokenService.createRefreshToken(rt.getUser());
    return ResponseEntity.ok(new TokenResponse(accessToken, newRt.getToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
    refreshTokenRepository.findByToken(request.getRefreshToken())
        .ifPresent(rt -> refreshTokenService.deleteByUser(rt.getUser()));
    return ResponseEntity.noContent().build();
  }
}
