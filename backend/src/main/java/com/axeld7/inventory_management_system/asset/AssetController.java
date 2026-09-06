package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.AssetStatsDTO;
import com.axeld7.inventory_management_system.asset.dto.AssetSummaryDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
@AllArgsConstructor
public class AssetController {

  private final AssetService assetService;

  @GetMapping("/stats")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<AssetStatsDTO> getAssetStats() {
    return ResponseEntity.ok(assetService.getAssetStats());
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<Page<AssetSummaryDTO>> getAssets(
      @RequestParam(name = "query", required = false) String query,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    Page<AssetSummaryDTO> response = assetService.findAssetsPaged(query, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AssetResponseDTO> handleGetAsset(@PathVariable Long id) {
    AssetResponseDTO response = assetService.getAssetById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AssetResponseDTO> handleCreateAsset(
      @Validated @RequestBody CreateAssetRequestDTO request,
      @AuthenticationPrincipal User currentUser) {
    AssetResponseDTO response = assetService.createAsset(request, currentUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AssetResponseDTO> handleUpdateAsset(
      @Validated @RequestBody UpdateAssetRequestDTO request,
      @AuthenticationPrincipal User currentUser,
      @PathVariable Long id) {
    AssetResponseDTO response = assetService.updateAsset(request, currentUser, id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void handleDeleteAsset(@PathVariable Long id) {
    assetService.deleteAsset(id);
  }

  @PostMapping("/{id}/checkout")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<CheckoutResponseDTO> handleAssetCheckout(
      @Validated @RequestBody CheckoutRequestDTO request,
      @AuthenticationPrincipal User currentUser,
      @PathVariable Long id) {
    CheckoutResponseDTO response = assetService.checkoutAsset(request, currentUser, id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/{id}/checkin")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<CheckinResponseDTO> handleAssetCirculation(
      @Validated @RequestBody CheckinRequestDTO request,
      @AuthenticationPrincipal User currentUser,
      @PathVariable Long id) {
    CheckinResponseDTO response = assetService.checkinAsset(request, currentUser, id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
