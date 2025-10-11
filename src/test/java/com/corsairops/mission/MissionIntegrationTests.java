package com.corsairops.mission;

import com.corsairops.mission.dto.MissionRequest;
import com.corsairops.mission.dto.MissionResponse;
import com.corsairops.mission.model.MissionStatus;
import com.corsairops.mission.repository.MissionRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
        "encryption.key=MySecretKey123456789012345678900"
})
public class MissionIntegrationTests {

    private static final List<MissionRequest> SAMPLE_MISSION_REQUESTS = List.of(
            new MissionRequest("Black Ops 1", "Stealth mission in enemy territory", 1, LocalDate.now().plusDays(7), null, MissionStatus.PENDING),
            new MissionRequest("Recon 1", "Recon mission in friendly territory", 3, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), MissionStatus.IN_PROGRESS),
            new MissionRequest("Rescue 1", "Rescue mission for captured allies", 2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), MissionStatus.COMPLETED)
    );

    @LocalServerPort
    int port;

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
        missionRepository.deleteAll();
    }

    @Test
    void whenGetAllMissions_thenReturnMissions() {
        addSampleMissions();
        given()
                .when().get()
                .then()
                .statusCode(200)
                .body("size()", is(SAMPLE_MISSION_REQUESTS.size()));
    }

    @Test
    void givenValidId_whenGetMissionById_thenReturnMission() {
        MissionResponse missionResponse = addSampleMissions().getFirst();
        given().when().get("/" + missionResponse.id())
                .then()
                .statusCode(200)
                .body("id", equalTo(missionResponse.id().intValue()))
                .body("name", equalTo(missionResponse.name()))
                .body("description", equalTo(missionResponse.description()))
                .body("priority", equalTo(missionResponse.priority()))
                .body("startDate", equalTo(missionResponse.startDate().toString()))
                .body("endDate", missionResponse.endDate() != null ? equalTo(missionResponse.endDate().toString()) : nullValue())
                .body("status", equalTo(missionResponse.status()));
    }

    @Test
    void givenInvalidId_whenGetMissionById_thenReturnNotFound() {
        given().when().get("/9999")
                .then()
                .statusCode(404);
    }

    @Test
    void whenGetMissionCount_thenReturnCount() {
        addSampleMissions();
        given()
                .when().get("/count")
                .then()
                .statusCode(200)
                .body(equalTo(String.valueOf(SAMPLE_MISSION_REQUESTS.size())));
    }

    @Test
    void givenValidRequest_whenCreateMission_thenReturnCreatedMission() {
        MissionRequest request = SAMPLE_MISSION_REQUESTS.getFirst();
        createMission(request);
    }

    @Test
    void givenInvalidRequest_whenCreateMission_thenReturnBadRequest() {
        MissionRequest request = new MissionRequest("", "Description", -1, null, null, null);

        given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void givenDuplicateName_whenCreateMission_thenReturnConflict() {
        MissionRequest request = SAMPLE_MISSION_REQUESTS.getFirst();
        createMission(request);

        given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(409);
    }

    @Test
    void givenValidRequest_whenUpdateMission_thenReturnUpdatedMission() {
        MissionResponse missionResponse = addSampleMissions().getFirst();
        MissionRequest updateRequest = new MissionRequest("Updated Name", "Updated Description", 2, LocalDate.now().plusDays(10), null, MissionStatus.IN_PROGRESS);

        given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/" + missionResponse.id())
                .then()
                .statusCode(200)
                .body("id", equalTo(missionResponse.id().intValue()))
                .body("name", equalTo(updateRequest.name()))
                .body("description", equalTo(updateRequest.description()))
                .body("priority", equalTo(updateRequest.priority()))
                .body("startDate", equalTo(updateRequest.startDate().toString()))
                .body("endDate", updateRequest.endDate() != null ? equalTo(updateRequest.endDate().toString()) : nullValue())
                .body("status", equalTo(updateRequest.status().name()));
    }

    @Test
    void givenInvalidRequest_whenUpdateMission_thenReturnBadRequest() {
        MissionResponse missionResponse = addSampleMissions().getFirst();
        MissionRequest updateRequest = new MissionRequest("", "Updated Description", -1, null, null, null);

        given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/" + missionResponse.id())
                .then()
                .statusCode(400);
    }

    @Test
    void givenDuplicateName_whenUpdateMission_thenReturnConflict() {
        List<MissionResponse> missions = addSampleMissions();
        MissionResponse missionToUpdate = missions.get(0);
        MissionResponse otherMission = missions.get(1);

        MissionRequest updateRequest = new MissionRequest(otherMission.name(), "Updated Description", 2, LocalDate.now().plusDays(10), null, MissionStatus.IN_PROGRESS);

        given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/" + missionToUpdate.id())
                .then()
                .statusCode(409);
    }

    @Test
    void givenValidId_whenDeleteMission_thenReturnNoContent() {
        MissionResponse missionResponse = createMission(SAMPLE_MISSION_REQUESTS.getFirst());
        given()
                .header("X-User-Id", "test-user")
                .when()
                .delete("/" + missionResponse.id())
                .then()
                .statusCode(204);
    }

    @Test
    void givenInvalidId_whenDeleteMission_thenReturnNotFound() {
        given()
                .header("X-User-Id", "test-user")
                .when()
                .delete("/9999")
                .then()
                .statusCode(404);
    }


    private List<MissionResponse> addSampleMissions() {
        return SAMPLE_MISSION_REQUESTS.stream()
                .map(this::createMission)
                .toList();
    }

    private MissionResponse createMission(MissionRequest request) {
        return given()
                .header("X-User-Id", "test-user")
                .contentType("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("priority", equalTo(request.priority()))
                .body("startDate", equalTo(request.startDate().toString()))
                .body("endDate", request.endDate() != null ? equalTo(request.endDate().toString()) : nullValue())
                .body("status", equalTo(request.status().name()))
                .extract().as(MissionResponse.class);
    }
}