package br.com.evandrorenan.web3270.presentation.exception;

import br.com.evandrorenan.web3270.application.usecase.CreateSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.DisconnectSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.SendKeysUseCase;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import br.com.evandrorenan.web3270.presentation.api.v1.request.SendKeysRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RFC 9457 error handling.
 * Tests the complete error response flow through the application.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class ErrorHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateSessionUseCase createSessionUseCase;

    @MockBean
    private DisconnectSessionUseCase disconnectSessionUseCase;

    @MockBean
    private SendKeysUseCase sendKeysUseCase;

    @Test
    void shouldReturn404WithRFC9457FormatWhenSessionNotFound() throws Exception {
        String sessionId = "non-existent-session";
        SendKeysRequest request = new SendKeysRequest("ENTER");
        
        doThrow(new SessionNotFoundException(new SessionId(sessionId)))
            .when(sendKeysUseCase).execute(eq(sessionId), any());

        MvcResult result = mockMvc.perform(post("/api/v1/sessions/" + sessionId + "/keys")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Session Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Session " + sessionId + " not found"))
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ErrorResponse error = objectMapper.readValue(responseBody, ErrorResponse.class);
        
        assertThat(error.type().toString()).contains("session-not-found");
        assertThat(error.timestamp()).isNotNull();
    }

    @Test
    void shouldReturn400WithRFC9457FormatForValidationErrors() throws Exception {
        CreateSessionRequest invalidRequest = new CreateSessionRequest("", "99999", "3270", "037");
        
        MvcResult result = mockMvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ValidationErrorResponse error = objectMapper.readValue(responseBody, ValidationErrorResponse.class);
        
        assertThat(error.type().toString()).contains("validation-failed");
        assertThat(error.errors()).hasSize(3);
        assertThat(error.errors()).extracting("field").contains("host", "port");
    }

    @Test
    void shouldReturn400WithRFC9457FormatForInvalidHost() throws Exception {
        CreateSessionRequest invalidRequest = new CreateSessionRequest("invalid-host", "23", "3270", "037");
        
        mockMvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'host')]").exists());
    }

    @Test
    void shouldReturn400WithRFC9457FormatForInvalidPort() throws Exception {
        CreateSessionRequest invalidRequest = new CreateSessionRequest("192.168.1.1", "99999", "3270", "037");
        
        mockMvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'port')]").exists());
    }

    @Test
    void shouldIncludeFieldDetailsInValidationErrors() throws Exception {
        CreateSessionRequest invalidRequest = new CreateSessionRequest("", "", "3270", "037");
        
        MvcResult result = mockMvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").exists())
                .andExpect(jsonPath("$.errors[0].message").exists())
                .andExpect(jsonPath("$.errors[0].rejectedValue").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ValidationErrorResponse error = objectMapper.readValue(responseBody, ValidationErrorResponse.class);
        
        error.errors().forEach(fieldError -> {
            assertThat(fieldError.field()).isNotBlank();
            assertThat(fieldError.message()).isNotBlank();
        });
    }

    @Test
    void shouldIncludeInstanceUriInAllErrors() throws Exception {
        String sessionId = "test-123";
        SendKeysRequest request = new SendKeysRequest("ENTER");
        
        doThrow(new SessionNotFoundException(new SessionId(sessionId)))
            .when(sendKeysUseCase).execute(eq(sessionId), any());

        mockMvc.perform(post("/api/v1/sessions/" + sessionId + "/keys")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.instance").value("/api/v1/sessions/" + sessionId + "/keys"));
    }

    @Test
    void shouldIncludeTimestampInAllErrors() throws Exception {
        String sessionId = "test-456";
        doThrow(new RuntimeException("Unexpected error"))
            .when(disconnectSessionUseCase).execute(sessionId);

        MvcResult result = mockMvc.perform(delete("/api/v1/sessions/" + sessionId)
                        .with(jwt()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ErrorResponse error = objectMapper.readValue(responseBody, ErrorResponse.class);
        
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.timestamp()).isBeforeOrEqualTo(java.time.Instant.now());
    }

    @Test
    void shouldReturnConsistentErrorStructureAcrossDifferentErrorTypes() throws Exception {
        // Test 404 error structure (SessionNotFound)
        String sessionId = "missing";
        doThrow(new SessionNotFoundException(new SessionId(sessionId)))
            .when(disconnectSessionUseCase).execute(sessionId);

        MvcResult notFoundResult = mockMvc.perform(delete("/api/v1/sessions/" + sessionId)
                        .with(jwt()))
                .andExpect(status().isNotFound())
                .andReturn();
        
        ErrorResponse notFoundError = objectMapper.readValue(
                notFoundResult.getResponse().getContentAsString(), 
                ErrorResponse.class);
        
        assertThat(notFoundError.type()).isNotNull();
        assertThat(notFoundError.title()).isEqualTo("Session Not Found");
        assertThat(notFoundError.status()).isEqualTo(404);
        assertThat(notFoundError.detail()).isNotNull();
        assertThat(notFoundError.instance()).isNotNull();
        assertThat(notFoundError.timestamp()).isNotNull();

        // Test 400 validation error structure
        CreateSessionRequest invalidRequest = new CreateSessionRequest("", "", "3270", "037");
        MvcResult validationResult = mockMvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        ValidationErrorResponse validationError = objectMapper.readValue(
                validationResult.getResponse().getContentAsString(), 
                ValidationErrorResponse.class);
        
        assertThat(validationError.type()).isNotNull();
        assertThat(validationError.title()).isEqualTo("Validation Failed");
        assertThat(validationError.status()).isEqualTo(400);
        assertThat(validationError.detail()).isNotNull();
        assertThat(validationError.instance()).isNotNull();
        assertThat(validationError.timestamp()).isNotNull();
        assertThat(validationError.errors()).isNotEmpty();
    }
}
