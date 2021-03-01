package br.com.evandrorenan.web3270.pcomm;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.ibm.eNetwork.ECL.ECLErr;
import com.ibm.eNetwork.ECL.ECLPS;
import com.ibm.eNetwork.ECL.event.ECLPSEvent;
import com.ibm.eNetwork.ECL.event.ECLPSListener;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;

public class PcommEventListener implements ECLPSListener {
	
	private SimpMessagingTemplate messageTemplate;
	
	private IMySession mySession;

	private IScreenService screenService;

	public PcommEventListener(IMySession session, SimpMessagingTemplate messageTemplate, IScreenService screenService) {
		System.out.println("PcommEventListener instantiated: " + session.toString() + messageTemplate);
		this.mySession = session;
		this.messageTemplate = messageTemplate;
		this.screenService = screenService;
	}

	@Override
	public void PSNotifyError(ECLPS ps, ECLErr error) {
		System.out.println("PSNotifyError: "  + ps + " - " + error);		
	}

	@Override
	public void PSNotifyEvent(ECLPSEvent event) {
		System.out.println("PSNotifyEvent" + event);
		ScreenDto screen;
		
		try {
			screen = this.screenService.getScreenFields(this.mySession);
			System.out.println("adding new event to queue " + mySession.getSessionId());
	        System.out.println(" fields: " + screen.getFields());
	        System.out.println(" fields: " + screen.getFieldPos());
	        System.out.println(" fields: " + screen.getCursorPos());
	        System.out.println(" fields: " + screen.getSessionId());
			String queue = "/queue/session/" + mySession.getSessionId();
			System.out.println(queue);
			messageTemplate.convertAndSend(queue, screen);
		} catch (ExceptionWeb3270 e) {
			this.mySession.dispose();
			e.printStackTrace();
		}
	}

	@Override
	public void PSNotifyStop(ECLPS ps, int reason) {
		System.out.println("PSNotifyStop: "  + ps + " - " + reason);		
	}
}