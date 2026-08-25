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

@Entity
@Table(name = "asset_circulations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetCirculation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id", nullable = false)
  private Asset asset;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "borrower_id", nullable = false)
  private User borrower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checked_out_by", nullable = false)
  private User checkedOutBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checked_in_by", nullable = true)
  private User checkedInBy;

  @Column(name = "due_date", nullable = false)
  private Instant dueDate;

  @Column(name = "returned_at", nullable = true)
  private Instant returnedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private CirculationStatus status;

  @Column(name =  "is_damaged", nullable = true)
  private Boolean isDamaged;

  @Column(name = "notes", nullable = true)
  private String notes;
}