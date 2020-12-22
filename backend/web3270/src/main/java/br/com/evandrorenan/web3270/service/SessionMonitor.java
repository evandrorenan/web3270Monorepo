package br.com.evandrorenan.web3270.service;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.session._interface.IMySession;

@Component
public class SessionMonitor {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionMonitor.class);

	@Async
	public static CompletableFuture<Boolean> monitorConnectionStatus(SessionService service) {
		
		int i = 0;
		long sleepTime = 5000L;
		
		while (true) {
			for (Entry<String, IMySession> entry : service.getSessionMap().entrySet()) {
			     if (! entry.getValue().isConnected()) {
			    	 entry.getValue().dispose();
			    	 service.getSessionMap().remove(entry.getKey());
			    	 sleepTime = 1L;
			    	 break;
			     }		     
			}
			
			try {
				Thread.sleep(sleepTime);
				sleepTime = 5000L;
			} catch (InterruptedException e) {
				logger.error("InterruptedException occurred when monitoring session connection Status: %s", 
						e.getMessage());
			}
			
//			if (service.getSessionMap().isEmpty() ) {
//				break;
//			}
			System.out.println(service.getSessionMap().toString());
			i++;
			if (i > 20) {
				System.out.println("Forced break");
				break;
			}
		}
		return CompletableFuture.completedFuture(true); 
	}
}