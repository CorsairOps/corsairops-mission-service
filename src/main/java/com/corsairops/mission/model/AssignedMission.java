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
@Table(name = "assigned_missions")
@EntityListeners(AuditingEntityListener.class)
@Data @AllArgsConstructor @NoArgsConstructor @Builder
@IdClass(AssignedMissionId.class)
public class AssignedMission {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @CreatedDate
    private LocalDateTime assignedAt;
}