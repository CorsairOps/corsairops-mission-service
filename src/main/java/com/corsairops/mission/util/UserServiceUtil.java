package com.corsairops.mission.util;

import com.corsairops.mission.client.user.UserServiceClient;
import com.corsairops.shared.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceUtil {

    private final UserServiceClient userServiceClient;

    public User fetchUserById(String userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (Exception e) {
            log.error("Error fetching user with ID {}: {}", userId, e.getMessage());
            // Return a placeholder user
            return getDefaultUser(userId);
        }
    }

    public List<User> fetchUsersByIds(Set<String> ids) {
        try {
            String idString = String.join(",", ids);
            return userServiceClient.getUsersByIds(idString, true);
        } catch (Exception e) {
            log.error("Error fetching users with IDs {}: {}", ids, e.getMessage());
            return ids.stream()
                    .map(this::getDefaultUser)
                    .toList();
        }
    }

    public User getDefaultUser(String userId) {
        return new User(userId, null, null, null, null, true, null, List.of());
    }
}