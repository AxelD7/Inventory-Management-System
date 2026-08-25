package com.axeld7.inventory_management_system.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

  private String email;
  private String firstName;
  private String lastName;
  private String password;
}
