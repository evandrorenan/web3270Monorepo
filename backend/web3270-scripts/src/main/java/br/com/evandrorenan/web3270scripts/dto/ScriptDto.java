package br.com.evandrorenan.web3270scripts.dto;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270scripts.dao.ScriptDao;
import lombok.Data;

@Component
@Data
public class ScriptDto {

	private long idScript;
	private String name;
	private List<ScriptLineDto> lines;

	public ScriptDao convertDtoToEntity() {		
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(this, ScriptDao.class);
	}

	public static List<ScriptDto> convertScriptToDto(List<ScriptDao> scripts) {
		List<ScriptDto> scriptDtoList = new ArrayList<>();
		for (ScriptDao script : scripts) {
			scriptDtoList.add(convertScriptToDto(script));
		}
		return scriptDtoList;
	}

	public static ScriptDto convertScriptToDto(ScriptDao script) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(script, ScriptDto.class);
	}

	public List<String> toListString() {

		List <String> strLines = new ArrayList<>();
		for (ScriptLineDto scriptLineDto : lines) {
			strLines.add(scriptLineDto.getTextScriptLine());
		}
		return strLines;
	}
}
