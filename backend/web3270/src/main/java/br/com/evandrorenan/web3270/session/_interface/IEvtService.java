package br.com.evandrorenan.web3270.session._interface;

import java.util.List;

import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

public interface IEvtService {
	
	public CompilationReportDto getCompilationReport(String jobId) throws ExceptionWeb3270;

	public String getSysoutHtml(String opcao, String jobId) throws ExceptionWeb3270;

	public String getSysoutHtml(String evt, String opcao, String jobId) throws ExceptionWeb3270;
	
	public List<String> getSysout(String evt, String opcao, String jobId) throws ExceptionWeb3270;
}