package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.exception.AssetNotAvailableException;
import com.axeld7.inventory_management_system.exception.DuplicateResourceException;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;
  private final AssetLogRepository logRepository;
  private final AssetCirculationRepository circulationRepository;
  private final UserRepository userRepository;

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

    if (assetRepository.existsByAssetTag(request.assetTag())) {
      throw new DuplicateResourceException(
          "Asset tag " + request.assetTag() + " is already assigned to another asset.");
    }

    Asset asset = new Asset();
    asset.setName(request.name());
    asset.setBrand(request.brand());
    asset.setAssetTag(request.assetTag());
    asset.setDescription(request.description());
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

    if (assetRepository.existsByAssetTagAndIdNot(request.assetTag(), id)) {
      throw new DuplicateResourceException(
          "Asset tag " + request.assetTag() + " is already assigned to another asset.");
    }

    asset.setName(request.name());
    asset.setBrand(request.brand());
    asset.setAssetTag(request.assetTag());
    asset.setDescription(request.description());
    asset.setStatus(request.status());

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

  @Transactional
  public CheckoutResponseDTO checkoutAsset(CheckoutRequestDTO request, User currentUser, Long id) {

    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

    if (asset.getStatus() != AssetStatus.AVAILABLE) {
      throw new AssetNotAvailableException(
          "Asset "
              + asset.getAssetTag()
              + " is not currently available and cannot be checked out.");
    }

    User borrower =
        userRepository
            .findById(request.borrowerId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Borrower with id " + request.borrowerId() + " was not found"));

    AssetCirculation circulation = new AssetCirculation();
    circulation.setAsset(asset);
    circulation.setBorrower(borrower);
    circulation.setCheckedOutBy(currentUser);
    circulation.setCheckedInBy(null);
    Duration checkoutDuration = Duration.parse(request.checkoutDuration());
    circulation.setDueDate(Instant.now().plus(checkoutDuration));
    circulation.setReturnedAt(null);
    circulation.setStatus(CirculationStatus.ACTIVE);
    circulationRepository.save(circulation);

    asset.setStatus(AssetStatus.UNAVAILABLE);
    assetRepository.save(asset);

    AssetLog log = new AssetLog();
    log.setAction(AssetAction.CHECKOUT);
    log.setAsset(asset);
    log.setCreatedAt(Instant.now());
    log.setEmployee(currentUser);
    log.setPatron(borrower);
    log.setNotes("The asset was checked out to a borrower.");

    asset.setStatus(AssetStatus.UNAVAILABLE);
    logRepository.save(log);

    return CheckoutResponseDTO.fromEntity(circulation);
  }

  public CheckinResponseDTO checkinAsset(
      CheckinRequestDTO request, User currentUser, Long assetId) {

    AssetCirculation circulation =
        circulationRepository.findByAssetIdAndStatus(assetId, CirculationStatus.ACTIVE);

    circulation.setCheckedInBy(currentUser);
    circulation.setReturnedAt(Instant.now());
    circulation.setStatus(CirculationStatus.RETURNED);
    circulation.setIsDamaged(request.isDamaged());
    circulation.setNotes(request.notes());

    circulation.getAsset().setStatus(AssetStatus.AVAILABLE);

    AssetLog log = new AssetLog();
    log.setAction(AssetAction.CHECKIN);
    log.setAsset(circulation.getAsset());
    log.setCreatedAt(Instant.now());
    log.setEmployee(currentUser);
    log.setPatron(circulation.getBorrower());
    log.setNotes("The asset was checked back in from a borrower.");

    logRepository.save(log);

    return CheckinResponseDTO.fromEntity(circulation);
  }
}
