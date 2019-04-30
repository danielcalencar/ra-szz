package core.connector.model;

import org.tmatesoft.svn.core.io.SVNRepository;

public class SubversionConnector extends Connector<SVNRepository> 
{
	private String repoUrl;

	public String getRepoUrl() 
	{
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) 
	{
		this.repoUrl = repoUrl;
	}
	
}
