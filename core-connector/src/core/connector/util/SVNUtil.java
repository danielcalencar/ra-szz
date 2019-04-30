package core.connector.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNUtil extends SVNWCUtil 
{
	private static Boolean ourIsEclipse;
	private static final String ECLIPSE_AUTH_MANAGER_CLASSNAME = "org.tmatesoft.svn.core.internal.wc.EclipseSVNAuthenticationManager";
	
	public static ISVNAuthenticationManager createMyDefaultAuthenticationManager(String userName, String password) 
	{
		       return createMyDefaultAuthenticationManager(null, userName, password);
    }
	
	 public static ISVNAuthenticationManager createMyDefaultAuthenticationManager(File configDir, String userName, String password) {
		         DefaultSVNOptions options = createDefaultOptions(configDir, true);
		         boolean store = options.isAuthStorageEnabled();
		         return createMyDefaultAuthenticationManager(configDir, userName, password, store);
	 }
	 
	  public static ISVNAuthenticationManager createMyDefaultAuthenticationManager(File configDir, String userName, String password, boolean storeAuth) {
		          return createMyDefaultAuthenticationManager(configDir, userName, password, null, null, storeAuth);
	  }
	
	 public static ISVNAuthenticationManager createMyDefaultAuthenticationManager(File configDir, String userName, String password, File privateKey, String passphrase, boolean storeAuth) {
        // check whether we are running inside Eclipse.
        if (isEclipse()) {
            // use reflection to allow compilation when there is no Eclipse.
            try {
                ClassLoader loader = SVNWCUtil.class.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                Class<?> managerClass = loader.loadClass(ECLIPSE_AUTH_MANAGER_CLASSNAME);
                if (managerClass != null) {
                    Constructor<?> method = managerClass.getConstructor(new Class[] {
                            File.class, Boolean.TYPE, String.class, String.class, File.class, String.class
                    });
                    if (method != null) {
                        return (ISVNAuthenticationManager) method.newInstance(new Object[] {
                                configDir, storeAuth ? Boolean.TRUE : Boolean.FALSE, userName, password, privateKey, passphrase
                        });
                    }
                }
            } catch (Throwable e) {
            }
        }
        return new MyDefaultSVNAuthenticationManager(configDir, storeAuth, userName, password, privateKey, passphrase);
    }
    
    
    private static boolean isEclipse() {
    	        if (ourIsEclipse == null) {
    	            try {
    	                ClassLoader loader = SVNWCUtil.class.getClassLoader();
    	                if (loader == null) {
    	                    loader = ClassLoader.getSystemClassLoader();
    	                }
    	                Class<?> platform = loader.loadClass("org.eclipse.core.runtime.Platform");
    	                Method isRunning = platform.getMethod("isRunning", new Class[0]);
    	                Object result = isRunning.invoke(null, new Object[0]);
    	                if (result != null && Boolean.TRUE.equals(result)) {
    	                    ourIsEclipse = Boolean.TRUE;
    	                    return true;
    	                }
    	            } catch (Throwable th) {
    	            }
    	            ourIsEclipse = Boolean.FALSE;
    	        }
    	        return ourIsEclipse.booleanValue();
    	    }
}
