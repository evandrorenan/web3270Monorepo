package br.com.evandrorenan.web3270scripts.exception;

public class ScriptNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ScriptNotFoundException(String message) {
		super(message);
	}
}
