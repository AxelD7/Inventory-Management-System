package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.user.User;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/assets")
@AllArgsConstructor
public class AssetController {

  private final AssetService assetService;

  @PostMapping
  public ResponseEntity<AssetResponseDTO> handleCreateAsset(
      @Validated @RequestBody CreateAssetRequestDTO request, @AuthenticationPrincipal User currentUser) {

    AssetResponseDTO response = assetService.createAsset(request, currentUser);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AssetResponseDTO> handleGetAsset(@PathVariable Long id) {

    AssetResponseDTO response = assetService.getAssetById(id);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AssetResponseDTO> handleUpdateAsset(
      @Validated @RequestBody UpdateAssetRequestDTO request, @AuthenticationPrincipal User currentUser, @PathVariable Long id) {

    AssetResponseDTO response = assetService.updateAsset(request, currentUser, id);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void handleDeleteAsset(@PathVariable Long id) {
    assetService.deleteAsset(id);
  }
}
