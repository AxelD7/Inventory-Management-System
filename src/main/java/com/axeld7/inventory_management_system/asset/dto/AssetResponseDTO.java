package com.axeld7.inventory_management_system.asset.dto;

import java.time.Instant;

import com.axeld7.inventory_management_system.asset.Asset;
import com.axeld7.inventory_management_system.asset.AssetStatus;

public record AssetResponseDTO(
    Long id,
    String assetTag,
    String name,
    String brand,
    String description,
    AssetStatus status,
    Instant createdAt
) {
    public static AssetResponseDTO fromEntity(Asset asset) {
        return new AssetResponseDTO(
            asset.getId(),
            asset.getAssetTag(),
            asset.getName(),
            asset.getBrand(),
            asset.getDescription(),
            asset.getStatus(),
            asset.getCreatedAt()
        );
    }
}