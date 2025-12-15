package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.exception.ResourceNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final UserMapper mapper;

  public UserServiceImpl(UserRepository repository, UserMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public List<UserDto> listUsers() {
    return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  public UserDto getUserById(Long id) {
    User u = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    return mapper.toDto(u);
  }

  @Override
  public UserDto updateUser(Long id, UserDto dto) {
    User existing = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    // check unique username/email if changed
    if (!existing.getUsername().equals(dto.getUsername()) && repository.existsByUsername(dto.getUsername())) {
      throw new IllegalArgumentException("username already exists");
    }
    if (!existing.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("email already exists");
    }
    mapper.updateFromDto(dto, existing);
    repository.save(existing);
    return mapper.toDto(existing);
  }

  @Override
  public void deleteUser(Long id) {
    if (!repository.existsById(id)) throw new ResourceNotFoundException("User not found with id " + id);
    repository.deleteById(id);
  }
}
