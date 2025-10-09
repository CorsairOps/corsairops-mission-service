package com.corsairops.mission.controller;

import com.corsairops.mission.client.asset.AssetResponse;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.service.AssignedAssetService;
import com.corsairops.mission.util.MissionMapper;
import com.corsairops.shared.annotations.CommonReadResponses;
import com.corsairops.shared.annotations.CommonWriteResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Assigned Asset Management", description = "APIs for managing assigned assets to missions")
@RestController
@RequestMapping("/api/missions/assigned-assets")
@RequiredArgsConstructor
public class AssignedAssetController {
    private final AssignedAssetService assignedAssetService;
    private final MissionMapper missionMapper;

    @Operation(summary = "Assign an asset to a mission")
    @CommonWriteResponses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void assignAssetToMission(@RequestParam String assetId, @RequestParam Long missionId) {
        assignedAssetService.assignAssetToMission(assetId, missionId);
    }

    @Operation(summary = "Unassign an asset from a mission")
    @CommonWriteResponses
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignAssetFromMission(@RequestParam String assetId, @RequestParam Long missionId) {
        assignedAssetService.unassignAssetFromMission(assetId, missionId);
    }

    @Operation(summary = "Get all assets assigned to a specific mission")
    @CommonReadResponses
    @GetMapping("/assets/{missionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AssetResponse> getAssignedAssetsByMissionId(@PathVariable Long missionId) {
        return assignedAssetService.getAssignedAssetsByMissionId(missionId);
    }

    @Operation(summary = "Get all missions assigned to a specific asset")
    @CommonReadResponses
    @GetMapping("/missions/{assetId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getAssignedMissionsByAssetId(@PathVariable String assetId) {
        return assignedAssetService.getMissionsByAssetId(assetId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }
}