package br.com.evandrorenan.web3270.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session.MySessionConstants;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorService;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.ISessionService;
import lombok.Data;

@Component
@Data
public class FaultAnalyzerBaseLocatorService implements IBaseLocatorService {
	
	private ISessionService sessionService;

	@Autowired
	public FaultAnalyzerBaseLocatorService(ISessionService sessionService) {
		if (sessionService == null ) {
			this.sessionService = new SessionService();
		}
		this.sessionService = sessionService;
	}
	
	@Override
	public List<BaseLocatorDto> getBaseLocators(
			IBaseLocatorExtractParams params) throws ExceptionWeb3270 {
		
		IMySession mySession = this.getNewSession();
				
		this.logonTs48(mySession, params.getUser(), params.getPassword());
		this.navigateToStorageAreas(mySession, params.getProgramName(), params.getAbendId(), params.getAbendFile());

		List<BaseLocatorDto> baseLocators = this.scanStorageAreas(mySession, params.getProgramName());		
		logoffTs48(mySession);

		mySession.dispose();
		
		return baseLocators;
	}
	
	private void navigateToStorageAreas(
			IMySession mySession,
			String programName, 
			String abendId, 
			String abendFile) throws ExceptionWeb3270 {

		mySession.sendKeys("90" + MySessionConstants.ENTER_STR, 6, 15);
		mySession.sendKeys(abendFile + MySessionConstants.ERASEEOF_STR + MySessionConstants.ENTER_STR, 6, 32);
		mySession.sendKeys("        ", 14, 5);
		mySession.sendKeys(abendId, 14, 5);
		mySession.sendKeys(MySessionConstants.ENTER_STR, 14, 5);
		
		if (! mySession.getTextScreen(14, 5, 8).trim().equalsIgnoreCase(abendId.toUpperCase())) {
			this.logoffTs48(mySession);
			throw new ExceptionWeb3270 (
				"AbendId " + abendId + " not found", 
				mySession.getTextScreen(), 
				null);
		}
		
		mySession.sendKeys("i" + MySessionConstants.ENTER_STR, 14, 2);
		this.customWaitScreenUpdate(mySession);
		
		this.selectItem(mySession, "Storage Areas");		
		this.selectItem(mySession, "Program " + programName + " Storage Areas");
		
		if (mySession.getTextScreen().contains("Show all BLs")) {
			this.selectItem(mySession, "Show all BLs");
		}
	}

	private void customWaitScreenUpdate(IMySession mySession) {
		System.out.println(mySession.getTextScreen());
		String oldScreen = mySession.getTextScreen();
		try {
			for (int i = 3000 ; i > 10; i -= 50) {
				String newScreen = mySession.getTextScreen();
				if (!oldScreen.equals(newScreen)) {
					break;
				}
				System.out.print(i + ", ");
				Thread.sleep(50);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(mySession.getTextScreen());
	}

	private IMySession getNewSession() {
		SessionDto sessionDto = sessionService.createNewSessionDto("192.168.240.1", "51004");
		return this.sessionService.getSession(sessionDto.getSessionId());
	}
	
	private List<BaseLocatorDto> scanStorageAreas(IMySession mySession, String programName) throws ExceptionWeb3270 {
		List<String> processedAreas = new ArrayList<>();
		List<BaseLocatorDto> baseLocators = new ArrayList<>();
		
		for ( int j = 0; j < 10; j++)  {
			List<FieldDto> fields = mySession.getFields();
			for (int i = 1; i < fields.size(); i++) {

				if (this.containsBaseLocatorLink(fields, i, processedAreas)) {				
					BaseLocatorDto baseLocator = new BaseLocatorDto();
					baseLocator.setBaseLocatorType(fields.get(i - 2).getText().substring(2, 3));
					baseLocator.setBaseLocatorId(fields.get(i - 2).getText().substring(4, 8));
					mySession.sendKeys(MySessionConstants.ENTER_STR, fields.get(i).getRow(), fields.get(i).getCol());				
					baseLocator.extractWorkAreas(mySession, programName);
					mySession.sendKeys(MySessionConstants.F3_STR, 4, 15);				
					baseLocators.add(baseLocator);
				}
				if (fields.get(i).getText().trim().equals("*** Bottom of data.") 
				||  fields.get(i).getText().trim().equals("Hex-Dumped Storage")) {
					return baseLocators;
				}
			}
			
			mySession.sendKeys("[pf8]", 4, 15);
		}
		return baseLocators;
	}
	
	private boolean containsBaseLocatorLink(List<FieldDto> fields, int i, List<String> processedAreas) {
		if (i < 2) {
			return false;
		}
		
		if (fields.get(i).isProtected()) {
			return false;
		}
		
		if (fields.get(i - 2).getText().length() < 2) {
			return false;
		}
		
		if (!fields.get(i - 2).getText().substring(0, 2).equals("BL")) {
			return false;
		}
		
		if (! fields.get(i - 2).getText().contains("at address")) {
			return false;
		}
		
		return ! processedAreas.contains(fields.get(i).getText());
	}

	private void logonTs48(IMySession mySession, String user, String password) throws ExceptionWeb3270 {
		mySession.sendKeys("TS48[enter]", 24, 29);
		mySession.sendKeys(user + MySessionConstants.ENTER_STR, 2, 1);
		mySession.sendKeys(password + "[enter]", 8, 20);
		this.customWaitScreenUpdate(mySession);
		
		String textScreen = mySession.getTextScreen();
		if (! textScreen.substring(19, 36).equals("LOGON IN PROGRESS")) {
			throw new ExceptionWeb3270("TSO Logon Failed", "TSO Logon Failed. User: " + user + ", password: " + password, null);
		}
		mySession.sendKeys(MySessionConstants.ENTER_STR, 4, 15); //se tela diferente da esperada
		mySession.sendKeys(MySessionConstants.ENTER_STR, 4, 15);
		mySession.sendKeys(MySessionConstants.ENTER_STR, 4, 15);
	}
	
	private void logoffTs48(IMySession mySession) throws ExceptionWeb3270 {
		
		mySession.sendKeys("[pf3]", 4, 15);
		mySession.sendKeys("[pf3]", 4, 15);
		mySession.sendKeys("[pf3]", 4, 15);
		mySession.sendKeys("[pf3]", 4, 15);
		mySession.sendKeys("[pf3]", 4, 15);
	}
	
	private void selectItem(IMySession mySession, String item) throws ExceptionWeb3270 {
 		for (int j = 0; j < 5; j++) {
 			
 			List<FieldDto> fields = mySession.getFields();
 			for (int i = 0; i < fields.size() - 2; i++) {
 				if (fields.get(i + 2).getText().contains(item)) {
 					mySession.sendKeys("[enter]", fields.get(i).getRow(), fields.get(i).getCol());
 					return;
 				}
 				if (fields.get(i + 2).getText().contains("*** Bottom of data.")) {
 					this.logoffTs48(mySession);
	 				throw new ExceptionWeb3270("Item not available", "Item not available: " + item, null);
				}
 			}
			
			mySession.sendKeys("[pf8]", 1, 27);
		}
	}
}