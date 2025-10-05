package com.corsairops.mission.util;

import com.corsairops.mission.client.UserServiceClient;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.Mission;
import com.corsairops.shared.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MissionMapper {
    private final UserServiceClient userServiceClient;

    public MissionResponse mapToMissionResponse(Mission mission) {
        User user = userServiceClient.getUserById(mission.getCreatedBy());

        return MissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .priority(mission.getPriority())
                .status(mission.getStatus().name())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .createdBy(user)
                .createdAt(mission.getCreatedAt().toString())
                .updatedAt(mission.getUpdatedAt().toString())
                .build();
    }

    public List<MissionResponse> mapToMissionResponse(List<Mission> missions) {
        String ids = extractUserIdsFromMissions(missions);

        List<User> users = userServiceClient.getUsersByIds(ids);

        var userMap = users.stream()
                .collect(Collectors.toMap(User::id, user -> user));

        return missions.stream()
                .map(mission -> MissionResponse.builder()
                        .id(mission.getId())
                        .name(mission.getName())
                        .description(mission.getDescription())
                        .priority(mission.getPriority())
                        .status(mission.getStatus().name())
                        .startDate(mission.getStartDate())
                        .endDate(mission.getEndDate())
                        .createdBy(userMap.get(mission.getCreatedBy()))
                        .createdAt(mission.getCreatedAt().toString())
                        .updatedAt(mission.getUpdatedAt().toString())
                        .build())
                .toList();
    }

    private String extractUserIdsFromMissions(List<Mission> missions) {
        return missions.stream()
                .map(Mission::getCreatedBy)
                .distinct()
                .collect(Collectors.joining(","));
    }
}