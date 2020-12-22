package br.com.evandrorenan.web3270.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDivisionMapItemContentDto extends DataDivisionMapItemDto {
	
	private List<String> hexContent;
	private List<String> ebcdicContent;

	public DataDivisionMapItemContentDto(DataDivisionMapItemDto ddContent) {
		this.setLineId					(ddContent.getLineId());
		this.setDataHierarchy			(ddContent.getDataHierarchy());
		this.setDataName				(ddContent.getDataName());
		this.setBaseLocatorType			(ddContent.getBaseLocatorType());
		this.setBaseLocatorId			(ddContent.getBaseLocatorId());
		this.setBaseLocatorShift		(ddContent.getBaseLocatorShift());
		this.setDataStructurePosition	(ddContent.getDataStructurePosition());
		this.setAssemblerDataDefinition	(ddContent.getAssemblerDataDefinition());
		this.setDataType				(ddContent.getDataType());
		this.setDataDefAttribute		(ddContent.getDataDefAttribute());
		
		this.hexContent = new ArrayList<>();
		this.ebcdicContent = new ArrayList<>();
	}
}