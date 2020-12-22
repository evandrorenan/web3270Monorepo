package br.com.evandrorenan.web3270.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import lombok.Data;

@Component
@Data
public class WorkAreaService {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionWeb3270.class);

	private WorkAreaService() {}
	
	public static WorkAreaDto newWorkArea(String line) {
		if (!isValidWorkArea(line) ) {
			return null;
		}

		try {
			Long address = Long.parseLong(line.substring(1, 9), 16);
			Long offset = Long.parseLong(line.substring(10, 20)
					.trim()
					.replace("+", "")
					.replace("-", "") , 16 );
			String fullWordHex = line.substring(21, 29)
					 + line.substring(30, 38)
					 + line.substring(39, 47)
					 + line.substring(48, 56).trim();
			
			String fullWordEbcdic = line.substring(58, 74);
			return new WorkAreaDto(address, offset, fullWordHex, fullWordEbcdic);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error capturing work area from %s. Message: %s"
					, line, e.getMessage());
			return null;
		}
	}
	
	public static boolean isValidWorkArea(String str) {
		return str.substring(1, 9).matches("[0-9A-F]{8}");
	}	
}