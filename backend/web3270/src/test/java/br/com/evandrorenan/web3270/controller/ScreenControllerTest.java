package br.com.evandrorenan.web3270.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
class ScreenControllerTest {

    @Mock
    private IScreenService screenService;

    @Mock
    private ISessionService sessionService;

    @InjectMocks
    private ScreenController screenController;

    @Test
    @DisplayName("should send keys via MessageMapping")
    void shouldSendKeys() throws ExceptionWeb3270 {
        UserInputDto payload = new UserInputDto();
        payload.setSessionId("123");
        
        IMySession mySession = mock(IMySession.class);
        when(sessionService.getSession("123")).thenReturn(mySession);

        screenController.sendKeys(payload);

        verify(screenService).sendKeysAsync(eq(mySession), eq(payload));
    }
}
