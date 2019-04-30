package core.connector.factory;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;

import core.connector.model.SubversionConnector;
import core.connector.util.SVNUtil;

public class SubversionConnectorFactory extends ConnectorFactory 
{

	@Override
	public SubversionConnector createConnector(String user, String password, String url) 
	{
		SVNRepository connector = null;
		
		try
		{
			DAVRepositoryFactory.setup();
			connector = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			
			SubversionConnector svnRep = new SubversionConnector();
			svnRep.setUrl(url);
			svnRep.setPassword(password);
			svnRep.setUser(user);
			svnRep.setRepoUrl(url);
			
			ISVNAuthenticationManager authManager = SVNUtil.createMyDefaultAuthenticationManager(user, password);
	
			connector.setAuthenticationManager(authManager);
			svnRep.setEncapsulation(connector);
			
			return svnRep;
		}
		catch(SVNException svne)
		{
			svne.printStackTrace();
		}
		return null;
	}

}
