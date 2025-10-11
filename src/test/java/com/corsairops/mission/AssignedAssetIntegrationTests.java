package com.corsairops.mission;

import com.corsairops.mission.client.asset.AssetResponse;
import com.corsairops.mission.client.asset.AssetServiceClient;
import com.corsairops.mission.client.asset.AssetStatus;
import com.corsairops.mission.client.asset.AssetType;
import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.model.MissionStatus;
import com.corsairops.mission.repository.AssignedAssetRepository;
import com.corsairops.mission.repository.MissionRepository;
import com.corsairops.shared.dto.User;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.View;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
        "encryption.key=MySecretKey123456789012345678900"
})
public class AssignedAssetIntegrationTests {
    private final static MissionRequest SAMPLE_MISSION_REQUEST = new MissionRequest(
            "Recon Mission",
            "Gather intelligence on enemy positions",
            1,
            LocalDate.now().plusDays(5),
            null,
            MissionStatus.PENDING);
    private final static String USER_ID = "test-user";

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AssignedAssetRepository assignedAssetRepository;

    @MockitoBean
    private AssetServiceClient assetServiceClient;

    @LocalServerPort
    int port;
    @Qualifier("jacksonObjectMapper")
    @Autowired
    private Object object;
    @Autowired
    private View error;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/missions/assigned-assets";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void cleanUp() {
        missionRepository.deleteAll();
        assignedAssetRepository.deleteAll();
    }

    @Test
    void givenInvalidAsset_whenAssignAssetToMission_thenNotFound() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        String assetId = UUID.randomUUID().toString();

        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        jsonWithUser()
                .queryParam("assetId", assetId)
                .queryParam("missionId", missionId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    void givenInvalidMission_whenAssignAssetToMission_thenNotFound() {
        Long missionId = 9999L;
        String assetId = UUID.randomUUID().toString();

        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId)))
                .thenReturn(new AssetResponse(UUID.fromString(assetId), "Tank A1", AssetType.GROUND_VEHICLE, AssetStatus.ACTIVE, -90.0, 0.0, null, null));

        jsonWithUser()
                .queryParam("assetId", assetId)
                .queryParam("missionId", missionId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    void givenValidRequest_whenAssignAssetToMission_thenCreated() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        String assetId = UUID.randomUUID().toString();

        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId)))
                .thenReturn(new AssetResponse(UUID.fromString(assetId), "Tank A1", AssetType.GROUND_VEHICLE, AssetStatus.ACTIVE, -90.0, 0.0, null, null));

        assignAsset(missionId, assetId);
    }

    @Test
    void givenInvalidMission_whenUnassignAssetFromMission_thenNotFound() {
        Long missionId = 9999L;
        String assetId = UUID.randomUUID().toString();

        jsonWithUser()
                .queryParam("assetId", assetId)
                .queryParam("missionId", missionId)
                .when()
                .delete()
                .then()
                .statusCode(404);
    }

    @Test
    void givenValidRequest_whenUnassignAssetFromMission_thenNoContent() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        String assetId = UUID.randomUUID().toString();

        jsonWithUser()
                .queryParam("assetId", assetId)
                .queryParam("missionId", missionId)
                .when()
                .delete()
                .then()
                .statusCode(204);
    }

    @Test
    void givenValidMissionId_whenGetAssignedAssetsByMissionId_thenReturnAssets() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        String assetId1 = UUID.randomUUID().toString();
        String assetId2 = UUID.randomUUID().toString();

        AssetResponse asset1 = new AssetResponse(UUID.fromString(assetId1), "Tank A1", AssetType.GROUND_VEHICLE, AssetStatus.ACTIVE, -90.0, 0.0, null, null);
        AssetResponse asset2 = new AssetResponse(UUID.fromString(assetId2), "Drone D1", AssetType.DRONE, AssetStatus.ACTIVE, 45.0, 10.0, null, null);

        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId1)))
                .thenReturn(asset1);
        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId2)))
                .thenReturn(asset2);

        Mockito.when(assetServiceClient.getAssetsByIds(Mockito.any()))
                .thenReturn(List.of(asset1, asset2));

        // Assign assets to the mission
        assignAsset(missionId, assetId1);
        assignAsset(missionId, assetId2);

        jsonWithUser()
                .get("/assets/{missionId}", missionId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].id", anyOf(equalTo(UUID.fromString(assetId1).toString()), equalTo(UUID.fromString(assetId2).toString())))
                .body("[1].id", anyOf(equalTo(UUID.fromString(assetId1).toString()), equalTo(UUID.fromString(assetId2).toString())));

    }

    @Test
    void givenAssetAssignedToMissions_whenGetMissionsByAssetId_thenReturnMissions() {
        // Create two missions
        MissionResponse mission1 = createMission(SAMPLE_MISSION_REQUEST);
        MissionResponse mission2 = createMission(new MissionRequest(
                "Black Ops 2",
                "Reconnaissance mission",
                2,
                LocalDate.now().plusDays(10),
                null,
                MissionStatus.PENDING
        ));

        String assetId = UUID.randomUUID().toString();

        // Mock the asset service client for both assign operations
        Mockito.when(assetServiceClient.getAssetById(UUID.fromString(assetId)))
                .thenReturn(new AssetResponse(UUID.fromString(assetId), "Test Asset", AssetType.DRONE, AssetStatus.ACTIVE, 90.5, 45.0, null, null));

        // Assign the same asset to both missions
        assignAsset(mission1.id(), assetId);
        assignAsset(mission2.id(), assetId);

        // Get missions by asset ID
        given()
                .header("X-User-Id", USER_ID)
                .when()
                .get("/missions/" + assetId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", anyOf(equalTo(mission1.id().intValue()), equalTo(mission2.id().intValue())))
                .body("[1].id", anyOf(equalTo(mission1.id().intValue()), equalTo(mission2.id().intValue())));
    }


    private void assignAsset(Long missionId, String assetId) {
        jsonWithUser()
                .queryParam("assetId", assetId)
                .queryParam("missionId", missionId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    private MissionResponse createMission(MissionRequest request) {
        Mission mockMission = Mission.builder()
                .name(request.name())
                .description(request.description())
                .priority(request.priority())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(request.status())
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mission savedMission = missionRepository.save(mockMission);

        User testUser = new User(USER_ID, null, null, null, null, true, null, null);

        return new MissionResponse(savedMission.getId(),
                savedMission.getName(),
                savedMission.getDescription(),
                savedMission.getPriority(),
                savedMission.getStartDate(),
                savedMission.getEndDate(),
                savedMission.getStatus().name(),
                testUser,
                savedMission.getCreatedAt().toString(),
                savedMission.getUpdatedAt().toString()
        );
    }

    private RequestSpecification jsonWithUser() {
        return given().header("X-User-Id", USER_ID).contentType("application/json");
    }

    private RequestSpecification jsonWithUser(Object body) {
        return jsonWithUser().body(body);
    }
}