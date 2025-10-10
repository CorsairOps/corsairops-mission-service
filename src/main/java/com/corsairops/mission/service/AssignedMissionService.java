package com.corsairops.mission.service;

import com.corsairops.mission.client.user.UserServiceClient;
import com.corsairops.mission.model.AssignedMission;
import com.corsairops.mission.model.AssignedMissionId;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.repository.AssignedMissionRepository;
import com.corsairops.mission.util.UserServiceUtil;
import com.corsairops.shared.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssignedMissionService {
    private final AssignedMissionRepository assignedMissionRepository;
    private final MissionService missionService;
    private final UserServiceClient userServiceClient;
    private final UserServiceUtil userServiceUtil;

    @Transactional(readOnly = true)
    public List<Mission> getAllAssignedMissions(String userId) {
        return assignedMissionRepository.findByUserId(userId).stream()
                .map(AssignedMission::getMission)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<User> getUsersAssignedToMission(Long missionId) {
        Mission mission = missionService.getMissionById(missionId);
        List<AssignedMission> assignedMissions = assignedMissionRepository.findAllByMission(mission);

        if (assignedMissions.isEmpty()) {
            return List.of();
        }

        Set<String> userIds = assignedMissions.stream()
                .map(AssignedMission::getUserId)
                .collect(java.util.stream.Collectors.toSet());
        return userServiceUtil.fetchUsersByIds(userIds);
    }

    @Transactional
    public void assignMissionToUser(Long missionId, String userId) {
        Mission mission = missionService.getMissionById(missionId);

        // Validate user exists
        User user = userServiceClient.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }

        boolean alreadyAssigned = assignedMissionRepository.existsByUserIdAndMission(userId, mission);
        if (alreadyAssigned) {
            return;
        }

        AssignedMission assignedMission = AssignedMission.builder()
                .userId(userId)
                .mission(mission)
                .build();
        assignedMissionRepository.save(assignedMission);
    }

    @Transactional
    public void unassignMissionFromUser(Long missionId, String userId) {
        Mission mission = missionService.getMissionById(missionId);
        AssignedMissionId id = new AssignedMissionId(userId, mission);
        assignedMissionRepository.deleteById(id);
    }
}