package br.com.evandrorenan.web3270.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.dto.SessionPropertiesDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@RestController
/**
 * Provide access to a Terminal 3270 session, making possible to connect 
 * with Custom Connection Properties, get screen's content and send input keys. 
 * 
 * @author  evandrorenan
 */
public class SessionController {
	
	private ISessionService sessionService;
	private IScreenService screenService;
	
	@Autowired
	public SessionController(
			ISessionService sessionService,
			IScreenService screenService) {
		this.sessionService = sessionService;
		this.screenService = screenService;
	}
	
    /**
     * Create a new Terminal 3270 Session and return a it's Id.
     *
     * @param   data     {@code SessionPropertiesDto} object.
     * @return  a {@code SessionDto} that contains the Session Id and Connection Status.
     * @throws ExceptionWeb3270 
     */	
	@PostMapping(path = {"/newsession"})
	public SessionDto newSession(@RequestBody SessionPropertiesDto sessionPropertiesDto) throws ExceptionWeb3270 {
		SessionDto newSessionDto = this.sessionService.createNewSessionDto(
			sessionPropertiesDto.getHost(), 
			sessionPropertiesDto.getPort());

		if (newSessionDto == null) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		} 
		// TODO: NewSession should start a websocket queue identified by sessionId to
		//       be subscribed by the frontend. The registered ps should put screen
		return newSessionDto;
	}
	
	@GetMapping(path = {"/session/{sessionid}"})
	public ResponseEntity<SessionDto> getSession(@PathVariable String sessionid) {
		SessionDto sessionDto = this.sessionService.getSessionDto(
					this.sessionService.getSession(sessionid));
		if (sessionDto == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(sessionDto, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/session/{sessionid}/disconnect"})
	public SessionDto disconnectSession(@PathVariable String sessionId) {
		return this.sessionService.disconnect(sessionId);
	}
	
	@GetMapping(path = {"/session/{sessionId}/screen"})
    public ScreenDto getScreen(@PathVariable String sessionId) throws ExceptionWeb3270 {
		IMySession mySession = this.sessionService.getSession(sessionId);
		if (mySession == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return this.screenService.getScreenDto(mySession);
    }
	
	@GetMapping(path = {"/session/{sessionId}/screenfields"})
    public ScreenDto getScreenFields(@PathVariable String sessionId) throws ExceptionWeb3270 {
		IMySession mySession = this.sessionService.getSession(sessionId);
		if (mySession == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return this.screenService.getScreenFields(mySession);
    }
	
	@PostMapping(path = {"/session/sendkeys"})
	public ScreenDto sendKeys(@RequestBody UserInputDto userInputDto ) throws ExceptionWeb3270 {		
		IMySession mySession = this.sessionService.getSession(userInputDto.getSessionId());
		if (mySession == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		this.screenService.sendKeys(mySession, userInputDto);
		return this.screenService.getScreenFields(mySession);
	}
}