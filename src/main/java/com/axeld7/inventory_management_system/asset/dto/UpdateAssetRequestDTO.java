package com.axeld7.inventory_management_system.asset.dto;

import com.axeld7.inventory_management_system.asset.AssetStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAssetRequestDTO {

    private String assetTag;
    private String name;
    private String brand;
    private String description;
    private AssetStatus status;

}
