package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.Asset;
import com.axeld7.inventory_management_system.asset.AssetStatus;

public record AssetSummaryDTO(
    Long id, String assetTag, String name, String brand, AssetStatus status) {
  public static AssetSummaryDTO toAssetSummaryDTO(Asset asset) {
    return new AssetSummaryDTO(
        asset.getId(), asset.getAssetTag(), asset.getName(), asset.getBrand(), asset.getStatus());
  }
}
