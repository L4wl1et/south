package com.project.south;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.south.application.dto.GameRequest;
import com.project.south.application.dto.GameResponse;
import com.project.south.application.dto.RegistrationRequest;
import com.project.south.application.dto.RegistrationResponse;
import com.project.south.domain.model.RegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RegistrationIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String gameId;

    @BeforeEach
    void setUp() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setTitle("Beta Game Test");
        gameRequest.setDescription("Integration test game");
        gameRequest.setTotalSlots(2);
        gameRequest.setRegistrationOpen(true);

        MvcResult result = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gameRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        GameResponse gameResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), GameResponse.class);
        gameId = gameResponse.getId();
    }

    @Test
    void shouldCreateGameAndRegisterPlayerSuccessfully() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        MvcResult result = mockMvc.perform(post("/api/games/{gameId}/registrations", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        RegistrationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), RegistrationResponse.class);

        assertThat(response.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        assertThat(response.getEmail()).isEqualTo("fylip@email.com");
    }

    @Test
    void shouldRejectDuplicateRegistration() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        mockMvc.perform(post("/api/games/{gameId}/registrations", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/games/{gameId}/registrations", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundForInvalidGame() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        mockMvc.perform(post("/api/games/{gameId}/registrations", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnprocessableWhenRegistrationIsClosed() throws Exception {
        GameRequest closedGame = new GameRequest();
        closedGame.setTitle("Closed Game");
        closedGame.setDescription("Closed");
        closedGame.setTotalSlots(10);
        closedGame.setRegistrationOpen(false);

        MvcResult result = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closedGame)))
                .andExpect(status().isCreated())
                .andReturn();

        GameResponse closedGameResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), GameResponse.class);

        RegistrationRequest request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        mockMvc.perform(post("/api/games/{gameId}/registrations", closedGameResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldListRegistrationsByGameId() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        mockMvc.perform(post("/api/games/{gameId}/registrations", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/games/{gameId}/registrations", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("fylip@email.com"));
    }
}