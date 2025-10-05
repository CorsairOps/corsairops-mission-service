package com.corsairops.mission.dto;

import jakarta.validation.constraints.NotBlank;

public record MissionLogRequest(
        @NotBlank(message = "Entry is required")
        String entry
) {
}