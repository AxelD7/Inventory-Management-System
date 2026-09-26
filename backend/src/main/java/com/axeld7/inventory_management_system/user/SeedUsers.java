package com.axeld7.inventory_management_system.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedUsers implements ApplicationRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final String adminEmail;
  private final String adminPassword;
  private final String employeeEmail;
  private final String employeePassword;

  public SeedUsers(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      @Value("${app.seed.admin.email:}") String adminEmail,
      @Value("${app.seed.admin.password:}") String adminPassword,
      @Value("${app.seed.employee.email:}") String employeeEmail,
      @Value("${app.seed.employee.password:}") String employeePassword) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.adminEmail = adminEmail;
    this.adminPassword = adminPassword;
    this.employeeEmail = employeeEmail;
    this.employeePassword = employeePassword;
  }

  @Override
  public void run(ApplicationArguments args) {
    seed(adminEmail, adminPassword, "Initial", "Admin", UserRoles.ADMIN);
    seed(employeeEmail, employeePassword, "Initial", "Employee", UserRoles.EMPLOYEE);
  }

  private void seed(
      String email, String password, String firstName, String lastName, UserRoles role) {
    if (email.isBlank() && password.isBlank()) {
      return;
    }
    if (email.isBlank() || password.isBlank()) {
      throw new IllegalStateException("Both seed email and password must be configured.");
    }

    if (!userRepository.existsByEmail(email)) {
      User user = new User();
      user.setEmail(email);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setRole(role);
      user.setPasswordHash(passwordEncoder.encode(password));
      userRepository.save(user);
    }
  }
}
