package br.com.evandrorenan.web3270.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.ProgramReportDto;
import br.com.evandrorenan.web3270.dto.ProgramReportRequestDto;
import br.com.evandrorenan.web3270.service.ProgramReportService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ISessionService sessionService;

    @InjectMocks
    private ReportController reportController;
    
    // We need to mock the internal ProgramReportService
    private ProgramReportService programReportServiceMock;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        programReportServiceMock = mock(ProgramReportService.class);
        
        // Use ReflectionTestUtils to set the private field
        ReflectionTestUtils.setField(reportController, "programReportService", programReportServiceMock);
        
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("should generate program report")
    void shouldGenerateProgramReport() throws Exception {
        ProgramReportRequestDto request = new ProgramReportRequestDto();
        request.setCompilationJobid("job123");
        // Only job id provided
        
        when(programReportServiceMock.generateReport(anyString(), nullable(br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams.class))).thenReturn(new ProgramReportDto());

        mockMvc.perform(post("/programreport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should get compilation report")
    void shouldGetCompilationReport() throws Exception {
        ProgramReportRequestDto request = new ProgramReportRequestDto();
        request.setCompilationJobid("job123");
        
        when(programReportServiceMock.getCompilationReport("job123")).thenReturn(new CompilationReportDto());

        mockMvc.perform(post("/compilationreport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
