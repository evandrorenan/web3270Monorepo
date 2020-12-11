package br.com.evandrorenan.web3270scripts.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString 
@Entity
public class ScriptLine {
	@Id
	@GeneratedValue
	private long idScriptLine;
	private String text;
	@ManyToOne
	private ScriptDao script;
	
	public long getIdScriptLine() {
		return idScriptLine;
	}
	public void setIdScriptLine(long idScriptLine) {
		this.idScriptLine = idScriptLine;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ScriptDao getScript() {
		return script;
	}
	public void setScript(ScriptDao script) {
		this.script = script;
	}	
}