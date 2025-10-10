package com.corsairops.mission.controller;

import com.corsairops.mission.dto.MissionLogRequest;
import com.corsairops.mission.dto.MissionLogResponse;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.mission.service.MissionLogService;
import com.corsairops.mission.util.MissionLogMapper;
import com.corsairops.shared.annotations.CommonReadResponses;
import com.corsairops.shared.annotations.CommonWriteResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mission Log Management", description = "APIs for managing mission logs")
@RestController
@RequestMapping("/api/missions/{missionId}/logs")
@RequiredArgsConstructor
public class MissionLogController {
    private final MissionLogService missionLogService;
    private final MissionLogMapper missionLogMapper;

    @Operation(summary = "Create a new mission log for a specific mission")
    @CommonWriteResponses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MissionLogResponse createMissionLog
            (@RequestBody @Valid MissionLogRequest missionLogRequest,
             @PathVariable Long missionId,
             @RequestHeader("X-User-Id") String createdBy) {
        MissionLog log = missionLogService.createMissionLog(missionLogRequest, missionId, createdBy);
        return missionLogMapper.mapToResponse(log);
    }

    @Operation(summary = "Get all mission logs for a specific mission, sorted by timestamp descending")
    @CommonReadResponses
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MissionLogResponse> getMissionLogsForMission(@PathVariable Long missionId) {
        List<MissionLog> logs = missionLogService.getMissionLogsForMissionSorted(missionId);
        return missionLogMapper.mapToResponseList(logs);
    }

    @Operation(summary = "Delete a specific mission log by its ID")
    @CommonWriteResponses
    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMissionLog(@PathVariable Long logId, @PathVariable Long missionId) {
        missionLogService.deleteMissionLogById(logId);
    }

}