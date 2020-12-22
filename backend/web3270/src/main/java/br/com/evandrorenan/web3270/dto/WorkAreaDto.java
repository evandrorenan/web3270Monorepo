package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkAreaDto {
	private Long address;
	private Long offset;
	private String fullWordHex;
	private String fullWordEbcdic;
	
	public WorkAreaDto(String line) {
		
		if (!isValidWorkArea(line) ) {
			return;
		}
		
		this.address = Long.parseLong(line.substring(1, 9), 16);
		this.offset = Long.parseLong(line.substring(10, 20)
									.trim()
									.replace("+", "")
									.replace("-", "") , 16 );
		
		this.fullWordHex = line.substring(21, 29)
						 + line.substring(30, 38)
						 + line.substring(39, 47)
						 + line.substring(48, 56).trim();
				
		this.fullWordEbcdic = line.substring(58, 74);
	}
	
	public static boolean isValidWorkArea(String str) {
		return str.substring(1, 9).matches("[0-9A-F]{8}");
	}	
}