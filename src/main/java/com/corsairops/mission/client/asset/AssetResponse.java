package com.corsairops.mission.client.asset;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssetResponse(
        UUID id,
        String name,
        AssetType type,
        AssetStatus status,
        Double longitude,
        Double latitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}