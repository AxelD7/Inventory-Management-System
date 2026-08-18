package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.AssetStatus;

public record UpdateAssetRequestDTO(
    String assetTag, String name, String brand, String description, AssetStatus status) {}
