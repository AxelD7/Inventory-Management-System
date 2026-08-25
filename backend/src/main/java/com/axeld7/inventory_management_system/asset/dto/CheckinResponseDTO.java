package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.AssetCirculation;
import com.axeld7.inventory_management_system.asset.CirculationStatus;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import java.time.Instant;

public record CheckinResponseDTO(
    Long circulationId,
    AssetSummaryDTO asset,
    CirculationStatus status,
    Instant returnedAt,
    UserSummaryDTO checkedInBy,
    Boolean isDamaged,
    String notes) {

  public static CheckinResponseDTO fromEntity(AssetCirculation circulation) {

    return new CheckinResponseDTO(
        circulation.getId(),
        AssetSummaryDTO.toAssetSummaryDTO(circulation.getAsset()),
        circulation.getStatus(),
        circulation.getReturnedAt(),
        UserSummaryDTO.toUserSummaryDTO(circulation.getCheckedInBy()),
        circulation.getIsDamaged(),
        circulation.getNotes());
  }
}
