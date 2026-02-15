package br.com.evandrorenan.web3270.controller;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.SendKeysDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.dto.SessionPropertiesDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.service.ScreenService;
import br.com.evandrorenan.web3270.service.SessionService;
import br.com.evandrorenan.web3270.session.MySessionMock;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest(classes = {SessionController.class})
public class Web3270ControllerTest {

	private static final String VALID_SESSION_ID = "ValidSessionId";
	private static final String NON_EXISTENT_SESSION_ID = "NonExistentSessionId";

	private static final String VALID_HOST_IP = "192.168.240.1";
	private static final String INVALID_HOST_IP = "192.0.0.0";

	private static final String INVALID_PORT_NUMBER = "100";
	private static final String VALID_PORT_NUMBER = "51004";

	private static final String VALID_PF_KEY = "[enter]";

	@Autowired
	private SessionController web3270Controller;
	
	@MockBean(name = "mockWeb3270Sessionservice")
	private SessionService mockSessionservice;
	
	@MockBean(name = "mockWeb3270ScreenService")
	private ScreenService mockScreenService;
	
	@BeforeEach
	void setUp() throws Exception {
		RestAssuredMockMvc.standaloneSetup(this.web3270Controller);
	}

	@Test
	public void returnOk_whenNewSession() throws ExceptionWeb3270 {		
		SessionPropertiesDto sessionPropertiesDto = new SessionPropertiesDto(VALID_HOST_IP, VALID_PORT_NUMBER);
		
		Mockito	
			.when(this.mockSessionservice.createNewSessionDto(VALID_HOST_IP, VALID_PORT_NUMBER))
			.thenReturn(this.getSessionDto(VALID_SESSION_ID));

		RestAssuredMockMvc
			.given()
				.contentType("application/json")
				.body(sessionPropertiesDto)
			.when()
				.post("/newsession")
			.then()
				.statusCode(HttpStatus.OK.value());		
	}
	
	@Test 
	public void returnServiceUnavailable_whenNewSession() throws ExceptionWeb3270 {
		SessionPropertiesDto sessionPropertiesDto = new SessionPropertiesDto(INVALID_HOST_IP, INVALID_PORT_NUMBER);
		
		RestAssuredMockMvc
			.given()
				.contentType("application/json")
				.body(sessionPropertiesDto)
			.when()
				.post("/newsession")
			.then()
				.log().all()
				.statusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
	}
	
	@Test
	public void returnOk_whenGetSession() throws ExceptionWeb3270 {
		IMySession mySession = this.getMySession(VALID_SESSION_ID);
		
		Mockito
			.when(this.mockSessionservice.getSession(VALID_SESSION_ID))
			.thenReturn(mySession);

		Mockito	
			.when(this.mockSessionservice.getSessionDto(mySession))
			.thenReturn(this.getSessionDto(VALID_SESSION_ID));

		RestAssuredMockMvc
			.when()
				.get("/session/" + VALID_SESSION_ID)
			.then()
				.statusCode(HttpStatus.OK.value());
	}
	
