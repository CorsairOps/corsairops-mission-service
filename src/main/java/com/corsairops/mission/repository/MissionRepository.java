package com.corsairops.mission.repository;

import com.corsairops.mission.model.Mission;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    boolean existsByNameIgnoreCase(String name);
}