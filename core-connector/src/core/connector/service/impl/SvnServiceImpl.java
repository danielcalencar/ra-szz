package core.connector.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc2.SvnAnnotateItem;

import core.connector.factory.SubversionConnectorFactory;
import core.connector.model.SubversionConnector;
import core.connector.service.SvnService;
import core.connector.util.SvnOperationsUtil;

public class SvnServiceImpl implements SvnService {

	Logger logger = LoggerFactory.getLogger(SvnServiceImpl.class);

	@Override
	public SVNRepository openRepository(String url, String user, String password) throws Exception {
		logger.info("open repository... ");
		SVNRepository repository;
		SubversionConnector connector = new SubversionConnectorFactory().createConnector(user, password, url);
		repository = connector.getEncapsulation();
		return repository;
	}

	@Override
	public void checkout(SVNRepository repository, String folder, List<String> files, Long commitId)
			throws Exception {
		logger.info("Checking out {} {} ...", repository.getRepositoryRoot(true), Long.toString(commitId));
		
		for (String path: files) {
			String fname = getSvnFileName(path);
			String dir = getSvnDirectory(path);
			SVNURL svnURL = repository.getRepositoryRoot(true);
			String url = svnURL.getProtocol() + "://" + svnURL.getHost() + svnURL.getURIEncodedPath() + "/";
			ByteArrayOutputStream baous = catOperation(url, path, commitId);
			
			File file = new File(folder + dir);	 	 
			if (!file.exists()) {
				file.mkdirs();
			}
			
			try (OutputStream outputStream = new FileOutputStream(file.getAbsolutePath() + "\\" + fname)) {
				baous.writeTo(outputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	@Override
	public void checkout(SVNRepository repository, String folder, Map<String,Long> filesBefore)
			throws Exception {
		logger.info("Checking out {} {} ...", repository.toString());

		for (Map.Entry<String, Long> entry : filesBefore.entrySet()) {
			String path = entry.getKey();
			Long commitId = entry.getValue();
			String fname = getSvnFileName(path);
			SVNURL svnURL = repository.getRepositoryRoot();
			String url = svnURL.getProtocol() + svnURL.getHost() + svnURL.getURIEncodedPath();
			ByteArrayOutputStream baous = catOperation(url, path, commitId);
			try (OutputStream outputStream = new FileOutputStream(folder + fname)) {
				baous.writeTo(outputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public List<String> getChangedFilePaths(SVNRepository repository, long revisionnumber) throws SVNException{		
		List<String> paths = new ArrayList<String>();	
		List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		repository.log(new String[] { "" }, logEntries, revisionnumber, revisionnumber, true, false);
		SVNLogEntry entry = logEntries.get(0);
		logEntries.clear();
		for (SVNLogEntryPath ep : entry.getChangedPaths().values()) {
			if (ep.getKind() != SVNNodeKind.FILE) continue;
			paths.add(ep.getPath());
		}
		return paths;
	}	

	@Override
	public void fileTreeDiff(SVNRepository repository, SVNLogEntry current, List<String> javaFilesBefore,
			List<String> javaFilesCurrent, Map<String, String> renamedFilesHint, boolean detectRenames)
			throws Exception {

		if (detectRenames) {
			// TODO
		}

		for (SVNLogEntryPath entry : current.getChangedPaths().values()) {
			// logger.info("analyzing path: " + entry.getPath() + " type: " +
			// entry.getType() + " kind: " + entry.getKind());
			
			if (entry.getKind() == SVNNodeKind.FILE) {
				
				// current files
				if (entry.getType() != SVNLogEntryPath.TYPE_DELETED) {
					String newPath = entry.getPath();
					if (isJavafile(newPath)) {
						javaFilesCurrent.add(newPath);
						if (entry.getType() == SVNLogEntryPath.TYPE_REPLACED) {
							// TODO
							/*
							 * String oldPath = entry.getOldPath();
							 * renamedFilesHint.put(oldPath, newPath);
							 */
						}
					}
				}
				
				// before files
				if (entry.getType() != SVNLogEntryPath.TYPE_ADDED) {
					String oldPath = entry.getPath();
					if (isJavafile(oldPath)) {
						javaFilesBefore.add(oldPath);
					}					
					
					/*
					final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
					repository.getFileRevisions(entry.getPath(), fileRevisions, 0L, current.getRevision());					
					if (fileRevisions.size() > 1) {
						SVNFileRevision beforeFile = fileRevisions.get(fileRevisions.size() - 2);
						String oldPath = beforeFile.getPath();
						Long oldRev = beforeFile.getRevision();
						if (isJavafile(oldPath)) {
							javaFilesBefore.put(oldPath, oldRev);
						}
					}
					*/
				}
			}
		}
	}

	public ByteArrayOutputStream catOperation(String repoUrl, String path, long revision) throws Exception{
		return SvnOperationsUtil.catOperation(repoUrl, path, revision);
	}

	public ByteArrayOutputStream diffOperation(String repoUrl, String currentPath, long currentRev, String nextPath, long nextRev) throws Exception{
		return SvnOperationsUtil.diffOperation(repoUrl, currentPath, currentRev, nextPath, nextRev);
	}
	
	public List<SvnAnnotateItem> annotateOperation(String repoUrl, String beforePath, long beforeRev) throws Exception{
		return SvnOperationsUtil.annotateOperation(repoUrl, beforePath, beforeRev);
	}
		
	private boolean isJavafile(String path) {
		return path.endsWith(".java");
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
	
	private String getSvnDirectory(String path) {
		if (path == null)
			return null;
		String[] tokens = path.split("/");
		if (tokens.length == 0)
			return null;
		String directory = "";
		for(int i = 0; i < tokens.length -1; i++)
			directory += tokens[i] + "/"; 
		return directory;
	}
}
