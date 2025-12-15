package com.example.usermanagement.config;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.username:}")
  private String adminUsernameEnv;

  @Value("${admin.email:}")
  private String adminEmailEnv;

  @Value("${admin.password:}")
  private String adminPasswordEnv;

  public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    // create initial admin if env vars are provided and no admin exists
    boolean hasAdmin = userRepository.findAll().stream().anyMatch(u -> u.getRoles().contains("ROLE_ADMIN"));
    if (!hasAdmin && adminUsernameEnv != null && !adminUsernameEnv.isBlank()
        && adminEmailEnv != null && !adminEmailEnv.isBlank()
        && adminPasswordEnv != null && !adminPasswordEnv.isBlank()) {
      if (userRepository.existsByUsername(adminUsernameEnv) || userRepository.existsByEmail(adminEmailEnv)) {
        return;
      }
      User admin = new User();
      admin.setUsername(adminUsernameEnv);
      admin.setEmail(adminEmailEnv);
      admin.setFullName("Administrator");
      admin.setPassword(passwordEncoder.encode(adminPasswordEnv));
      admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
      userRepository.save(admin);
      System.out.println("Created initial admin user: " + adminUsernameEnv);
    }
  }
}
