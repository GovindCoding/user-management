package com.example.usermanagement.service.impl;

import com.example.usermanagement.model.RefreshToken;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RefreshTokenRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final long refreshTokenDurationMs;

  public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                 UserRepository userRepository,
                                 @Value("${app.jwt.refresh-expiration-ms}") long refreshTokenDurationMs) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.refreshTokenDurationMs = refreshTokenDurationMs;
  }

  @Override
  public RefreshToken createRefreshToken(User user) {
    // delete existing tokens for user (simple approach)
    refreshTokenRepository.deleteByUser(user);
    RefreshToken rt = new RefreshToken();
    rt.setUser(user);
    rt.setToken(UUID.randomUUID().toString());
    rt.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    return refreshTokenRepository.save(rt);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new IllegalArgumentException("Refresh token expired. Please login again.");
    }
    return token;
  }

  @Override
  public void deleteByUser(User user) {
    refreshTokenRepository.deleteByUser(user);
  }
}
