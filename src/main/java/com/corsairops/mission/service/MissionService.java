package com.corsairops.mission.service;

import com.corsairops.mission.client.UserServiceClient;
import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.exception.InvalidMissionDateException;
import com.corsairops.mission.exception.MissionNameConflictException;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.model.MissionStatus;
import com.corsairops.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    @Transactional
    public Mission createMission(MissionRequest missionRequest, String userId) {
        validateMissionRequest(missionRequest);

        Mission mission = Mission.builder()
                .name(missionRequest.name())
                .description(missionRequest.description())
                .priority(missionRequest.priority())
                .startDate(missionRequest.startDate())
                .endDate(missionRequest.endDate())
                .status(MissionStatus.PENDING)
                .createdBy(userId)
                .build();

        return missionRepository.save(mission);
    }

    private void validateMissionRequest(MissionRequest missionRequest) {
        if (missionRepository.existsByNameIgnoreCase(missionRequest.name())) {
            throw new MissionNameConflictException("Mission name already exists", HttpStatus.CONFLICT);
        }

        if (missionRequest.endDate() != null && missionRequest.endDate().isBefore(missionRequest.startDate())) {
            throw new InvalidMissionDateException("End date cannot be before start date", HttpStatus.BAD_REQUEST);
        }
    }

}