package com.corsairops.mission.repository;

import com.corsairops.mission.model.AssignedAsset;
import com.corsairops.mission.model.AssignedAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignedAssetRepository extends JpaRepository<AssignedAsset, AssignedAssetId> {

    List<AssignedAsset> findByMissionId(Long missionId);

    List<AssignedAsset> findAllByAssetId(String assetId);
}