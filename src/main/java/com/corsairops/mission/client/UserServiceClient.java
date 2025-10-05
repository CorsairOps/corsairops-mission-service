package com.corsairops.mission.client;

import com.corsairops.shared.dto.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface UserServiceClient {

    @GetExchange("/api/users/{id}")
    User getUserById(@PathVariable String id);

    @GetExchange("/api/users/ids")
    List<User> getUsersByIds(@RequestParam String ids);
}