package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.evandrorenan.web3270.application.usecase.GetSessionScreenUseCase;
import br.com.evandrorenan.web3270.application.dto.ScreenResponse;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ScreenControllerTest {

    @Mock
    private GetSessionScreenUseCase getSessionScreenUseCase;

    @InjectMocks
    private ScreenController screenController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
    }

    @Test
    @DisplayName("should get screen successfully")
    void shouldGetScreen() throws Exception {
        ScreenResponse screenDto = new ScreenResponse("content", 0, Collections.emptyList());
        
        when(getSessionScreenUseCase.execute("123")).thenReturn(screenDto);

        mockMvc.perform(get("/api/v1/sessions/123/screen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("content"));
    }
}
