package br.ufrn.szz.framework.persistence;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import br.ufrn.raszz.model.szz.Bug;
import br.ufrn.raszz.model.szz.Issue;

public abstract class MetricDAO extends AbstractDAO {

	public abstract List<String> getBicodeRevisionsForProject(String project, String szztable);
	
	public abstract Long getCountRevisionsForBug(String issuecode, String szztable);
	
	public abstract Long getCountRevisionsForBugAfterReleaseDate(String issuecode, DateTime releasedate, String szztable);
	
	public abstract List<String> getIssueCodesWithAffectedVersionForProject(String project, String szztable);
		
	//public abstract List<Long> getBicodeForMetaChange(String project);

	//public abstract List<BuggyCommit> getRecentBicodeRevisionsForProject(String project);

	//public abstract List<BuggyCommit> getLargeBicodeRevisionsForProject(String project);

	public abstract List<Object[]> getFutureBugsRev(String revision, String project, String szztable);

	public abstract List<Bug> getBugsWithBicodes(String project, String szztable);
	
	public abstract Date getDateRev(String revision, String szztable);
	
	public abstract Date getReleaseDateEarliestAffectedVersion(String issuecode, String szztable);	
	
	public abstract List<Issue> getIssuesBetweenDates(String project, DateTime dateStart, DateTime dateEnd);
	
}
