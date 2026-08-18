package com.axeld7.inventory_management_system.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetCirculationRepository extends JpaRepository<AssetCirculation, Long> {
    AssetCirculation findByAssetIdAndStatus(Long assetId, CirculationStatus status);
}