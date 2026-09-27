package com.axeld7.inventory_management_system.auth;

import com.axeld7.inventory_management_system.auth.dto.LoginRequestDTO;
import com.axeld7.inventory_management_system.auth.dto.LoginResponseDTO;
import com.axeld7.inventory_management_system.auth.dto.RegisterRequestDTO;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;
  private JwtUtil jwtUtil;

  @PostMapping("/signin")
  public ResponseEntity<LoginResponseDTO> authenticateUser(@RequestBody LoginRequestDTO request) {

    LoginResponseDTO resp = authService.authenticateUser(request);

    String refreshtoken = jwtUtil.generateRefreshToken(resp.user().email());
    ResponseCookie refreshCookie =
        ResponseCookie.from("refreshToken", refreshtoken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/v1/auth/")
            .maxAge(Duration.ofDays(7))
            .build();

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(resp);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshAccessToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken) {

    if (refreshToken == null || !jwtUtil.validateJwtToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String userEmail = jwtUtil.getUserFromToken(refreshToken);

    LoginResponseDTO resp = authService.refreshAccessToken(userEmail);

    return ResponseEntity.status(HttpStatus.OK).body(resp);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie deleteCookie =
        ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/v1/auth/")
            .maxAge(0)
            .build();

    SecurityContextHolder.clearContext();

    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .build();
  }

  @PostMapping("/signup")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserSummaryDTO> registerUser(@RequestBody RegisterRequestDTO request) {

    UserSummaryDTO resp = authService.registerUser(request);

    return ResponseEntity.ok(resp);
  }
}
