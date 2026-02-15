package br.com.evandrorenan.web3270.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.dto.SysoutDto;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
public class EvtServiceTest {

    @Mock
    private ISessionService sessionService;

    @InjectMocks
    private EvtService evtService;

    private IMySession sessionMock;

    @BeforeEach
    void setUp() {
        sessionMock = mock(IMySession.class);
        evtService.setBradescoHostIp("1.2.3.4");
        evtService.setBradescoHostPort("23");
    }

    @Test
    @DisplayName("should get sysout HTML correctly")
    void shouldGetSysoutHtml() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("session-1");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(sessionDto);
        when(sessionService.getSession("session-1")).thenReturn(sessionMock);
        
        // Mock navigateToSysout behavior
        // Mock getPage behavior
        String mockedScreenPart1 = "line 1" + " ".repeat(74) +
                                   "line 2" + " ".repeat(74) +
                                   "line 3" + " ".repeat(74) +
                                   "line 4" + " ".repeat(26) + "001" + " ".repeat(47) +
                                   "data line 1" + " ".repeat(69) +
                                   "----  FINAL DO RELATORIO  ----" + " ".repeat(50) +
                                   " ".repeat(80 * 18);
        
        when(sessionMock.getTextScreen()).thenReturn(mockedScreenPart1);
        when(sessionMock.getTextScreen(anyInt(), anyInt(), anyInt())).thenReturn("LIHAS->");

        String result = evtService.getSysoutHtml("opcao", "jobId");

        assertNotNull(result);
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("data line 1"));
    }

    @Test
    @DisplayName("should get sysout DTO correctly")
    void shouldGetSysoutDto() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("session-1");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(sessionDto);
        when(sessionService.getSession("session-1")).thenReturn(sessionMock);
        
        String mockedScreen = " ".repeat(80 * 3) + " ".repeat(32) + "002" + " ".repeat(45) +
                             "line 1" + " ".repeat(74) + "line 2" + " ".repeat(74) + "----  FINAL DO RELATORIO  ----" + " ".repeat(50) +
                             " ".repeat(80 * 18);
        
        when(sessionMock.getTextScreen()).thenReturn(mockedScreen);
        when(sessionMock.getTextScreen(anyInt(), anyInt(), anyInt())).thenReturn("LIHAS->");

        SysoutDto result = evtService.getSysout("EVT04", "OPT", "JOB");

        assertNotNull(result);
        assertEquals("JOB", result.getJobId());
        assertTrue(result.getContent().contains("line 1"));
    }

    @Test
    @DisplayName("should get compilation report correctly")
    void shouldGetCompilationReport() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("session-1");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(sessionDto);
        when(sessionService.getSession("session-1")).thenReturn(sessionMock);
        
        String screenWithJobInfo = " ".repeat(90) + "PROGNAME" + " ".repeat(119 - 98) + "JOB00001" + " ".repeat(1920 - 127);
        assertEquals(1920, screenWithJobInfo.length());
        when(sessionMock.getTextScreen()).thenReturn(screenWithJobInfo);
        
        // Mock getExpandedSourceCode loop to end early
        when(sessionMock.getTextScreen(5, 2, "SELECAO OU PESQUISA".length())).thenReturn("SELECAO OU PESQUISA");

        CompilationReportDto result = evtService.getCompilationReport("JOB00001");

        assertNotNull(result);
        assertEquals("PROGNAME", result.getProgramName());
        assertEquals("JOB00001", result.getJobId());
    }
}
