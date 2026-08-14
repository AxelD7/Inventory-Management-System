package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.exception.DuplicateResourceException;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;

  @Transactional(readOnly = true)
  public AssetResponseDTO getAssetById(Long id) {
    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

    return AssetResponseDTO.fromEntity(asset);
  }

  @Transactional
  public AssetResponseDTO createAsset(CreateAssetRequestDTO request) {

    if (assetRepository.existsByAssetTag(request.getAssetTag())) {
      throw new DuplicateResourceException(
          "Asset tag " + request.getAssetTag() + " is already assigned to another asset.");
    }

    Asset asset = new Asset();
    asset.setName(request.getName());
    asset.setBrand(request.getBrand());
    asset.setAssetTag(request.getAssetTag());
    asset.setDescription(request.getDescription());
    asset.setStatus(AssetStatus.UNAVAILABLE);

    Asset savedAsset = assetRepository.save(asset);

    return AssetResponseDTO.fromEntity(savedAsset);
  }

  @Transactional
  public AssetResponseDTO updateAsset(Long id, UpdateAssetRequestDTO request) {

    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Asset with id " + id + " not found!"));

    if (assetRepository.existsByAssetTagAndIdNot(request.getAssetTag(), id)) {
      throw new DuplicateResourceException(
          "Asset tag " + request.getAssetTag() + " is already assigned to another asset.");
    }

    asset.setName(request.getName());
    asset.setBrand(request.getBrand());
    asset.setAssetTag(request.getAssetTag());
    asset.setDescription(request.getDescription());
    asset.setStatus(request.getStatus());

    return AssetResponseDTO.fromEntity(asset);
  }

  @Transactional
  public void deleteAsset(Long id) {

    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

    assetRepository.delete(asset);
  }
}
