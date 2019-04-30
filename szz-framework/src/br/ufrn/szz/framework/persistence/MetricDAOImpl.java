package br.ufrn.szz.framework.persistence;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.joda.time.DateTime;

import br.ufrn.raszz.model.szz.Bug;
import br.ufrn.raszz.model.szz.BugIntroducingCode;
import br.ufrn.raszz.model.szz.Issue;
import br.ufrn.raszz.util.DateUtils;
import br.ufrn.szz.framework.utils.SzzImplementationType;

public class MetricDAOImpl extends MetricDAO {
	
	@Override
	public List<String> getBicodeRevisionsForProject(String project, String szztable) {
		String sql = "select distinct(bic.revision) from " + 
				szztable + " bic " +
				"inner join linkedissuessvn lsvn " + 
				"on bic.fixrevision\\:\\:varchar = lsvn.revisionnumber " +
				"where lsvn.projectname = :project " +
				"and lsvn.issuetype = 'Bug' " +	
				"order by bic.revision "; 
			SQLQuery query = currentSession.createSQLQuery(sql);
			query.setParameter("project",project);
			List<Object> result = (List<Object>) query.list(); 
			List<String> revisions = new ArrayList<String>();
			for(Object r : result){
				revisions.add(r.toString());//.longValue());
			}
			return revisions;
	}
	
	@Override
	public Long getCountRevisionsForBug(String issuecode, String szztable) {
		String sql = "select count(distinct(bic.revision)) from " +  szztable + " bic " +
				"inner join linkedissuessvn lsvn " + 
				"on bic.fixrevision\\:\\:varchar = lsvn.revisionnumber " +
				"where lsvn.issuecode = :issuecode " +
				"and lsvn.issuetype = 'Bug'"; 
			SQLQuery query = currentSession.createSQLQuery(sql);
			query.setParameter("issuecode",issuecode);
			BigInteger lastProcessedRevision = (BigInteger) query.uniqueResult(); 
			if(lastProcessedRevision == null){
				return 0L;
			} else {
				return lastProcessedRevision.longValue();
			}			
			
			/*List<Object> result = (List<Object>) query.list(); 
			List<String> revisions = new ArrayList<String>();
			for(Object r : result){
				revisions.add(r.toString());//.longValue());
			}
			return revisions;*/
	}
	
	@Override
	public Long getCountRevisionsForBugAfterReleaseDate(String issuecode, DateTime releasedate, String szztable) {
		String sql = "select count(distinct(bic.revision)) from " +  szztable + " bic " +
				"inner join linkedissuessvn lsvn " + 
				"on bic.fixrevision\\:\\:varchar = lsvn.revisionnumber " +
				"where bic.szz_date > '" + releasedate + "'\\:\\:timestamp " +
				"and lsvn.issuecode = :issuecode " +
				"and lsvn.issuetype = 'Bug'"; 
			SQLQuery query = currentSession.createSQLQuery(sql);
			query.setParameter("issuecode",issuecode);
			/*
			List<Integer> result = (List<Integer>) query.list(); 
			for(Integer i : result)
				return (int)i;			
			return 0;*/
			
			BigInteger lastProcessedRevision = (BigInteger) query.uniqueResult(); 
			if(lastProcessedRevision == null){
				return 0L;
			} else {
				return lastProcessedRevision.longValue();
			}	
			
			/*List<Object> result = (List<Object>) query.list(); 
			List<String> revisions = new ArrayList<String>();
			for(Object r : result){
				revisions.add(r.toString());//.longValue());
			}
			return revisions;*/
	}
	
	@Override
	public List<String> getIssueCodesWithAffectedVersionForProject(String project, String szztable) {
		String sql = "select distinct(lsvn.issuecode) from " + 
				szztable + " bic " +
				"inner join linkedissuessvn lsvn " + 
				"on bic.fixrevision\\:\\:varchar = lsvn.revisionnumber " +
				"inner join issuecontents ic on lsvn.issuecode = ic.bug_id " +
				"inner join issuecontents_affectedversions ica on ic.id = ica.issuecontents_id " +
				"where lsvn.projectname = :project " +
				"and lsvn.issuetype = 'Bug' " +	
				"order by lsvn.issuecode "; 
			SQLQuery query = currentSession.createSQLQuery(sql);
			query.setParameter("project",project);
			List<Object> result = (List<Object>) query.list(); 
			List<String> revisions = new ArrayList<String>();
			for(Object r : result){
				revisions.add(r.toString());//.longValue());
			}
			return revisions;
	}
	
	
	@Override
	public synchronized List<Object[]> getFutureBugsRev(String revision, String project, String szztable){
		String sql = "select distinct(lsvn.issuecode), lsvn.createddate " +
			//"from bugintroducingcode bic " +
			//"inner join bugintroducingcode bic2 " + 
			"from " + szztable + " bic " +
			"inner join " + szztable + " bic2 " + 
			"on bic.revision = bic2.revision " +
			"inner join linkedissuessvn lsvn " + 
			"on bic2.fixrevision\\:\\:varchar = lsvn.revisionnumber " +
			"where lsvn.projectname like :project " +
			"and bic.revision = :revision " +
			"and lsvn.issuetype = 'Bug' ";
		SQLQuery query = currentSession.createSQLQuery(sql);	
		List<Object[]> result = null;
		query.setParameter("project", project);
		//if (szztable.equals(SzzImplementationType.BSZZ.getTableName()) || szztable.equals(SzzImplementationType.AGSZZ.getTableName())) {
			//query.setParameter("revision", revision);		
		//} else {
			query.setParameter("revision", Long.parseLong(revision));
		//}
		result = (List<Object[]>) query.list();		
		//converting the String dates to a Date object :-)
		for(Object[] r : result){
			r[1] = convertStringToDate((String) r[1]);
		}
		return result;
	}
	
