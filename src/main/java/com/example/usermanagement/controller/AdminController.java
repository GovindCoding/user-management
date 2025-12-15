package com.example.usermanagement.controller;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

  private final UserRepository userRepository;
  private final UserService userService;

  public AdminController(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserDto>> listAllUsers() {
    return ResponseEntity.ok(userService.listUsers());
  }

  @PostMapping("/users/{username}/roles")
  public ResponseEntity<Void> addRoleToUser(@PathVariable String username, @RequestBody Map<String, String> body) {
    String role = body.get("role");
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
    Set<String> roles = user.getRoles();
    roles.add(role.startsWith("ROLE_") ? role : "ROLE_" + role);
    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/{username}/roles")
  public ResponseEntity<Void> removeRoleFromUser(@PathVariable String username, @RequestBody Map<String, String> body) {
    String role = body.get("role");
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
    Set<String> roles = user.getRoles();
    roles.remove(role.startsWith("ROLE_") ? role : "ROLE_" + role);
    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.noContent().build();
  }
}
