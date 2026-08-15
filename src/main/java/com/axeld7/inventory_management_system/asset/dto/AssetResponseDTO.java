package com.axeld7.inventory_management_system.asset.dto;

import java.time.Instant;

import com.axeld7.inventory_management_system.asset.Asset;
import com.axeld7.inventory_management_system.asset.AssetStatus;
import com.axeld7.inventory_management_system.user.User;

public record AssetResponseDTO(
    Long id,
    String assetTag,
    String name,
    String brand,
    String description,
    AssetStatus status,
    Instant createdAt,
    Instant updateAt,
    Long createdById,
    Long updatedById
) {
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
            asset.getCreatedById(),
            asset.getUpdatedById()
        );
    }
}