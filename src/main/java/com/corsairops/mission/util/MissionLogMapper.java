package com.corsairops.mission.util;

import com.corsairops.mission.client.user.UserServiceClient;
import com.corsairops.mission.dto.MissionLogResponse;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.shared.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MissionLogMapper {
    private final UserServiceClient userServiceClient;

    public MissionLogResponse mapToResponse(MissionLog missionLog) {
        try {
            User createdBy = userServiceClient.getUserById(missionLog.getCreatedBy());
            return new MissionLogResponse(
                    missionLog.getId(),
                    missionLog.getMission().getId(),
                    createdBy,
                    missionLog.getEntry(),
                    missionLog.getTimestamp()
            );
        } catch (Exception e) {
            return new MissionLogResponse(
                    missionLog.getId(),
                    missionLog.getMission().getId(),
                    null,
                    missionLog.getEntry(),
                    missionLog.getTimestamp()
            );
        }
    }

    public List<MissionLogResponse> mapToResponseList(List<MissionLog> missionLogs) {
        try {
            if (missionLogs.isEmpty()) {
                return List.of();
            }

            String ids = missionLogs.stream()
                    .map(MissionLog::getCreatedBy)
                    .distinct()
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            List<User> users = userServiceClient.getUsersByIds(ids, true);
            Map<String, User> userMap = users.stream()
                    .collect(java.util.stream.Collectors.toMap(User::id, user -> user));

            return missionLogs.stream()
                    .map(log -> new MissionLogResponse(
                            log.getId(),
                            log.getMission().getId(),
                            userMap.getOrDefault(log.getCreatedBy(), null),
                            log.getEntry(),
                            log.getTimestamp()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}