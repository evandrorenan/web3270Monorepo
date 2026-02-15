package br.com.evandrorenan.web3270.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.ProgramReportDto;
import br.com.evandrorenan.web3270.dto.ProgramReportRequestDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.service.ProgramReportService;
import br.com.evandrorenan.web3270.session._interface.IProgramReportService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@RestController
public class ReportController {
	
	private IProgramReportService programReportService;
	
	@Autowired
	public ReportController(IProgramReportService programReportService) {
		this.programReportService = programReportService;
	}
	
	@PostMapping(path = {"/programreport"})
	public ProgramReportDto programReport(@RequestBody ProgramReportRequestDto request) throws ExceptionWeb3270 {
		
		if (request.getBaseLocators() == null && request.getCompilationReport() == null ) {
			return this.programReportService.generateReport(
					request.getCompilationJobid(), 
					request.getBaseLocatorExtractParams());
		}

		if (request.getBaseLocators() == null) {
			return this.programReportService.generateReport(
					request.getCompilationReport(), 
					request.getBaseLocatorExtractParams());
		}
		
		return this.programReportService.generateReport(
				request.getCompilationReport(),
				request.getBaseLocators());
	}

	@PostMapping(path = {"/compilationreport"})
	public CompilationReportDto getCompilationReport(@RequestBody ProgramReportRequestDto request) throws ExceptionWeb3270 {
		return this.programReportService.getCompilationReport(
				request.getCompilationJobid());	
	}

	@PostMapping(path = {"/baselocators"})
	public List<BaseLocatorDto> getBaseLocators (@RequestBody ProgramReportRequestDto request) throws ExceptionWeb3270 {
		return this.programReportService.getBaseLocators(
				request.getBaseLocatorExtractParams());
	}
}