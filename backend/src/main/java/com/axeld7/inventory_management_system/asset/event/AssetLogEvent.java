package com.axeld7.inventory_management_system.asset.event;

import java.time.Instant;

import com.axeld7.inventory_management_system.asset.AssetAction;

public record AssetLogEvent(Long assetId, Long employeeId, Long patronId, AssetAction action, String notes, Instant timestamp) {

    public AssetLogEvent {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

}
