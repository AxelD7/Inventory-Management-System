package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.asset.dto.AssetStatsDTO;
import com.axeld7.inventory_management_system.asset.dto.AssetSummaryDTO;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Asset a WHERE a.id = :id")
  Optional<Asset> findByIdWithPessimisticLock(@Param("id") Long id);

  Boolean existsByAssetTag(String assetTag);

  Boolean existsByAssetTagAndIdNot(String assetTag, Long id);

  @Query(
      "SELECT new com.axeld7.inventory_management_system.asset.dto.AssetStatsDTO("
          + "COUNT(a),"
          + "COALESCE(SUM(CASE WHEN a.status = 'AVAILABLE' THEN 1 ELSE 0 END), 0),"
          + "COALESCE(SUM(CASE WHEN a.status = 'CHECKED_OUT' THEN 1 ELSE 0 END), 0),"
          + "COALESCE(SUM(CASE WHEN a.status = 'DAMAGED' THEN 1 ELSE 0 END), 0)"
          + ") "
          + "FROM Asset a")
  AssetStatsDTO getAssetStats();

  @Query(
      "SELECT new com.axeld7.inventory_management_system.asset.dto.AssetSummaryDTO(a.id,"
          + "a.assetTag,"
          + "a.name,"
          + "a.brand,"
          +  "a.status) "
          + "FROM Asset a "
          + "WHERE LOWER(a.assetTag) LIKE LOWER(CONCAT('%', :query, '%')) "
          + " OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) "
          + " OR LOWER(a.brand) LIKE LOWER(CONCAT('%', :query, '%')) "
          + " OR LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) "
          + "ORDER BY a.id DESC")
  Page<AssetSummaryDTO> findBySearch(@Param("query") String query,Pageable pageable);
}
