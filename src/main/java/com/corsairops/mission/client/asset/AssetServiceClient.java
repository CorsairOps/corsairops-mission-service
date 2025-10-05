package com.corsairops.mission.client.asset;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

public interface AssetServiceClient {

    @GetExchange("/api/assets/{id}")
    AssetResponse getAssetById(@PathVariable("id") UUID id);

    @GetExchange("/api/assets/ids")
    List<AssetResponse> getAssetsByIds(@RequestParam String ids);
}