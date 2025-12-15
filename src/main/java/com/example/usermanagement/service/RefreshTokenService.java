package com.example.usermanagement.service;

import com.example.usermanagement.model.RefreshToken;
import com.example.usermanagement.model.User;

public interface RefreshTokenService {
  RefreshToken createRefreshToken(User user);
  RefreshToken verifyExpiration(RefreshToken token);
  void deleteByUser(User user);
}
