package refdiff.core.rm2.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;
import refdiff.core.rm2.model.SDModel;

public class SvnHistoryStructuralDiffAnalyzer {
	
	Logger logger = LoggerFactory.getLogger(SvnHistoryStructuralDiffAnalyzer.class);
	private final RefDiffConfig config;
	
	public SvnHistoryStructuralDiffAnalyzer() {
        this(new RefDiffConfigImpl());
    }
	
	public SvnHistoryStructuralDiffAnalyzer(RefDiffConfig config) {
        this.config = config;
    }

	public void detectAtCommit(SVNRepository repository, String folder, String commitId, StructuralDiffHandler handler) {
		List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		SvnService svnService = new SvnServiceImpl();
		try {
			repository.log(new String[] { "" }, logEntries, Long.parseLong(commitId), Long.parseLong(commitId), true, false);
			SVNLogEntry currentCommit = logEntries.get(0);
			logEntries.clear();
			this.detectRefactorings(svnService, folder, repository, handler, currentCommit);
		} catch (Exception e) {
		    logger.warn(String.format("Ignored revision %s due to error", commitId), e);
		    handler.handleException(commitId, e);
        }		
	}
	
	protected void detectRefactorings(SvnService svnService, String tempFolder, SVNRepository repository, final StructuralDiffHandler handler, SVNLogEntry currentCommit) throws Exception {
	    long commitId = currentCommit.getRevision();
	    List<String> filesBefore = new ArrayList<String>();
	    List<String> filesCurrent = new ArrayList<String>();
		Map<String, String> renamedFilesHint = new HashMap<String, String>();
						
		svnService.fileTreeDiff(repository, currentCommit, filesBefore, filesCurrent, renamedFilesHint, false);
		// If no java files changed, there is no refactoring. Also, if there are
		// only ADD's or only REMOVE's there is no refactoring
		
		SDModelBuilder builder = new SDModelBuilder(config);
		if (filesBefore.isEmpty() || filesCurrent.isEmpty()) {
		    return;
		}
		
		// Checkout and build model for current commit
	    File folderAfter = new File(tempFolder, "v1\\" + commitId);
	    if (folderAfter.exists()) {
	        logger.info(String.format("Analyzing code after (%s) ...", commitId) + " " + new Date());
	        builder.analyzeAfter(folderAfter, filesCurrent);
	    } else {
	    	/*
	    	String[] file = new String[2];
	    	file[0] = filesCurrent.get(0);
	    	file[1] = filesCurrent.get(1);
	    	
	    	ISVNFileCheckoutTarget fileCheckoutHandler = new
	    	repository.checkoutFiles(commitId, file, null);
	    	repository.getfi
	    	*/
	        svnService.checkout(repository,folderAfter.getAbsolutePath(),filesCurrent,commitId);
	        
	        logger.info(String.format("Analyzing code after (%s) ...", commitId) + " " + new Date());
	        builder.analyzeAfter(folderAfter, filesCurrent);
	    }
	
	    Long parentCommitId = currentCommit.getRevision() - 1;
		File folderBefore = new File(tempFolder, "v0\\" + commitId);
		if (folderBefore.exists()) {
		    logger.info(String.format("Analyzing code before (%s) ...", parentCommitId)+ " " + new Date());
            builder.analyzeBefore(folderBefore, filesBefore);
		} else {
		    // Checkout and build model for parent commit
		    //svnService.checkout(repository, folderBefore.getAbsolutePath(), filesBefore);
			svnService.checkout(repository,folderBefore.getAbsolutePath(),filesBefore,parentCommitId);
		    logger.info(String.format("Analyzing code before (%s) ...", parentCommitId)+ " " + new Date());
		    builder.analyzeBefore(folderBefore, filesBefore);
		}
		final SDModel model = builder.buildModel();
		handler.svnHandle(currentCommit, model);
	}
	
	private List<String> extractList(Map<String, Long> files) {
		
		List<String> result = new ArrayList<String>();
		
		for (Map.Entry<String, Long> entry : files.entrySet()) {
			result.add(entry.getKey());
		}
		
		return result;
		
		
	}

}
