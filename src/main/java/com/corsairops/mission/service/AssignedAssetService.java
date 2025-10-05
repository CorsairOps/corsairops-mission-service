package com.corsairops.mission.service;

import com.corsairops.mission.client.asset.AssetResponse;
import com.corsairops.mission.client.asset.AssetServiceClient;
import com.corsairops.mission.model.AssignedAsset;
import com.corsairops.mission.model.AssignedAssetId;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.repository.AssignedAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignedAssetService {
    private final AssignedAssetRepository assignedAssetRepository;
    private final AssetServiceClient assetServiceClient;
    private final MissionService missionService;

    @Transactional
    public void assignAssetToMission(String assetId, Long missionId) {
        assetServiceClient.getAssetById(UUID.fromString(assetId));
        Mission mission = missionService.getMissionById(missionId);

        AssignedAsset assignedAsset = AssignedAsset.builder()
                .assetId(assetId)
                .mission(mission)
                .build();
        assignedAssetRepository.save(assignedAsset);
    }

    @Transactional
    public void unassignAssetFromMission(String assetId, Long missionId) {
        Mission mission = missionService.getMissionById(missionId);
        AssignedAssetId id = new AssignedAssetId(assetId, mission);
        assignedAssetRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> getAssignedAssetsByMissionId(Long missionId) {
        // Find all assigned assets for the given mission ID
        List<AssignedAsset> assignedAssets = assignedAssetRepository.findByMissionId(missionId);

        // Extract asset IDs from the assigned assets
        String assetIds = String.join(",", assignedAssets.stream()
                .map(AssignedAsset::getAssetId)
                .toList());
        // Fetch asset details from the Asset Service
        return assetServiceClient.getAssetsByIds(assetIds);
    }

    @Transactional(readOnly = true)
    public List<Mission> getMissionsByAssetId(String assetId) {
        List<AssignedAsset> assignedAssets = assignedAssetRepository.findAllByAssetId(assetId);
        return assignedAssets.stream()
                .map(AssignedAsset::getMission)
                .toList();
    }
}