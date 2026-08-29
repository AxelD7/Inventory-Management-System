package com.axeld7.inventory_management_system.auth;

import com.axeld7.inventory_management_system.auth.dto.LoginRequestDTO;
import com.axeld7.inventory_management_system.auth.dto.LoginResponseDTO;
import com.axeld7.inventory_management_system.auth.dto.RegisterRequestDTO;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import com.axeld7.inventory_management_system.user.UserRoles;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<LoginResponseDTO> authenticateUser(@RequestBody LoginRequestDTO request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String token = jwtUtil.generateAccessToken(userDetails.getUsername());
    String refreshtoken = jwtUtil.generateRefreshToken(userDetails.getUsername());

    LoginResponseDTO resp = new LoginResponseDTO(token, userDetails.getUsername());

    ResponseCookie refreshCookie =
        ResponseCookie.from("refreshToken", refreshtoken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/v1/auth/")
            .maxAge(Duration.ofDays(7))
            .build();

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(resp);
  }

  @GetMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshAccessToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken) {

    if (refreshToken == null || !jwtUtil.validateJwtToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String userEmail = jwtUtil.getUserFromToken(refreshToken);

    String newAccessToken = jwtUtil.generateAccessToken(userEmail);

    LoginResponseDTO resp = new LoginResponseDTO(newAccessToken, userEmail);

    return ResponseEntity.status(HttpStatus.OK).body(resp);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie deleteCookie =
        ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true) 
            .sameSite("Strict")
            .path("/api/v1/auth/") 
            .maxAge(0)
            .build();

    SecurityContextHolder.clearContext();

    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .build();
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
