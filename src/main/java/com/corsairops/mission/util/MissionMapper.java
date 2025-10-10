package com.corsairops.mission.util;

import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.Mission;
import com.corsairops.shared.dto.User;
import com.corsairops.shared.exception.EncryptionException;
import com.corsairops.shared.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionMapper {
    private final UserServiceUtil userServiceUtil;
    private final EncryptionUtil encryptionUtil;

    public MissionResponse mapToMissionResponse(Mission mission) {
        decryptDescription(mission);
        return MissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .priority(mission.getPriority())
                .status(mission.getStatus().name())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .createdBy(userServiceUtil.fetchUserById(mission.getCreatedBy()))
                .createdAt(mission.getCreatedAt().toString())
                .updatedAt(mission.getUpdatedAt().toString())
                .build();
    }



    public List<MissionResponse> mapToMissionResponse(List<Mission> missions) {
        if (missions.isEmpty()) {
            return List.of();
        }
        missions.forEach(this::decryptDescription);
        Set<String> ids = extractUserIdsFromMissions(missions);
        Map<String, User> userMap;
        if (!ids.isEmpty()) {
            List<User> users = userServiceUtil.fetchUsersByIds(ids);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::id, user -> user));
        } else {
            userMap = new HashMap<>();
        }

        return missions.stream()
                .map(mission -> MissionResponse.builder()
                        .id(mission.getId())
                        .name(mission.getName())
                        .description(mission.getDescription())
                        .priority(mission.getPriority())
                        .status(mission.getStatus().name())
                        .startDate(mission.getStartDate())
                        .endDate(mission.getEndDate())
                        .createdBy(userMap.getOrDefault(mission.getCreatedBy(), userServiceUtil.getDefaultUser(mission.getCreatedBy())))
                        .createdAt(mission.getCreatedAt().toString())
                        .updatedAt(mission.getUpdatedAt().toString())
                        .build())
                .toList();
    }



    private Set<String> extractUserIdsFromMissions(List<Mission> missions) {
        return missions.stream()
                .map(Mission::getCreatedBy)
                .collect(Collectors.toSet());
    }

    private void decryptDescription(Mission mission) {
        try {
            String decryptedDescription = encryptionUtil.decryptString(mission.getDescription());
            mission.setDescription(decryptedDescription);
        } catch (EncryptionException e) {
            log.warn("Failed to decrypt mission description for mission ID {}: {}", mission.getId(), e.getCause().getMessage());
            // If decryption fails, retain the original encrypted description
        }
    }


}