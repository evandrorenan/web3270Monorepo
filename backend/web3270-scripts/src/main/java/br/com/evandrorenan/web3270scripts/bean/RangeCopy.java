package br.com.evandrorenan.web3270scripts.bean;

import lombok.Data;

@Data
public class RangeCopy {

	private int startRow = 0;
	private int startCol = 0;
	private int endRow = 0;
	private int endCol = 0;
	
	public int getRowLength() {		
		return this.endCol - this.startCol + 1;
	}

}
