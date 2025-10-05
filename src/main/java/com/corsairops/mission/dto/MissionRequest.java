package com.corsairops.mission.dto;

import com.corsairops.mission.model.MissionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MissionRequest(
        @NotBlank(message = "Name is mandatory")
        String name,

        String description,

        @NotNull(message = "Priority is mandatory")
        @Min(1)
        @Max(5)
        Integer priority,

        @NotNull(message = "Start date is mandatory")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Status is mandatory")
        MissionStatus status


) {
}