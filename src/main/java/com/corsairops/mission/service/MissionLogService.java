package com.corsairops.mission.service;

import com.corsairops.mission.dto.MissionLogRequest;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.mission.repository.MissionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionLogService {
    private final MissionLogRepository missionLogRepository;
    private final MissionService missionService;

    @Transactional
    public MissionLog createMissionLog(MissionLogRequest missionLogRequest, Long missionId, String createdBy) {
        Mission mission = missionService.getMissionById(missionId);
        MissionLog missionLog = MissionLog.builder()
                .mission(mission)
                .entry(missionLogRequest.entry())
                .createdBy(createdBy)
                .build();
        return missionLogRepository.save(missionLog);
    }

    @Transactional(readOnly = true)
    public List<MissionLog> getMissionLogsForMissionSorted(Long missionId) {
        return missionLogRepository.findAllByMissionIdOrderByTimestampDesc(missionId);
    }

    @Transactional
    public void deleteMissionLogById(Long id) {
        missionLogRepository.deleteById(id);
    }
}