import java.io.ByteArrayOutputStream;
import java.io.File;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCat;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class SvnExport {

	public static void main(String... args) {

		final String svnurl = "https://LG-ED/svn/bckrepo/";
		final String destPath = "c:/tmp/svnfiles";
		final String svnUserName = "syncuser";
		final String svnPassword = "";
		System.out.println("—————————————");
		//System.out.println("Repository URL " + args[0]);
		//System.out.println("Checkout destination path: " + args[1]);

		try {

			SVNRepository repository = null;
			DAVRepositoryFactory.setup();

			// initiate the reporitory from the svnurl
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnurl));

			// create authentication data
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUserName, svnPassword);
			repository.setAuthenticationManager(authManager);

			// output some data to verify connection
			System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
			System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));

			// need to identify latest revision
			long latestRevision = repository.getLatestRevision();
			System.out.println("Repository Latest Revision: " + latestRevision);

			// create client manager and set authentication
			SVNClientManager ourClientManager = SVNClientManager.newInstance();
			ourClientManager.setAuthenticationManager(authManager);
					
		
			// use SVNUpdateClient to do the export
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);
			updateClient.doExport(repository.getLocation(), new File(destPath),
					SVNRevision.create(latestRevision), SVNRevision.create(latestRevision),
					null, true, SVNDepth.INFINITY);
			//repository.
			
			
			System.out.println("Checkout file/folder successfully !");
			System.out.println("**************************************!");

		} catch (SVNException e) {
			// e.printStackTrace();
			System.out.println("Error message :" + e.getMessage());
			System.exit(1);
		}

	}

}