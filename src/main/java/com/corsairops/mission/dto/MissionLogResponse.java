package com.corsairops.mission.dto;

import com.corsairops.shared.dto.User;

import java.time.LocalDateTime;

public record MissionLogResponse(
        Long id,
        Long missionId,
        User createdBy,
        String entry,
        LocalDateTime timestamp
) {

}