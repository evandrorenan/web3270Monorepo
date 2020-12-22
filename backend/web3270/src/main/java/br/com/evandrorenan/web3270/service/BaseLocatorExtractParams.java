package br.com.evandrorenan.web3270.service;

import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseLocatorExtractParams implements IBaseLocatorExtractParams {

	private String user; 
	private String password; 
	private String programName; 
	private String abendId;
	private String abendFile;
	
}
