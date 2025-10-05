package com.corsairops.mission.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AssignedAssetId {
    private String assetId;
    private Mission mission;
}