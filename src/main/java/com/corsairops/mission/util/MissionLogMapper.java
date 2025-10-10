package com.corsairops.mission.util;

import com.corsairops.mission.dto.MissionLogResponse;
import com.corsairops.mission.model.MissionLog;
import com.corsairops.shared.dto.User;
import com.corsairops.shared.exception.EncryptionException;
import com.corsairops.shared.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionLogMapper {
    private final UserServiceUtil userServiceUtil;
    private final EncryptionUtil encryptionUtil;

    public MissionLogResponse mapToResponse(MissionLog missionLog) {
        User createdBy = userServiceUtil.fetchUserById(missionLog.getCreatedBy());
        decryptEntry(missionLog);
        return new MissionLogResponse(
                missionLog.getId(),
                missionLog.getMission().getId(),
                createdBy,
                missionLog.getEntry(),
                missionLog.getTimestamp()
        );
    }

    public List<MissionLogResponse> mapToResponseList(List<MissionLog> missionLogs) {
        try {
            if (missionLogs.isEmpty()) {
                return List.of();
            }

            missionLogs.forEach(this::decryptEntry);

            Set<String> ids = missionLogs.stream()
                    .map(MissionLog::getCreatedBy)
                    .collect(java.util.stream.Collectors.toSet());

            List<User> users = userServiceUtil.fetchUsersByIds(ids);
            Map<String, User> userMap;
            if (users.isEmpty()) {
                userMap = Map.of();
            } else {
                userMap = users.stream()
                        .collect(java.util.stream.Collectors.toMap(User::id, user -> user));
            }

            return missionLogs.stream()
                    .map(log -> new MissionLogResponse(
                            log.getId(),
                            log.getMission().getId(),
                            userMap.getOrDefault(log.getCreatedBy(), userServiceUtil.getDefaultUser(log.getCreatedBy())),
                            log.getEntry(),
                            log.getTimestamp()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void decryptEntry(MissionLog missionLog) {
        try {
            String decryptedEntry = encryptionUtil.decryptString(missionLog.getEntry());
            missionLog.setEntry(decryptedEntry);
        } catch (EncryptionException e) {
            log.warn("Failed to decrypt mission log entry with ID " + missionLog.getId() + ". Reason: " + e.getCause().getMessage());
        }
    }
}