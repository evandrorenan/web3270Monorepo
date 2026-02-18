package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import br.com.evandrorenan.web3270.infrastructure.configuration.TestConfig;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class SessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should execute full session flow: create -> get screen")
    void shouldExecuteFullSessionFlow() throws Exception {
        // 1. Create Session
        CreateSessionRequest createRequest = new CreateSessionRequest("127.0.0.1", "23", "3270", "037");
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/sessions")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.connected").value(true))
                .andReturn();

        String sessionId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("sessionId").asText();

        // 2. Get Screen
        mockMvc.perform(get("/api/v1/sessions/" + sessionId + "/screen")
                .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("WELCOME TO IBM 3270 MOCK TERMINAL\nENTER USERID:"));
    }

    @Test
    @DisplayName("should return 401 when creating session without JWT")
    void shouldReturn401WithoutJwt() throws Exception {
        CreateSessionRequest createRequest = new CreateSessionRequest("127.0.0.1", "23", "3270", "037");
        
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
}
