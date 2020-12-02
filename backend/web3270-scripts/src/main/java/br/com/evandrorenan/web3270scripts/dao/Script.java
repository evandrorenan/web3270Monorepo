package br.com.evandrorenan.web3270scripts.dao;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString 
@Entity
public class Script {
	@Id
	@GeneratedValue
	private long idScript;
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "script")
	private List<ScriptLine> scriptLines;
	
	public long getIdScript() {
		return idScript;
	}
	public void setIdScript(long idExtract) {
		this.idScript = idExtract;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ScriptLine> getScriptLines() {
		return scriptLines;
	}
	public void setScriptLines(List<ScriptLine> scriptLines) {
		this.scriptLines = scriptLines;
	}
}