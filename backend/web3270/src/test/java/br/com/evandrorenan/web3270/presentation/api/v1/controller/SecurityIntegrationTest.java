package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import br.com.evandrorenan.web3270.application.usecase.CreateSessionUseCase;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import br.com.evandrorenan.web3270.infrastructure.configuration.TestConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateSessionUseCase createSessionUseCase;

    @Test
    @DisplayName("should return 401 when accessing secured endpoint without token")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/sessions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return 200 (or other success) when accessing secured endpoint with valid token")
    void shouldReturnSuccessWithToken() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest("127.0.0.1", "23", "3270", "037");

        mockMvc.perform(post("/api/v1/sessions")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 400 when host is invalid")
    void shouldReturn400WithInvalidHost() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest("invalid-ip", "23", "3270", "037");

        mockMvc.perform(post("/api/v1/sessions")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when port is invalid")
    void shouldReturn400WithInvalidPort() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest("127.0.0.1", "99999", "3270", "037");

        mockMvc.perform(post("/api/v1/sessions")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 200 for public health endpoint")
    void shouldReturn200ForHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
