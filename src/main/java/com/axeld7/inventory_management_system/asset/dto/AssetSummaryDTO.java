package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.Asset;

public record AssetSummaryDTO(Long id, String assetTag, String name, String brand) {
  public static AssetSummaryDTO toAssetSummaryDTO(Asset asset) {
    return new AssetSummaryDTO(
        asset.getId(), asset.getAssetTag(), asset.getName(), asset.getBrand());
  }
}
