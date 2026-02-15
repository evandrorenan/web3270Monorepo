package br.com.evandrorenan.web3270.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.dto.SessionPropertiesDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private ISessionService sessionService;

    @Mock
    private IScreenService screenService;

    @Mock
    private IMySession mySession;

    @InjectMocks
    private SessionController sessionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("should create new session successfully")
    void shouldCreateNewSession() throws Exception {
        SessionPropertiesDto props = new SessionPropertiesDto("host", "23");
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("123");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(sessionDto);

        mockMvc.perform(post("/newsession")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(props)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }

    @Test
    @DisplayName("should return 503 when new session fails")
    void shouldReturn503WhenNewSessionFails() throws Exception {
        SessionPropertiesDto props = new SessionPropertiesDto("host", "23");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/newsession")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(props)))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("should get session successfully")
    void shouldGetSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("123");
        
        when(sessionService.getSession("123")).thenReturn(mySession);
        when(sessionService.getSessionDto(mySession)).thenReturn(sessionDto);

        mockMvc.perform(get("/session/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }

    @Test
    @DisplayName("should return 404 when session not found")
    void shouldReturn404WhenSessionNotFound() throws Exception {
        when(sessionService.getSession("123")).thenReturn(null);
        when(sessionService.getSessionDto(null)).thenReturn(null);

        mockMvc.perform(get("/session/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should disconnect session")
    void shouldDisconnectSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("123");
        
        when(sessionService.disconnect("123")).thenReturn(sessionDto);

        mockMvc.perform(get("/session/123/disconnect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }

    @Test
    @DisplayName("should get screen successfully")
    void shouldGetScreen() throws Exception {
        ScreenDto screenDto = new ScreenDto();
        screenDto.setSessionId("123");
        
        when(sessionService.getSession("123")).thenReturn(mySession);
        when(screenService.getScreenDto(mySession)).thenReturn(screenDto);

        mockMvc.perform(get("/session/123/screen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }
    
    @Test
    @DisplayName("should return 404 for screen when session not found")
    void shouldReturn404ForScreenWhenSessionNotFound() throws Exception {
        when(sessionService.getSession("123")).thenReturn(null);

        mockMvc.perform(get("/session/123/screen"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should get screen fields successfully")
    void shouldGetScreenFields() throws Exception {
        ScreenDto screenDto = new ScreenDto();
        screenDto.setSessionId("123");
        
        when(sessionService.getSession("123")).thenReturn(mySession);
        when(screenService.getScreenFields(mySession)).thenReturn(screenDto);

        mockMvc.perform(get("/session/123/screenfields"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
    }

    @Test
    @DisplayName("should send keys via POST successfully")
    void shouldSendKeysViaPost() throws Exception {
        UserInputDto userInput = new UserInputDto();
        userInput.setSessionId("123");
        ScreenDto screenDto = new ScreenDto();
        screenDto.setSessionId("123");
        
        when(sessionService.getSession("123")).thenReturn(mySession);
        when(screenService.getScreenFields(mySession)).thenReturn(screenDto);

        mockMvc.perform(post("/session/sendkeys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("123"));
        
        verify(screenService).sendKeys(any(IMySession.class), any(UserInputDto.class));
    }
}