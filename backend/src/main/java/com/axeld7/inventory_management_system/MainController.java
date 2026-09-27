package com.axeld7.inventory_management_system;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MainController {

  @GetMapping("/health")
  public String health() {
    return "Server is up";
  }

  @GetMapping("/all/welcome")
  public String allAccess() {
    return "Everyone access";
  }

  @GetMapping("/user/useraccess")
  @PreAuthorize("hasRole('USER')")
  public String userAccess() {
    return "User Content with JWT";
  }

  @GetMapping("/admin/adminaccess")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Content with JWT";
  }

  @GetMapping("/employee/employeeaccess")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public String employeeAccess() {
    return "Employee access with jwt";
  }
}
