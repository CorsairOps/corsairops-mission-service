package com.corsairops.mission.dto;

import com.corsairops.mission.model.MissionLog;

import java.time.LocalDateTime;

public record MissionLogResponse(
        Long id,
        Long missionId,
        String createdBy,
        String entry,
        LocalDateTime timestamp
) {
    public static MissionLogResponse from(MissionLog missionLog) {
        return new MissionLogResponse(
                missionLog.getId(),
                missionLog.getMission().getId(),
                missionLog.getCreatedBy(),
                missionLog.getEntry(),
                missionLog.getTimestamp()
        );
    }
}