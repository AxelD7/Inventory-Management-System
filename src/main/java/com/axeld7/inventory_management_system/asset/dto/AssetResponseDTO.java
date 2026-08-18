package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.Asset;
import com.axeld7.inventory_management_system.asset.AssetStatus;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import com.axeld7.inventory_management_system.user.User;

import java.time.Instant;

public record AssetResponseDTO(
    Long id,
    String assetTag,
    String name,
    String brand,
    String description,
    AssetStatus status,
    Instant createdAt,
    Instant updateAt,
    UserSummaryDTO createdBy,
    UserSummaryDTO updatedBy) {
  public static AssetResponseDTO fromEntity(Asset asset) {

    return new AssetResponseDTO(
        asset.getId(),
        asset.getAssetTag(),
        asset.getName(),
        asset.getBrand(),
        asset.getDescription(),
        asset.getStatus(),
        asset.getCreatedAt(),
        asset.getUpdatedAt(),
        UserSummaryDTO.toUserSummaryDTO(asset.getCreatedBy()),
        UserSummaryDTO.toUserSummaryDTO(asset.getUpdatedBy()));
  }


}
