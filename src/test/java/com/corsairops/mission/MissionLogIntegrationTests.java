package com.corsairops.mission;

import com.corsairops.mission.dto.MissionLogRequest;
import com.corsairops.mission.dto.MissionLogResponse;
import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.Mission;
import com.corsairops.mission.model.MissionStatus;
import com.corsairops.mission.repository.MissionLogRepository;
import com.corsairops.mission.repository.MissionRepository;
import com.corsairops.shared.dto.User;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
        "encryption.key=MySecretKey123456789012345678900"
})
public class MissionLogIntegrationTests {

    private static final String USER_ID = "test-user";
    private static final MissionRequest SAMPLE_MISSION_REQUEST = new MissionRequest(
            "Black Ops 1",
            "Stealth mission in enemy territory",
            1,
            LocalDate.now().plusDays(7),
            null,
            MissionStatus.PENDING
    );

    @LocalServerPort
    int port;

    @Autowired
    private MissionLogRepository missionLogRepository;

    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/missions";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void afterEach() {
        missionLogRepository.deleteAll();
        missionRepository.deleteAll();
    }

    @Test
    void givenValidRequest_whenCreateMissionLog_thenReturnCreated() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        MissionLogRequest missionLogRequest = new MissionLogRequest("Initial log entry");
        createMissionLog(missionId, missionLogRequest);
    }

    @Test
    void givenInvalidRequest_whenCreateMissionLog_thenReturnBadRequest() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        MissionLogRequest missionLogRequest = new MissionLogRequest("");
        jsonWithUser(missionLogRequest)
                .when()
                .post("/{missionId}/logs", missionId)
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvalidMissionId_whenCreateMissionLog_thenReturnNotFound() {
        MissionLogRequest missionLogRequest = new MissionLogRequest("Initial log entry");
        jsonWithUser(missionLogRequest)
                .when()
                .post("/{missionId}/logs", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    void givenValidMissionId_whenGetMissionLogs_thenReturnLogs() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        MissionLogRequest missionLogRequest1 = new MissionLogRequest("First log entry");
        MissionLogRequest missionLogRequest2 = new MissionLogRequest("Second log entry");
        createMissionLog(missionId, missionLogRequest1);
        createMissionLog(missionId, missionLogRequest2);

        jsonWithUser()
                .when()
                .get("/{missionId}/logs", missionId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].entry", equalTo("Second log entry"))
                .body("[1].entry", equalTo("First log entry"));
    }

    @Test
    void givenInvalidMissionId_whenGetMissionLogs_thenReturnNotFound() {
        jsonWithUser()
                .when()
                .get("/9999/logs")
                .then()
                .statusCode(404);
    }

    @Test
    void givenValidLogId_whenDeleteMissionLog_thenReturnNoContent() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        MissionLogRequest missionLogRequest = new MissionLogRequest("Log entry to delete");
        MissionLogResponse logResponse = createMissionLog(missionId, missionLogRequest);

        jsonWithUser()
                .when()
                .delete("/{missionId}/logs/{logId}", missionId, logResponse.id())
                .then()
                .statusCode(204);
    }

    @Test
    void givenInvalidLogId_whenDeleteMissionLog_thenReturnNotFound() {
        Long missionId = createMission(SAMPLE_MISSION_REQUEST).id();
        jsonWithUser()
                .when()
                .delete("/{missionId}/logs/{logId}", missionId, 9999)
                .then()
                .statusCode(404);
    }

    private MissionLogResponse createMissionLog(Long missionId, MissionLogRequest request) {
        return jsonWithUser(request)
                .when()
                .post("/{missionId}/logs", missionId)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("entry", equalTo(request.entry()))
                .body("createdBy.id", equalTo(USER_ID))
                .extract().as(MissionLogResponse.class);
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