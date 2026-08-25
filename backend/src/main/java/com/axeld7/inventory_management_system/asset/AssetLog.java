package com.axeld7.inventory_management_system.asset;

import com.axeld7.inventory_management_system.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "asset_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id", nullable = false)
  private Asset asset;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private User employee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patron_id", nullable = true)
  private User patron;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50)
  private AssetAction action;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;
}
