package edu.eci.arsw.collabboard.infrastructure.web.rest;

import edu.eci.arsw.collabboard.domain.model.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardRestControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createShouldReturn201WithGeneratedId() {
        ResponseEntity<Board> response = restTemplate.postForEntity(
                "/api/boards", Map.of("name", "Architecture Session"), Board.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals("Architecture Session", response.getBody().name());
    }

    @Test
    void getExistingBoardShouldReturn200() {
        Board created = restTemplate.postForEntity(
                "/api/boards", Map.of("name", "X"), Board.class).getBody();

        ResponseEntity<Board> response = restTemplate.getForEntity(
                "/api/boards/" + created.id(), Board.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created.id(), response.getBody().id());
    }

    @Test
    void getMissingBoardShouldReturn404WithApiError() {
        ResponseEntity<ApiError> response = restTemplate.getForEntity(
                "/api/boards/missing-board", ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("BOARD_NOT_FOUND", response.getBody().code());
    }

    @Test
    void replaceExistingBoardShouldReturn200() {
        Board created = restTemplate.postForEntity(
                "/api/boards", Map.of("name", "X"), Board.class).getBody();

        Map<String, Object> replaceRequest = Map.of("name", "Renamed", "elements", List.of());

        ResponseEntity<Board> response = restTemplate.exchange(
                "/api/boards/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(replaceRequest),
                Board.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Renamed", response.getBody().name());
    }

    @Test
    void replaceMissingBoardShouldReturn404() {
        Map<String, Object> replaceRequest = Map.of("name", "X", "elements", List.of());

        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/api/boards/missing-board",
                HttpMethod.PUT,
                new HttpEntity<>(replaceRequest),
                ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}