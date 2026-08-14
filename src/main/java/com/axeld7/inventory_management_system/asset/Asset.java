package com.axeld7.inventory_management_system.asset;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="assets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="asset_tag", nullable=false, unique=true)
    private String assetTag;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="brand")
    private String brand;

    @Column(name="description")
    private String description;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private AssetStatus status;
}
