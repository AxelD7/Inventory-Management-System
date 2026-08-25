package com.axeld7.inventory_management_system.asset.event;

import com.axeld7.inventory_management_system.asset.Asset;
import com.axeld7.inventory_management_system.asset.AssetLog;
import com.axeld7.inventory_management_system.asset.AssetLogRepository;
import com.axeld7.inventory_management_system.asset.AssetRepository;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class AssetLogEventListener {

  private static final Logger log = LoggerFactory.getLogger(AssetLogEventListener.class);

  private final AssetRepository assetRepository;
  private final AssetLogRepository logRepository;
  private final UserRepository userRepository;

  @Async("auditLogExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleAssetLogEvent(AssetLogEvent event) {
    log.info(
        "Processing AssetLogEvent asyncronously on thread [{}] for Asset ID: {}",
        Thread.currentThread().getName(),
        event.assetId());

    Asset asset = assetRepository.findById(event.assetId()).orElse(null);
    User employee =
        event.employeeId() != null
            ? userRepository.findById(event.employeeId()).orElse(null)
            : null;

    User patron =
        event.patronId() != null
            ? userRepository.findById(event.patronId()).orElse(null)
            : null;

    if (asset == null) {
      log.error("Failed to write audit log. Asset ID {} not found", event.assetId());
      return;
    }

    AssetLog assetLog = new AssetLog();
    assetLog.setAsset(asset);
    assetLog.setEmployee(employee);
    assetLog.setPatron(patron);
    assetLog.setAction(event.action());
    assetLog.setNotes(event.notes());
    assetLog.setCreatedAt(event.timestamp());

    logRepository.save(assetLog);
  }
}
