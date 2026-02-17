package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.evandrorenan.web3270.application.usecase.CreateSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.DisconnectSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.GetSessionScreenUseCase;
import br.com.evandrorenan.web3270.application.usecase.SendKeysUseCase;
import br.com.evandrorenan.web3270.application.dto.SessionResponse;
import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import br.com.evandrorenan.web3270.presentation.api.v1.request.SendKeysRequest;

import java.time.LocalDateTime;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private CreateSessionUseCase createSessionUseCase;

    @Mock
    private DisconnectSessionUseCase disconnectSessionUseCase;

    @Mock
    private SendKeysUseCase sendKeysUseCase;

    @InjectMocks
    private SessionController sessionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @DisplayName("should create new session successfully")
    void shouldCreateNewSession() throws Exception {
        CreateSessionRequest props = new CreateSessionRequest("127.0.0.1", "23", "3270", "UTF-8");
        SessionResponse sessionDto = new SessionResponse("123", true, LocalDateTime.now());
        
        when(createSessionUseCase.execute(any())).thenReturn(sessionDto);

        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(props)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }

    @Test
    @DisplayName("should send keys sucessfully")
    void shouldSendKeys() throws Exception {
        SendKeysRequest request = new SendKeysRequest("keys");

        mockMvc.perform(post("/api/v1/sessions/123/keys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        verify(sendKeysUseCase).execute(eq("123"), any());
    }

    @Test
    @DisplayName("should disconnect session")
    void shouldDisconnectSession() throws Exception {
        mockMvc.perform(delete("/api/v1/sessions/123"))
                .andExpect(status().isNoContent());
        
        verify(disconnectSessionUseCase).execute("123");
    }

    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
