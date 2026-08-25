package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.AssetCirculation;
import com.axeld7.inventory_management_system.asset.CirculationStatus;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import java.time.Instant;

public record CheckoutResponseDTO(
    Long id,
    AssetSummaryDTO asset,
    UserSummaryDTO borrower,
    UserSummaryDTO checkedOutBy,
    UserSummaryDTO checkedInBy,
    Instant dueDate,
    Instant returnedAt,
    CirculationStatus status) {

  public static CheckoutResponseDTO fromEntity(AssetCirculation checkout) {

    return new CheckoutResponseDTO(
        checkout.getId(),
        AssetSummaryDTO.toAssetSummaryDTO(checkout.getAsset()),
        UserSummaryDTO.toUserSummaryDTO(checkout.getBorrower()),
        UserSummaryDTO.toUserSummaryDTO(checkout.getCheckedOutBy()),
        UserSummaryDTO.toUserSummaryDTO(checkout.getCheckedInBy()),
        checkout.getDueDate(),
        checkout.getReturnedAt(),
        checkout.getStatus());
  }
}
