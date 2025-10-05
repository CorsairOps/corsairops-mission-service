package com.corsairops.mission.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@IdClass(AssignedAssetId.class)
@Table(name = "assigned_assets")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AssignedAsset {

    @Id
    @Column(nullable = false)
    private String assetId;

    @Id
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @CreatedDate
    private LocalDateTime assignedAt;
}