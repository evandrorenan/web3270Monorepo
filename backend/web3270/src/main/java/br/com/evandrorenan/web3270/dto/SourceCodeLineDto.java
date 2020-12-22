package br.com.evandrorenan.web3270.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class SourceCodeLineDto {

	private Integer lineId;
	private String text;
	private List<TokenDto> tokens;
	
	public SourceCodeLineDto() {
		this.tokens = new ArrayList<>();
	}
}