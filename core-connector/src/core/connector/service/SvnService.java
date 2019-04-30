package core.connector.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc2.SvnAnnotateItem;

public interface SvnService {
	
	SVNRepository openRepository(String url, String user, String password) throws Exception;
	
	void checkout(SVNRepository repository, String folder, List<String> files, Long commitId) throws Exception;

	void checkout(SVNRepository repository, String folder, Map<String,Long> filesBefore) throws Exception;
	
	void fileTreeDiff(SVNRepository repository, SVNLogEntry currentCommit, List<String> filesBefore, List<String> filesCurrent, Map<String, String> renamedFilesHint, boolean detectRenames) throws Exception;
	
	List<String> getChangedFilePaths(SVNRepository repository, long revisionnumber) throws SVNException;
	
	ByteArrayOutputStream catOperation(String repoUrl, String path, long revision) throws Exception;
	
	ByteArrayOutputStream diffOperation(String repoUrl, String currentPath, long currentRev, String nextPath, long nextRev) throws Exception;
	
	List<SvnAnnotateItem> annotateOperation(String repoUrl, String beforePath, long beforeRev) throws Exception;
}
