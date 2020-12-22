package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class TokenDto {

	private boolean isReservedWord;
	private Integer dataReference;
	
}