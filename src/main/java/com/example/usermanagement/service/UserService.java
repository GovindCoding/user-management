package com.example.usermanagement.service;

import com.example.usermanagement.dto.UserDto;

import java.util.List;

public interface UserService {
  List<UserDto> listUsers();
  com.example.usermanagement.dto.UserDto getUserById(Long id);
  com.example.usermanagement.dto.UserDto updateUser(Long id, com.example.usermanagement.dto.UserDto dto);
  void deleteUser(Long id);
}
