package core.connector.util;

import java.io.File;

import org.tmatesoft.svn.core.internal.wc.DefaultSVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

public class MyDefaultSVNAuthenticationManager extends DefaultSVNAuthenticationManager
{
	public MyDefaultSVNAuthenticationManager(File configDirectory, boolean storeAuth, String userName, String password) 
	{
		super(configDirectory, storeAuth, userName, password, null, null);
    }
	
	public MyDefaultSVNAuthenticationManager(File configDirectory, boolean storeAuth, String userName, String password, File privateKey, String passphrase) 
	{
		          
		super(configDirectory, storeAuth, userName, password, privateKey, passphrase);
	}
	
	public int getConnectTimeout(SVNRepository repository) 
	{
		return 300000;
    }
	
	
	
	
}

