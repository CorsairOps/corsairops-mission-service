package com.corsairops.mission.repository;

import com.corsairops.mission.model.AssignedMission;
import com.corsairops.mission.model.AssignedMissionId;
import com.corsairops.mission.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignedMissionRepository extends JpaRepository<AssignedMission, AssignedMissionId> {
    List<AssignedMission> findByUserId(String userId);

    boolean existsByUserIdAndMission(String userId, Mission mission);

    List<AssignedMission> findAllByMission(Mission mission);
}