package com.corsairops.mission.controller;

import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.service.AssignedMissionService;
import com.corsairops.mission.util.MissionMapper;
import com.corsairops.shared.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions/assigned-missions")
@RequiredArgsConstructor
public class AssignedMissionController {
    private final AssignedMissionService assignedMissionService;
    private final MissionMapper missionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignMissionToUser(@RequestParam Long missionId, @RequestParam String userId) {
        assignedMissionService.assignMissionToUser(missionId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getAllAssignedMissions(@RequestHeader("X-User-Id") String userId) {
        return assignedMissionService.getAllAssignedMissions(userId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignMissionFromUser(@RequestParam Long missionId, @RequestParam String userId) {
        assignedMissionService.unassignMissionFromUser(missionId, userId);
    }

    @GetMapping("/users/{missionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersAssignedToMission(@PathVariable Long missionId) {
        return assignedMissionService.getUsersAssignedToMission(missionId);
    }

    @GetMapping("/missions/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getMissionsAssignedToUser(@PathVariable String userId) {
        return assignedMissionService.getAllAssignedMissions(userId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }
}