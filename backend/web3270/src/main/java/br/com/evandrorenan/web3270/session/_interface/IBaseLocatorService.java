package br.com.evandrorenan.web3270.session._interface;

import java.util.List;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

public interface IBaseLocatorService {

	public List<BaseLocatorDto> getBaseLocators(
			IBaseLocatorExtractParams abendExtractParams) throws ExceptionWeb3270;
}