	@Test 
	public void returnNotFound_whenGetSession() throws ExceptionWeb3270 {
		this.getSessionDto(NON_EXISTENT_SESSION_ID);
		
		Mockito
			.when(this.mockSessionservice.getSession(NON_EXISTENT_SESSION_ID))
			.thenReturn(null);
		
		Mockito	
			.when(this.mockSessionservice.getSessionDto(null))
			.thenReturn(null);

		RestAssuredMockMvc
			.when()
				.get("/session/" + NON_EXISTENT_SESSION_ID)
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value());		
	}
	
	@Test
	public void returnOk_whenGetSessionScreen() throws ExceptionWeb3270 {
		IMySession mySession = new MySessionMock(VALID_SESSION_ID);

		Mockito
			.when(this.mockSessionservice.getSession(VALID_SESSION_ID))
			.thenReturn(mySession);

		Mockito	
			.when(this.mockSessionservice.getSessionDto(mySession))
			.thenReturn(this.getSessionDto(VALID_SESSION_ID));
		
		Mockito
			.when(this.mockScreenService.getScreenDto(mySession))
			.thenReturn(this.getScreenDto(mySession));

		RestAssuredMockMvc
			.when()
				.get("/session/" + VALID_SESSION_ID + "/screen")
			.then()
				.body(Matchers.containsString(VALID_SESSION_ID))
				.body(Matchers.containsString("protected"))
				.body(Matchers.containsString("cursorPos"))
				.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void returnNotFound_whenGetSessionScreen() throws ExceptionWeb3270 {
		Mockito	
			.when(this.mockSessionservice.getSessionDto(null))
			.thenReturn(this.getSessionDto(null));
		
		RestAssuredMockMvc
			.when()
				.get("/session/" + NON_EXISTENT_SESSION_ID + "/screen")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void returnOk_whenSendKeys() throws ExceptionWeb3270 {
		
		IMySession mySession = this.getMySession(VALID_SESSION_ID);
		
		Mockito
			.when(this.mockSessionservice.getSession(VALID_SESSION_ID))
			.thenReturn(mySession);

		Mockito	
			.when(this.mockSessionservice.getSessionDto(mySession))
			.thenReturn(this.getSessionDto(VALID_SESSION_ID));
		
		Mockito
			.when(this.mockScreenService.getScreenFields(mySession))
			.thenReturn(this.getScreenDto(mySession));

		//Assuming that mockWeb3270ScreenService.sendKeys will do nothing by default
		//so there's no need to mock sendKeys method
		
		RestAssuredMockMvc
			.given()
				.contentType("application/json")
				.body(this.getUserInputDto(VALID_SESSION_ID))
			.when()
				.post("/session/sendkeys")
			.then()
				.statusCode(HttpStatus.OK.value())
				.body(Matchers.containsString("protected"))
				.body(Matchers.containsString("cursorPos"))
				.body(Matchers.containsString(VALID_SESSION_ID));
	}
	
	@Test 
	public void returnNotFound_whenSendKeys() {
		IMySession mySession = new MySessionMock(NON_EXISTENT_SESSION_ID);

		Mockito
			.when(this.mockSessionservice.getSession(NON_EXISTENT_SESSION_ID))
			.thenReturn(null);
		
		Mockito	
			.when(this.mockSessionservice.getSessionDto(mySession))
			.thenReturn(this.getSessionDto(mySession.getSessionId()));
		
		//Assuming that mockWeb3270ScreenService.sendKeys will do nothing by default
		//so there's no need to mock sendKeys method
		
		RestAssuredMockMvc
			.given()
				.contentType("application/json")
				.body(this.getUserInputDto(NON_EXISTENT_SESSION_ID))
			.when()
				.post("/session/sendkeys")
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	private SessionDto getSessionDto(String sessionId) {
		SessionDto sessionDto = new SessionDto();
		sessionDto.setConnected(true);
		sessionDto.setSessionId(sessionId);
		return sessionDto;
	}
	
	private UserInputDto getUserInputDto(String sessionId) {
		UserInputDto userInputDto = new UserInputDto();
		SendKeysDto sendKeysDto = new SendKeysDto();
		sendKeysDto.setRow(1);
		sendKeysDto.setCol(1);
		sendKeysDto.setText(VALID_PF_KEY);
		
		List<SendKeysDto> sendKeys = new ArrayList<>();
		sendKeys.add(sendKeysDto);
		userInputDto.setSendKeys(sendKeys);	
		
		userInputDto.setSessionId(sessionId);

		return userInputDto;
	}
	
	private IMySession getMySession(String sessionId) {
		if (sessionId.equals(NON_EXISTENT_SESSION_ID)) {
			return null;
		}
		return new MySessionMock(sessionId);		
	}
	
	private ScreenDto getScreenDto(IMySession mySession) throws ExceptionWeb3270 {
		ScreenDto screenDto = new ScreenDto();
		screenDto.setSessionId(mySession.getSessionId());
		screenDto.setPositions(mySession.getPositions());
		screenDto.setFieldPos(mySession.getFieldsIniPosition());
		screenDto.setCursorPos(mySession.getCursorPosition());		
		return screenDto;		
	}
}