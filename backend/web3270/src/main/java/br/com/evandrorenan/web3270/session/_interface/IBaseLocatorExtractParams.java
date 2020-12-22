package br.com.evandrorenan.web3270.session._interface;

public interface IBaseLocatorExtractParams {
	
	public String getUser();
	public String getPassword();
	public String getProgramName();
	public String getAbendId();
	public String getAbendFile();
	
	public void setUser(String user);
	public void setPassword(String password);
	public void setProgramName(String programName);
	public void setAbendId(String abendId);
	public void setAbendFile(String abendFile);
	
}
