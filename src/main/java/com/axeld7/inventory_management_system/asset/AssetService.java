package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckinResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutResponseDTO;
import com.axeld7.inventory_management_system.asset.dto.CreateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.UpdateAssetRequestDTO;
import com.axeld7.inventory_management_system.asset.event.AssetLogEvent;
import com.axeld7.inventory_management_system.exception.AssetNotAvailableException;
import com.axeld7.inventory_management_system.exception.AssetNotCheckedOutException;
import com.axeld7.inventory_management_system.exception.DuplicateResourceException;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;
  private final AssetCirculationRepository circulationRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public AssetResponseDTO getAssetById(Long id) {
    Asset asset =
        assetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

    return AssetResponseDTO.fromEntity(asset);
  }

  @Transactional
  public AssetResponseDTO createAsset(CreateAssetRequestDTO request, User currentUser) {

    if (assetRepository.existsByAssetTag(request.assetTag())) {
      throw new DuplicateResourceException(
          "Asset tag " + request.assetTag() + " is already assigned to another asset.");
    }

    Asset asset = new Asset();
    asset.setName(request.name());
    asset.setBrand(request.brand());
    asset.setAssetTag(request.assetTag());
    asset.setDescription(request.description());
    asset.setStatus(AssetStatus.AVAILABLE);

    Asset savedAsset = assetRepository.save(asset);

    eventPublisher.publishEvent(
        new AssetLogEvent(
            savedAsset.getId(),
            currentUser.getId(),
            null,
            AssetAction.CREATED,
            ("Asset record initialized with tag: " + savedAsset.getAssetTag()),
            null));

    return AssetResponseDTO.fromEntity(savedAsset);
  }

  @Transactional
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

    // Check for version to make sure race conditions are handled
    if (!asset.getVersion().equals(request.version())) {
      throw new ObjectOptimisticLockingFailureException(Asset.class, id);
    }

    asset.setName(request.name());
    asset.setBrand(request.brand());
    asset.setAssetTag(request.assetTag());
    asset.setDescription(request.description());
    asset.setStatus(request.status());

    Asset savedAsset = assetRepository.save(asset);

    eventPublisher.publishEvent(
        new AssetLogEvent(
            savedAsset.getId(),
            currentUser.getId(),
            null,
            AssetAction.UPDATED,
            ("Asset record updated with tag: " + savedAsset.getAssetTag()),
            null));

    return AssetResponseDTO.fromEntity(savedAsset);
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
            .findByIdWithPessimisticLock(id)
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

    AssetCirculation savedCirculation = circulationRepository.save(circulation);

    asset.setStatus(AssetStatus.UNAVAILABLE);

    eventPublisher.publishEvent(
        new AssetLogEvent(
            asset.getId(),
            currentUser.getId(),
            borrower.getId(),
            AssetAction.CHECKOUT,
            ("Asset checked out to borrower ID: " + borrower.getId()),
            null));

    return CheckoutResponseDTO.fromEntity(savedCirculation);
  }

  @Transactional
  public CheckinResponseDTO checkinAsset(
      CheckinRequestDTO request, User currentUser, Long assetId) {

    AssetCirculation circulation =
        circulationRepository
            .findByAssetIdAndStatus(assetId, CirculationStatus.ACTIVE)
            .orElseThrow(
                () ->
                    new AssetNotCheckedOutException(
                        "The asset was not in circulation and does not need to be checked back"
                            + " in."));

    circulation.setCheckedInBy(currentUser);
    circulation.setReturnedAt(Instant.now());
    circulation.setStatus(CirculationStatus.RETURNED);
    circulation.setIsDamaged(request.isDamaged());
    circulation.setNotes(request.notes());

    if (Boolean.TRUE.equals(circulation.getIsDamaged())) {
      circulation.getAsset().setStatus(AssetStatus.DAMAGED);
    } else {
      circulation.getAsset().setStatus(AssetStatus.AVAILABLE);
    }

    AssetCirculation updatedCirculation = circulationRepository.save(circulation);

    eventPublisher.publishEvent(
        new AssetLogEvent(
            circulation.getAsset().getId(),
            currentUser.getId(),
            circulation.getBorrower().getId(),
            AssetAction.CHECKIN,
            ("Asset checked in by user ID: " + currentUser.getId()),
            null));

    return CheckinResponseDTO.fromEntity(updatedCirculation);
  }
}
