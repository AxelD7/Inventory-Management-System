package com.axeld7.inventory_management_system.asset;

import jakarta.persistence.LockModeType;
import java.util.Optional;
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
}