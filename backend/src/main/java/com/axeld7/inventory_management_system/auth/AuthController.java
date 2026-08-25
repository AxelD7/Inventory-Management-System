package com.axeld7.inventory_management_system.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axeld7.inventory_management_system.auth.dto.LoginRequestDTO;
import com.axeld7.inventory_management_system.auth.dto.RegisterRequestDTO;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import com.axeld7.inventory_management_system.user.UserRoles;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private AuthenticationManager authenticationManager;
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private JwtUtil jwtUtil;

  @Autowired
  public AuthController(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/signin")
  public String authenticateUser(@RequestBody LoginRequestDTO request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return jwtUtil.generateToken(userDetails.getUsername());
  }

  @PostMapping("/signup")
  public String registerUser(@RequestBody RegisterRequestDTO request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      return "User already exists!";
    }
    final User newUser =
        new User(
            null,
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            UserRoles.USER,
            passwordEncoder.encode(request.getPassword()));

    userRepository.save(newUser);
    return "User registered successfully!";
  }
}
