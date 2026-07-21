package com.axeld7.inventory_management_system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MainController {

  @GetMapping("/all/welcome")
  public String allAccess() {
    return "Everyone access";
  }

  @GetMapping("/user/userAccess")
  public String userAccess() {
    return "User Content with JWT";
  }

  @GetMapping("/admin/adminAccess")
  public String adminAccess() {
    return "Admin Content with JWT";
  }

  @GetMapping("/employee/employeeaccess")
  public String employeeAccess() {
    return "Employee access with jwt";
  }
}