	public synchronized List<Bug> getBugsWithBicodes(String project, String szztable){
		String sql = "select distinct(lsvn.issuecode), bic.revision, bic.szz_date " + 
			"from linkedissuessvn lsvn " +
			"inner join " + szztable + " bic " +
			"on lsvn.revisionnumber = bic.fixrevision\\:\\:varchar " +
			"where lsvn.projectname = :project and lsvn.issuetype = 'Bug' " +
			"order by lsvn.issuecode";
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project",project);
		List<Object[]> result = (List<Object[]>) query.list();
		List<Bug> bugs = new ArrayList<Bug>();
		Bug bug = null;
		String lastissuecode = null;
		for(Object[] r : result){
			String issuecode = (String) r[0];
			if(lastissuecode == null || !issuecode.equals(lastissuecode)){
				bug = new Bug();
				bug.setCode(issuecode);
				bugs.add(bug);
			}
			String revision = r[1].toString();
			Date date = (Date) r[2];
			BugIntroducingCode bic = new BugIntroducingCode();
			bic.setSzzDate(date);
			bic.setRevision(revision);
			bug.getBicodes().add(bic);
			lastissuecode = issuecode;
		}
		// still have to process this result'
		return bugs;
	}
	
	@Override
	public synchronized Date getDateRev(String revision, String szztable){
		String sql = "select distinct szz_date from " + szztable
				   + " where revision = :revision ";
		SQLQuery query = currentSession.createSQLQuery(sql);	
		List<Object> result = null;
		//if (szztable.equals(SzzImplementationType.BSZZ.getTableName()) || szztable.equals(SzzImplementationType.AGSZZ.getTableName())) {
			//query.setParameter("revision", revision);		
		//} else {
			query.setParameter("revision", Long.parseLong(revision));
		//}
		result = (List<Object>) query.list();		
		for(Object r : result){
			Date date = (Date) r;
			return date;
		}
		return null;
	}	
	
	@Override
	public synchronized Date getReleaseDateEarliestAffectedVersion(String issuecode, String szztable){
		String sql = "select min(r.releasedate) from "+ szztable + " l "
				+ "inner join linkedissuessvn i "
				+ "on l.fixrevision\\:\\:varchar = i.revisionnumber "
				+ "inner join issuecontents ic "
				+ "on i.issuecode = ic.bug_id "
				+ "inner join issuecontents_affectedversions ica "
				+ "on ic.id = ica.issuecontents_id "
				+ "inner join release r "
				+ "on ica.affectedversions = r.version and r.project = ic.product "
				+ "where i.issuecode = :issuecode";
		SQLQuery query = currentSession.createSQLQuery(sql);	
		List<Object> result = null;
		query.setParameter("issuecode", issuecode);
		result = (List<Object>) query.list();		
		for(Object r : result){
			Date date = (Date) r;
			return date;
		}
		return null;
	}
	
		
	@Override
	public synchronized List<Issue> getIssuesBetweenDates(String project, DateTime dateStart, DateTime dateEnd){
		String sql = "select distinct issuecode, issuetype, revisionnumber, commitdate\\:\\:timestamp, createddate "
				+ "from linkedissuessvn where commitdate\\:\\:timestamp between ('"+dateStart+"'\\:\\:timestamp) and ('"+dateEnd+"'\\:\\:timestamp) "
				+ "and projectname = :project "
				+ "order by commitdate";
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project",project);
		List<Object[]> result = (List<Object[]>) query.list();
		
		List<Issue> issues = new ArrayList<Issue>();	
		for(Object[] r : result){
			String issuecode = (String) r[0];
			String issuetype = r[1].toString();
			String revisionnumber = r[2].toString();
			Date commitdate = (Date) r[3];			
			Date createddate = convertStringToDate(r[4].toString());
			
			Issue issue = new Issue();
			issue.setCommitdate(commitdate);
			issue.setCreateddate(createddate);
			issue.setIssuecode(issuecode);
			issue.setIssuetype(issuetype);
			issue.setRevisionnumber(revisionnumber);
			
			issues.add(issue);			
		}
		return issues;
	}
	
	private Date convertStringToDate(String dateToParse){
		Date date = null;
		try {
			date = DateUtils.parseDateWithFormat("EEE, dd MMM yyyy HH:mm:ss zzzzz",dateToParse,null);
		} catch (ParseException e) {
			log.error(e.getMessage());
			return null;
		}
		return date;
	}
}
