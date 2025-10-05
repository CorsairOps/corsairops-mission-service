package com.corsairops.mission.dto;

import com.corsairops.shared.dto.User;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MissionResponse(
        Long id,
        String name,
        String description,
        Integer priority,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        User createdBy,
        String createdAt,
        String updatedAt

) {

}