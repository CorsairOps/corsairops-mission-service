package com.corsairops.mission.service;

import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.exception.InvalidMissionDateException;
import com.corsairops.mission.exception.MissionNameConflictException;
import com.corsairops.mission.exception.MissionNotFoundException;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.repository.MissionRepository;
import com.corsairops.shared.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

    private final MissionRepository missionRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional(readOnly = true)
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new MissionNotFoundException("Mission not found", HttpStatus.NOT_FOUND));
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
                .status(missionRequest.status())
                .createdBy(userId)
                .build();

        encryptDescription(mission);

        return missionRepository.save(mission);
    }

    @Transactional
    public Mission updateMission(MissionRequest missionRequest, Long id) {
        Mission existingMission = getMissionById(id);
        validateMissionRequest(missionRequest, id);

        existingMission.setName(missionRequest.name());
        existingMission.setDescription(missionRequest.description());
        existingMission.setPriority(missionRequest.priority());
        existingMission.setStartDate(missionRequest.startDate());
        existingMission.setEndDate(missionRequest.endDate());
        existingMission.setStatus(missionRequest.status());

        encryptDescription(existingMission);
        return missionRepository.save(existingMission);
    }

    @Transactional
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new MissionNotFoundException("Mission not found", HttpStatus.NOT_FOUND);
        }
        missionRepository.deleteById(id);
    }

    private void validateMissionRequest(MissionRequest missionRequest) {
        if (missionRepository.existsByNameIgnoreCase(missionRequest.name())) {
            throw new MissionNameConflictException("Mission name already exists", HttpStatus.CONFLICT);
        }

        if (missionRequest.endDate() != null && missionRequest.endDate().isBefore(missionRequest.startDate())) {
            throw new InvalidMissionDateException("End date cannot be before start date", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateMissionRequest(MissionRequest missionRequest, Long id) {
        if (missionRepository.existsByNameIgnoreCaseAndIdNot(missionRequest.name(), id)) {
            throw new MissionNameConflictException("Mission name already exists", HttpStatus.CONFLICT);
        }

        if (missionRequest.endDate() != null && missionRequest.endDate().isBefore(missionRequest.startDate())) {
            throw new InvalidMissionDateException("End date cannot be before start date", HttpStatus.BAD_REQUEST);
        }
    }

    private void encryptDescription(Mission mission) {
        String encryptedDescription = encryptionUtil.encryptString(mission.getDescription());
        mission.setDescription(encryptedDescription);
    }


}