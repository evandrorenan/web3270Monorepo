package br.com.evandrorenan.web3270.session._interface;

import java.util.List;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.ProgramReportDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

public interface IProgramReportService {
	
	public CompilationReportDto getCompilationReport(
			String compilationJobId) throws ExceptionWeb3270;

	public List<BaseLocatorDto> getBaseLocators(
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270;

	public ProgramReportDto generateReport(
			String compilationJobid, 
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270;

	public ProgramReportDto generateReport(
			CompilationReportDto compilationReportDto, 
			List<BaseLocatorDto> baseLocators);

	public ProgramReportDto generateReport(
			CompilationReportDto compilationReportDto, 
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270;

	public ProgramReportDto generateReport(String compilationJobid, List<BaseLocatorDto> baseLocators) throws ExceptionWeb3270;
}
