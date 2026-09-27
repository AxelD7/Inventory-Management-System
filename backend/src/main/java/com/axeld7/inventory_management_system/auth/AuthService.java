package com.axeld7.inventory_management_system.auth;

import com.axeld7.inventory_management_system.auth.dto.LoginRequestDTO;
import com.axeld7.inventory_management_system.auth.dto.LoginResponseDTO;
import com.axeld7.inventory_management_system.auth.dto.RegisterRequestDTO;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import com.axeld7.inventory_management_system.exception.DuplicateResourceException;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import com.axeld7.inventory_management_system.user.UserRoles;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private UserRepository userRepository;
  private AuthenticationManager authenticationManager;
  private PasswordEncoder passwordEncoder;
  private JwtUtil jwtUtil;

  @Autowired
  public AuthService(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User authenticatedUser = (User) authentication.getPrincipal();

    String token = jwtUtil.generateAccessToken(authenticatedUser.getEmail());

    LoginResponseDTO resp =
        new LoginResponseDTO(token, UserSummaryDTO.toUserSummaryDTO(authenticatedUser));

    return resp;
  }

  public LoginResponseDTO refreshAccessToken(String userEmail) {

    User authenticatedUser = userRepository.findByEmailIgnoreCase(userEmail);

    String token = jwtUtil.generateAccessToken(authenticatedUser.getEmail());

    LoginResponseDTO resp =
        new LoginResponseDTO(token, UserSummaryDTO.toUserSummaryDTO(authenticatedUser));

    return resp;
  }

  public UserSummaryDTO registerUser(RegisterRequestDTO request) {
    String email = request.getEmail().toLowerCase(Locale.ROOT);
    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new DuplicateResourceException("User with that email already exists.");
    }
    User newUser =
        new User(
            null,
            request.getFirstName(),
            request.getLastName(),
            email,
            UserRoles.USER,
            passwordEncoder.encode(request.getPassword()));

    newUser = userRepository.save(newUser);

    return new UserSummaryDTO(
        newUser.getId(),
        newUser.getEmail(),
        newUser.getFirstName(),
        newUser.getLastName(),
        newUser.getRole());
  }
}
