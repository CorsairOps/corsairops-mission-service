package com.corsairops.mission.repository;

import com.corsairops.mission.model.MissionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionLogRepository extends JpaRepository<MissionLog, Long> {
    List<MissionLog> findAllByMissionIdOrderByTimestampDesc(Long missionId);
}