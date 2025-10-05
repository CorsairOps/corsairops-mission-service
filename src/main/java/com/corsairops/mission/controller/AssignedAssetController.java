package com.corsairops.mission.controller;

import com.corsairops.mission.client.asset.AssetResponse;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.service.AssignedAssetService;
import com.corsairops.mission.util.MissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions/assigned-assets")
@RequiredArgsConstructor
public class AssignedAssetController {
    private final AssignedAssetService assignedAssetService;
    private final MissionMapper missionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void assignAssetToMission(@RequestParam String assetId, @RequestParam Long missionId) {
        assignedAssetService.assignAssetToMission(assetId, missionId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignAssetFromMission(@RequestParam String assetId, @RequestParam Long missionId) {
        assignedAssetService.unassignAssetFromMission(assetId, missionId);
    }

    @GetMapping("/assets/{missionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AssetResponse> getAssignedAssetsByMissionId(@PathVariable Long missionId) {
        return assignedAssetService.getAssignedAssetsByMissionId(missionId);
    }

    @GetMapping("/missions/{assetId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MissionResponse> getMissionsByAssetId(@PathVariable String assetId) {
        return assignedAssetService.getMissionsByAssetId(assetId).stream()
                .map(missionMapper::mapToMissionResponse)
                .toList();
    }
}