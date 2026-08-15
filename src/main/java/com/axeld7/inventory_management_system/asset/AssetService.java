package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.exception.DuplicateResourceException;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;
  private final AssetLogRepository logRepository;

  @Transactional(readOnly = true)
  public AssetResponseDTO getAssetById(Long id) {
    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

    return AssetResponseDTO.fromEntity(asset);
  }

  @Transactional
  public AssetResponseDTO createAsset(CreateAssetRequestDTO request, User employee) {

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

    AssetLog log = new AssetLog();
    log.setAsset(savedAsset);
    log.setEmployee(employee);
    log.setPatron(null);
    log.setAction(AssetAction.CREATED);
    log.setNotes("Asset record intialized with tag: " + asset.getAssetTag());

    logRepository.save(log);

    return AssetResponseDTO.fromEntity(savedAsset);
  }

  public AssetResponseDTO updateAsset(UpdateAssetRequestDTO request, User currentUser, Long id) {

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
    
    Asset savedAsset = assetRepository.save(asset);

    AssetLog log = new AssetLog();
    log.setAsset(savedAsset);
    log.setEmployee(currentUser);
    log.setPatron(null);
    log.setAction(AssetAction.UPDATED);
    log.setNotes("Asset record updated with tag: " + asset.getAssetTag());

    logRepository.save(log);


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
