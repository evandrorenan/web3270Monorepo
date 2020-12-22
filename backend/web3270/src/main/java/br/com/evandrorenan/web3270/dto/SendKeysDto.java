package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class SendKeysDto {
	private int row;
	private int col;
	private String text;
	private String functionKey;
}