package com.axeld7.inventory_management_system.asset;

import org.springframework.data.jpa.repository.JpaRepository;

interface AssetRepository extends JpaRepository<Asset, Long> {

  Boolean existsByAssetTag(String assetTag);

  Boolean existsByAssetTagAndIdNot(String assetTag, Long id);
}
