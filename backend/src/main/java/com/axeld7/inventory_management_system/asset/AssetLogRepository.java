package com.axeld7.inventory_management_system.asset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetLogRepository extends JpaRepository<AssetLog, Long>{
    
    List<AssetLog> findByAssetIdOrderByCreatedAtDesc(Long assetId);


}
