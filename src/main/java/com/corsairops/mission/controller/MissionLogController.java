package com.corsairops.mission.controller;

import com.corsairops.mission.dto.MissionLogRequest;
import com.corsairops.mission.dto.MissionLogResponse;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.mission.service.MissionLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions/{missionId}/logs")
@RequiredArgsConstructor
public class MissionLogController {
    private final MissionLogService missionLogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MissionLogResponse createMissionLog
            (@RequestBody @Valid MissionLogRequest missionLogRequest,
             @PathVariable Long missionId,
             @RequestHeader("X-User-Id") String createdBy) {
        MissionLog log = missionLogService.createMissionLog(missionLogRequest, missionId, createdBy);
        return MissionLogResponse.from(log);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MissionLogResponse> getMissionLogsForMission(@PathVariable Long missionId) {
        List<MissionLog> logs = missionLogService.getMissionLogsForMissionSorted(missionId);
        return logs.stream()
                .map(MissionLogResponse::from)
                .toList();
    }

    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMissionLog(@PathVariable Long logId) {
        missionLogService.deleteMissionLogById(logId);
    }

}