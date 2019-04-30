import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNFileCheckoutTarget;
import org.tmatesoft.svn.core.io.SVNRepository;

import core.connector.factory.SubversionConnectorFactory;
import core.connector.model.Connector;
import core.connector.model.SubversionConnector;
import core.connector.model.enums.ConnectorType;
import core.connector.util.SvnOperationsUtil;

public class ManualAnalysisUtils extends Miner {

	private String project;
	private SubversionConnector connector;
	private String repoUrl;
	private SVNRepository encapsulation;

	public static void main(String[] args) throws Exception {

		String project = "Camel";
		String user = "syncuser";
		String password = "";

		ManualAnalysisUtils test = new ManualAnalysisUtils();
		String url = "https://LG-ED/svn/bckrepo/";
		Map<ConnectorType, Connector> cs = new HashMap<ConnectorType, Connector>();
		SubversionConnector c = null;
		try {
			c = new SubversionConnectorFactory().createConnector(user,
					password, url);
		} catch (Exception e) {
			// TODO
		}
		cs.put(ConnectorType.SVN, c);
		test.setConnectors(cs);
		Map p = new HashMap();
		try {
			p.put("project", project);
			p.put("connector", c);
			p.put("batchSize", 300L);
			p.put("paths", new String[] { "" });
			p.put("existingdata", false);
			test.setParameters(p);
			test.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performSetup() throws Exception {
		try {
			this.project = (String) this.getParameters().get("project");
			connector = (SubversionConnector) connectors.get(ConnectorType.SVN);
			repoUrl = connector.getRepoUrl();
			encapsulation = connector.getEncapsulation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void performMining() throws Exception {
		List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		Long commitId = (long) 428339;
		encapsulation.log(new String[] { "" }, logEntries, commitId, commitId, true, false);
		SVNLogEntry currentCommit = logEntries.get(0);
		
		String[] files = new String[4];
		int i = 0;
		for (SVNLogEntryPath entry : currentCommit.getChangedPaths().values()) {
			if (entry.getType() != SVNLogEntryPath.TYPE_DELETED) {
				String newPath = entry.getPath();
				files[i++] = newPath;
			}
		}
				
		encapsulation.checkoutFiles(commitId, files, null);
		
		
		logEntries.clear();
		for (SVNLogEntryPath entry : currentCommit.getChangedPaths().values()) {
			SVNURL svnURL = encapsulation.getRepositoryRoot();
			String url = svnURL.getProtocol() + "://" + svnURL.getHost() + svnURL.getURIEncodedPath() + "/"; //"https://LG-ED/svn/bckrepo/";
			ByteArrayOutputStream baous = SvnOperationsUtil.catOperation(url, entry.getPath(), commitId);
		}		
	}

	private String getSvnFileName(String path) {
		if (path == null)
			return null;
		String[] tokens = path.split("/");
		if (tokens.length == 0)
			return null;
		String lastPart = tokens[tokens.length - 1];
		return lastPart;
	}

}
