package br.com.evandrorenan.web3270.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.evandrorenan.web3270.dto.SysoutDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IEvtService;

@RestController
public class EvtController {
	
	private IEvtService evtService;
	
	@Autowired
	public EvtController(
			IEvtService evtService) {
		this.evtService = evtService;
	}
	
	@GetMapping(path = {"/sysout/html/{opcao}/{jobId}"})
	public String getSysoutHtml(
			@PathVariable String opcao, 
			@PathVariable String jobId) throws ExceptionWeb3270 {
		String str = this.evtService.getSysoutHtml(opcao, jobId);
		return str;
	}
	
	@GetMapping(path = {"/sysout/html/{evt}/{opcao}/{jobId}"})
	public String getSysoutEvtHtml(
			@PathVariable String evt,
			@PathVariable String opcao, 
			@PathVariable String jobId) throws ExceptionWeb3270 {
		String str = this.evtService.getSysoutHtml(evt, opcao, jobId);
		return str;
	}

	@GetMapping(path = {"/sysout/txt/{evt}/{opcao}/{jobId}"})
	public String getSysoutTxt(
			@PathVariable String evt,
			@PathVariable String opcao, 
			@PathVariable String jobId) throws ExceptionWeb3270 {
		String str = this.evtService.getSysoutTxt(evt, opcao, jobId);
		return str;
	}

	@GetMapping(path = {"/sysout/{evt}/{opcao}/{jobId}"})
	public SysoutDto getSysoutEvt(
			@PathVariable String evt,
			@PathVariable String opcao, 
			@PathVariable String jobId) throws ExceptionWeb3270 {
		SysoutDto sysout = this.evtService.getSysout(evt, opcao, jobId);
		return sysout;
	}
}