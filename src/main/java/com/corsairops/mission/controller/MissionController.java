package com.corsairops.mission.controller;

import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.service.MissionService;
import com.corsairops.mission.util.MissionMapper;
import com.corsairops.shared.annotations.CommonReadResponses;
import com.corsairops.shared.annotations.CommonWriteResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mission Management", description = "APIs for managing missions")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;
    private final MissionMapper missionMapper;

    @Operation(summary = "Get all missions")
    @CommonReadResponses
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getAllMissions() {
        List<Mission> missions = missionService.getAllMissions();
        return missionMapper.mapToMissionResponse(missions);
    }

    @Operation(summary = "Get a mission by ID")
    @CommonReadResponses
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MissionResponse getMissionById(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        return missionMapper.mapToMissionResponse(mission);
    }

    @Operation(summary = "Get count of all missions")
    @CommonReadResponses
    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public Long getMissionCount() {
        return missionService.getMissionCount();
    }

    @Operation(summary = "Create a new mission")
    @CommonWriteResponses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MissionResponse createMission(@RequestBody @Valid MissionRequest missionRequest,
                                         @RequestHeader("X-User-Id") String userId) {
        Mission mission = missionService.createMission(missionRequest, userId);
        return missionMapper.mapToMissionResponse(mission);
    }

    @Operation(summary = "Update an existing mission")
    @CommonWriteResponses
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MissionResponse updateMission(@RequestBody @Valid MissionRequest missionRequest,
                                            @PathVariable Long id) {
        Mission mission = missionService.updateMission(missionRequest, id);
        return missionMapper.mapToMissionResponse(mission);
    }

    @Operation(summary = "Delete a mission by ID")
    @CommonWriteResponses
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
    }
}