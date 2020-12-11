package br.com.evandrorenan.web3270scripts.dto;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270scripts.dao.Script;
import lombok.Data;

@Component
@Data
public class ScriptDto {

	private long idScript;
	private String name;
	private List<ScriptLineDto> lines;

	public Script convertDtoToEntity() {		
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(this, Script.class);
	}

	public static List<ScriptDto> convertScriptToDto(List<Script> scripts) {
		List<ScriptDto> scriptDtoList = new ArrayList<>();
		for (Script script : scripts) {
			scriptDtoList.add(convertScriptToDto(script));
		}
		return scriptDtoList;
	}

	public static ScriptDto convertScriptToDto(Script script) {
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
