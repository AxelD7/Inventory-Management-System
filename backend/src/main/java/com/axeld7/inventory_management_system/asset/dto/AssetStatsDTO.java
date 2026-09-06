package com.axeld7.inventory_management_system.asset.dto;

public record AssetStatsDTO(
    long totalCount,
    long availableCount,
    long checkedOutCount,
    long damagedCount) {
}
