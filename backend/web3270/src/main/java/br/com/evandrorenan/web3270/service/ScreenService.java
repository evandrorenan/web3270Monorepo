package br.com.evandrorenan.web3270.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.SendKeysDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import lombok.Data;

@Component
@Data
public class ScreenService implements IScreenService {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	public ScreenService() {
		System.out.println("ScreenService constructed;");
	}

	public ScreenDto getScreenDto(IMySession mySession) throws ExceptionWeb3270 {
		ScreenDto screenDto = new ScreenDto();
		screenDto.setSessionId(mySession.getSessionId());
		screenDto.setPositions(mySession.getPositions());
		screenDto.setFieldPos(mySession.getFieldsIniPosition());
		screenDto.setCursorPos(mySession.getCursorPosition());
		screenDto.setFields(null);
		return screenDto;
	}

	public ScreenDto getScreenFields(IMySession mySession) throws ExceptionWeb3270 {
		ScreenDto screenDto = new ScreenDto();
		screenDto.setSessionId(mySession.getSessionId());
		screenDto.setPositions(null);
		screenDto.setFieldPos(mySession.getFieldsIniPosition());
		screenDto.setCursorPos(mySession.getCursorPosition());
		screenDto.setFields(mySession.getFields());
		return screenDto;
	}

	public void sendKeys(IMySession mySession, UserInputDto userInputDto) throws ExceptionWeb3270 {		
		for (SendKeysDto sendKeys : userInputDto.getSendKeys()) {
			mySession.setText(sendKeys.getRow(), sendKeys.getCol(), sendKeys.getText());
			if (sendKeys.getFunctionKey() != null && !sendKeys.getFunctionKey().equals("")  ) {
				mySession.sendKeys(sendKeys.getFunctionKey(), sendKeys.getRow(), sendKeys.getCol());
			}			
		}
	}
	
	@Async
	public void sendKeysAsync(IMySession mySession, UserInputDto userInputDto) throws ExceptionWeb3270 {

		this.sendKeys(mySession, userInputDto);
		
//		ScreenDto s = new ScreenDto();
//		s.setPositions(new ArrayList<>());
//		s.setFields(new ArrayList<>());
//		
//		for (int i = 0; i < 24; i++ ) {
//			FieldDto f = new FieldDto();
//			f.setFieldId(String.valueOf(i));
//			f.setStart((i * 80) + 1);
//			f.setEnd(f.getStart() + 80);
//			f.setText("....+....1....+....2....+....3....+....4....+....5....+....6....+....7....+....8");
//			f.setProtected(false);
//			f.setHidden(false);
//			f.setHighIntensity(false);
//			f.setColor("Cyan");
//			f.setRow(i);
//			f.setCol(1);
//			f.setUnderline(false);
//			s.getFields().add(f);
//		}
//		
//		s.setPositions(null);
//		s.setFieldPos(new ArrayList<>());
//		s.getFieldPos().add(1);
//		s.setCursorPos(2);
//		s.setScreendId("ScreendIdMocked");
//		s.setSessionId(userInputDto.getSessionId());
//		
//		template.convertAndSend("/queue/session/" + userInputDto.getSessionId(), s);
	}
}