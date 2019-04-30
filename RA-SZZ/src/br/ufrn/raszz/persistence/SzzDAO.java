package br.ufrn.raszz.persistence;

import java.util.List;
import java.util.Map;

import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.model.Refac;
import br.ufrn.raszz.model.SZZImplementationType;
import br.ufrn.raszz.model.szz.BugIntroducingCode;

public abstract class SzzDAO extends AbstractDAO {

	public abstract List<String> getLinkedRevisions(String project);
	
	public abstract List<String> getLinkedRevisionsWProcessedBIC(String project);
	
	public abstract List<String> getGitLinkedRevision(String project);
	
	public abstract List<String> getLinkedRevisionWAffectedVersions(String project);
	
	public abstract List<String> getAllRevisionProcessed(String project);
	
	public abstract Map<String,String> getAllRefDiffRevisionsProcessed(String project);
		
	public abstract void insertProjectRevisionsProcessed(String project, String revision);

	public abstract void insertBugIntroducingCode(BugIntroducingCode bicode, SZZImplementationType szzType);
		   
    public abstract boolean hasRefacFix(String path, String fixrevision, int linenumber, int adjustmentindex, String content);
	
	public abstract boolean hasRefacBic(String path, String revision, int linenumber, int adjustmentindex, String content);

	public abstract List<RefElement> getRefacBicByRevision(String revision, String project);
	
	public abstract List<RefElement> getRefacBic(String project);

	public abstract List<Refac> getRefacFix(String project);
	
	public abstract List<Object[]> getRefacBic(String path, String revision, int linenumber, int adjustmentindex, String content);
		
}
