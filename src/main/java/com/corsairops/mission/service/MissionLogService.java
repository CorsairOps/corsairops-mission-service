package com.corsairops.mission.service;

import com.corsairops.mission.dto.MissionLogRequest;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.mission.repository.MissionLogRepository;
import com.corsairops.shared.exception.HttpResponseException;
import com.corsairops.shared.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionLogService {
    private final MissionLogRepository missionLogRepository;
    private final MissionService missionService;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public MissionLog createMissionLog(MissionLogRequest missionLogRequest, Long missionId, String createdBy) {
        Mission mission = missionService.getMissionById(missionId);
        MissionLog missionLog = MissionLog.builder()
                .mission(mission)
                .entry(missionLogRequest.entry())
                .createdBy(createdBy)
                .build();

        encryptEntry(missionLog);
        return missionLogRepository.save(missionLog);
    }

    private void encryptEntry(MissionLog missionLog) {
        String encryptedEntry = encryptionUtil.encryptString(missionLog.getEntry());
        missionLog.setEntry(encryptedEntry);
    }

    @Transactional(readOnly = true)
    public List<MissionLog> getMissionLogsForMissionSorted(Long missionId) {
        Mission mission = missionService.getMissionById(missionId);
        return missionLogRepository.findAllByMissionIdOrderByTimestampDesc(mission.getId());
    }

    @Transactional
    public void deleteMissionLogById(Long id) {
        if (!missionLogRepository.existsById(id)) {
            throw new HttpResponseException("Mission log with ID " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }
        missionLogRepository.deleteById(id);
    }
}