package com.axeld7.inventory_management_system.asset;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetCirculationRepository extends JpaRepository<AssetCirculation, Long> {
  Optional<AssetCirculation> findByAssetIdAndStatus(Long assetId, CirculationStatus status);

  List<AssetCirculation> findAllByBorrowerIdAndStatus(Long borrowerId, CirculationStatus status);
}
