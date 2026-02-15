package br.com.evandrorenan.web3270.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
public class FaultAnalyzerBaseLocatorServiceTest {

    @Mock
    private ISessionService sessionService;

    @InjectMocks
    private FaultAnalyzerBaseLocatorService abendService;

    private IMySession sessionMock;

    @BeforeEach
    void setUp() {
        sessionMock = mock(IMySession.class);
    }

    @Test
    @DisplayName("should get base locators correctly")
    void shouldGetBaseLocators() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("session-1");
        
        when(sessionService.createNewSessionDto(anyString(), anyString())).thenReturn(sessionDto);
        when(sessionService.getSession("session-1")).thenReturn(sessionMock);
        
        String logScreen = " ".repeat(19) + "LOGON IN PROGRESS" + " ".repeat(1920 - 36);
        String storageAreasScreen = " ".repeat(80 * 14 + 5) + "ABEND1  " + " ".repeat(1920 - (80 * 14 + 13)) + "Storage Areas" + " ".repeat(10);
        
        StringBuilder sb = new StringBuilder("Program PROG1 Storage Areas" + " ".repeat(80 - 27));
        for (int i = 1; i < 23; i++) sb.append(" ".repeat(80));
        sb.append("      Event for Program PROG1 BL1=0002" + " ".repeat(80 - 38)); // Stop condition for BL1=0001
        String programStorageAreasScreen = sb.toString();
        
        // Return logScreen for logon, then storageAreasScreen for navigation
        // customWaitScreenUpdate calls getTextScreen multiple times for logging and waiting.
        when(sessionMock.getTextScreen()).thenReturn(
            logScreen, logScreen, logScreen + " ", logScreen, logScreen, logScreen, // logon sequence (plenty)
            storageAreasScreen, storageAreasScreen, storageAreasScreen + " ", storageAreasScreen, storageAreasScreen, storageAreasScreen, // navigate sequence
            programStorageAreasScreen, programStorageAreasScreen, programStorageAreasScreen, programStorageAreasScreen // scan sequence
        );
        when(sessionMock.getTextScreen(14, 5, 8)).thenReturn("ABEND1");

        List<FieldDto> fields = new ArrayList<>();
        // Pre-populate fields to avoid NPEs
        for (int i = 0; i < 20; i++) {
            FieldDto f = new FieldDto("f" + i);
            f.setStart(i * 80);
            f.setRow(i + 1);
            f.setCol(1);
            f.setText("");
            f.setProtected(true);
            fields.add(f);
        }

        // Setup fields for selectItem("Storage Areas")
        // selectItem checks fields.get(i+2).getText().contains(item)
        // then sendKeys on fields.get(i)
        fields.get(0).setRow(5); fields.get(0).setCol(5);
        fields.get(2).setText("Storage Areas");

        // Setup fields for selectItem("Program PROG1 Storage Areas")
        fields.get(1).setRow(6); fields.get(1).setCol(5);
        fields.get(3).setText("Program PROG1 Storage Areas");
        
        // Setup fields for scanStorageAreas
        // containsBaseLocatorLink checks fields.get(i-2) for "BL...at address"
        // and fields.get(i) for link (not protected)
        fields.get(5).setText("BL 1 0001 at address 12345678");
        fields.get(7).setText("LINK");
        fields.get(7).setProtected(false);
        fields.get(7).setRow(10);
        fields.get(7).setCol(10);
        
        fields.get(8).setText("*** Bottom of data.");

        when(sessionMock.getFields()).thenReturn(fields);
        
        IBaseLocatorExtractParams params = mock(IBaseLocatorExtractParams.class);
        when(params.getUser()).thenReturn("USER");
        when(params.getPassword()).thenReturn("PASS");
        when(params.getProgramName()).thenReturn("PROG1");
        when(params.getAbendId()).thenReturn("ABEND1");
        when(params.getAbendFile()).thenReturn("FILE");

        List<BaseLocatorDto> result = abendService.getBaseLocators(params);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
