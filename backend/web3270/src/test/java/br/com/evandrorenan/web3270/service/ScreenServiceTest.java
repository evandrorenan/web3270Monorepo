package br.com.evandrorenan.web3270.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.SendKeysDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;

@ExtendWith(MockitoExtension.class)
class ScreenServiceTest {

    @Mock
    private SimpMessagingTemplate template;

    @InjectMocks
    private ScreenService screenService;

    private IMySession sessionMock;

    @BeforeEach
    void setUp() {
        sessionMock = mock(IMySession.class);
        org.mockito.Mockito.lenient().when(sessionMock.getSessionId()).thenReturn("session-1");
    }

    @Test
    @DisplayName("should get screen DTO correctly")
    void shouldGetScreenDto() throws ExceptionWeb3270 {
        when(sessionMock.getPositions()).thenReturn(new ArrayList<>());
        when(sessionMock.getFieldsIniPosition()).thenReturn(new ArrayList<>());
        when(sessionMock.getCursorPosition()).thenReturn(10);

        ScreenDto result = screenService.getScreenDto(sessionMock);

        assertEquals("session-1", result.getSessionId());
        assertEquals(10, result.getCursorPos());
    }

    @Test
    @DisplayName("should get screen fields correctly")
    void shouldGetScreenFields() throws ExceptionWeb3270 {
        when(sessionMock.getFieldsIniPosition()).thenReturn(new ArrayList<>());
        when(sessionMock.getCursorPosition()).thenReturn(20);
        when(sessionMock.getFields()).thenReturn(new ArrayList<>());

        ScreenDto result = screenService.getScreenFields(sessionMock);

        assertEquals("session-1", result.getSessionId());
        assertEquals(20, result.getCursorPos());
    }

    @Test
    @DisplayName("should send keys correctly")
    void shouldSendKeys() throws ExceptionWeb3270 {
        UserInputDto userInputDto = new UserInputDto();
        SendKeysDto sendKeysDto = new SendKeysDto();
        sendKeysDto.setRow(1);
        sendKeysDto.setCol(2);
        sendKeysDto.setText("text");
        sendKeysDto.setFunctionKey("ENTER");
        userInputDto.setSendKeys(Collections.singletonList(sendKeysDto));

        screenService.sendKeys(sessionMock, userInputDto);

        verify(sessionMock).setText(1, 2, "text");
        verify(sessionMock).sendKeys("ENTER", 1, 2);
    }
    
    @Test
    @DisplayName("should send keys without function key correctly")
    void shouldSendKeysWithoutFunctionKey() throws ExceptionWeb3270 {
        UserInputDto userInputDto = new UserInputDto();
        SendKeysDto sendKeysDto = new SendKeysDto();
        sendKeysDto.setRow(1);
        sendKeysDto.setCol(2);
        sendKeysDto.setText("text");
        userInputDto.setSendKeys(Collections.singletonList(sendKeysDto));

        screenService.sendKeys(sessionMock, userInputDto);

        verify(sessionMock).setText(1, 2, "text");
        verify(sessionMock, times(0)).sendKeys(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("should send keys async correctly")
    void shouldSendKeysAsync() throws ExceptionWeb3270 {
        UserInputDto userInputDto = new UserInputDto();
        SendKeysDto sendKeysDto = new SendKeysDto();
        sendKeysDto.setRow(1);
        sendKeysDto.setCol(2);
        sendKeysDto.setText("async");
        userInputDto.setSendKeys(Collections.singletonList(sendKeysDto));

        screenService.sendKeysAsync(sessionMock, userInputDto);

        verify(sessionMock).setText(1, 2, "async");
    }
}
