package com.axeld7.inventory_management_system.user;

import com.axeld7.inventory_management_system.asset.CirculationStatus;
import com.axeld7.inventory_management_system.asset.dto.AssetSummaryDTO;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

  private UserService userService;

  @GetMapping()
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<Page<UserSummaryDTO>> getUsers(
      @RequestParam(name = "query", required = false) String query,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    Page<UserSummaryDTO> response = userService.findPagedUsers(query, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<UserSummaryDTO> getUserInfo(@PathVariable Long id) {

    UserSummaryDTO response = userService.getUserSummary(id);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{id}/circulations")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<List<AssetSummaryDTO>> getUserCirculations(
      @PathVariable Long id, @RequestParam CirculationStatus status) {

    List<AssetSummaryDTO> response = userService.getUserCirculations(id, status);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
