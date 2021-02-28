package br.com.evandrorenan.web3270.pcomm;

import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.eNetwork.ECL.ECLCommNotify;
import com.ibm.eNetwork.ECL.ECLConnection;
import com.ibm.eNetwork.ECL.ECLErr;

public class PcommCommunicationEventListener implements ECLCommNotify {
	
	@Autowired
	public PcommCommunicationEventListener() {
		System.out.println("PcommEventListener instantiated");
	}

	@Override
	public void NotifyError(ECLConnection conn, ECLErr err) {
		System.out.println("PcommCommunicationEventListener- NotifyError: "  + conn + " - " + err);		
		
	}

	@Override
	public void NotifyEvent(ECLConnection conn, boolean status) {
		System.out.println("PcommCommunicationEventListener - NotifyEvent: "  + conn + " - " + status);		
		
	}

	@Override
	public void NotifyStop(ECLConnection conn, int reason) {
		System.out.println("PcommCommunicationEventListener - NotifyStop: "  + conn + " - " + reason);	
		
	}
}