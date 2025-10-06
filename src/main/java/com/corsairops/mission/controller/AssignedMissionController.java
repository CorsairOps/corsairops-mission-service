package com.corsairops.mission.controller;

import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.service.AssignedMissionService;
import com.corsairops.mission.util.MissionMapper;
import com.corsairops.shared.annotations.CommonWriteResponses;
import com.corsairops.shared.dto.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Assigned Mission Management", description = "APIs for managing assigned missions to users")
@RestController
@RequestMapping("/api/missions/assigned-missions")
@RequiredArgsConstructor
public class AssignedMissionController {
    private final AssignedMissionService assignedMissionService;
    private final MissionMapper missionMapper;

    @Operation(summary = "Assign a mission to a user")
    @CommonWriteResponses
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignMissionToUser(@RequestParam Long missionId, @RequestParam String userId) {
        assignedMissionService.assignMissionToUser(missionId, userId);
    }

    @Operation(summary = "Get all missions assigned to the user making the request")
    @CommonWriteResponses
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getAllAssignedMissions(@RequestHeader("X-User-Id") String userId) {
        return assignedMissionService.getAllAssignedMissions(userId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }

    @Operation(summary = "Unassign a mission from a user")
    @CommonWriteResponses
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignMissionFromUser(@RequestParam Long missionId, @RequestParam String userId) {
        assignedMissionService.unassignMissionFromUser(missionId, userId);
    }

    @Operation(summary = "Get all users assigned to a specific mission")
    @CommonWriteResponses
    @GetMapping("/users/{missionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersAssignedToMission(@PathVariable Long missionId) {
        return assignedMissionService.getUsersAssignedToMission(missionId);
    }

    @Operation(summary = "Get all missions assigned to a specific user")
    @CommonWriteResponses
    @GetMapping("/missions/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getMissionsAssignedToUser(@PathVariable String userId) {
        return assignedMissionService.getAllAssignedMissions(userId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }
}