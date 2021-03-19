package br.com.evandrorenan.web3270.pcomm;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.ibm.eNetwork.ECL.ECLErr;
import com.ibm.eNetwork.ECL.ECLOIA;
import com.ibm.eNetwork.ECL.ECLPS;
import com.ibm.eNetwork.ECL.event.ECLPSEvent;
import com.ibm.eNetwork.ECL.event.ECLPSListener;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;

public class PcommEventListener implements ECLPSListener {
	
	private SimpMessagingTemplate messageTemplate;
	
	private MyPcommSession myPcommSession;

	private IScreenService screenService;

	public PcommEventListener(IMySession session, SimpMessagingTemplate messageTemplate, IScreenService screenService) {
		this.myPcommSession = (MyPcommSession) session;
		this.messageTemplate = messageTemplate;
		this.screenService = screenService;
	}

	@Override
	public void PSNotifyError(ECLPS ps, ECLErr error) {
	}

	@Override
	public void PSNotifyEvent(ECLPSEvent event) {
		
		this.myPcommSession.printScreenOnConsole();
		ScreenDto screen;
		
		try {
			screen = this.screenService.getScreenFields(this.myPcommSession);
			String queue = "/queue/session/" + myPcommSession.getSessionId();
			messageTemplate.convertAndSend(queue, screen);
		} catch (ExceptionWeb3270 e) {
			this.myPcommSession.dispose();
			e.printStackTrace();
		}
		
		if (this.myPcommSession.getCountDownLatch() != null ) {
			if (this.myPcommSession.GetOIA().InputInhibited() == ECLOIA.INHIBIT_NOTINHIBITED) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.myPcommSession.getCountDownLatch().countDown();
			}
		} 	
	}

	@Override
	public void PSNotifyStop(ECLPS ps, int reason) {
	}
}