package br.ufrn.raszz.persistence;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;

import br.ufrn.raszz.model.DiffLine;

public class UtilQueryDAOImpl extends UtilQueryDAO {

	@Override
	public synchronized List<Object[]> getBugcodesByProject(String project){
		String sql = "select linenumber, path, content, revision, fixrevision, project "
				+ "from bugintroducingcode " +
			"where project = :project";
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		List<Object[]> bugcodes = query.list();
		return bugcodes;
	}
	
	@Override
	public synchronized List<Object[]> getBICwithRefacByProject(String project){
		String sql = "select linenumber, path, content, revision, fixrevision, project "
				+ "from bicszzse2 " +
			"where isrefac = 't' and project = :project";
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		List<Object[]> bugcodes = query.list();
		return bugcodes;
	}
	
	@Override
	public synchronized List<Object[]> getBICwithRefacByProject2(String project){
		String sql = "select linenumber, path, content, revision, fixrevision, project "
				+ "from bugintroducingcode " +
			"where refdiffindex not in (0, 3, 4, 102) and project = :project";
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		List<Object[]> bugcodes = query.list();
		return bugcodes;
	}
	
	@Override
	public synchronized List<Long> getSampledRevisionWithBugsWithBICIdentifiedByProject(String project) {
		String sql = "select distinct revisionnumber from linkedissuessvn "
				+ "where linkedissuessvn.issample = '1' "
				+ "and projectname = :project "
				+ "order by revisionnumber";		
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		List<Long> result = query.list();	
		return result;
	}
	
	@Override
	public synchronized void updateContentDiff(long bicRev, long linenumber, String path, String project, String contentDiff){ 
		String sql = "update bicszzse set contentDiff = :param5 where " + 
			"revision = :param1 and linenumber = :param2 and " +
		    "path = :param3 and project = :param4";
		List<Object> values = new ArrayList<Object>();
		executeSQLWithParams(sql, bicRev, linenumber, path, project, contentDiff);
	}
	
	@Override
	public synchronized List<Object[]> getBicWithContentDiffNullByProject(String project){
		String sql = "select distinct linenumber, path, revision, fixrevision from bugintroducingcode "
				+ "where project = :project and contentdiff is null";		
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project",project);
		List<Object[]> result = query.list();
		return result;
	}

	@Override
	public synchronized List<Object[]> getRealeasesByProject(String project){
		String sql = "select distinct path, revision from bugintroducingcode "
				+ "where project = :project and contentdiff is not null";		
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project",project);
		List<Object[]> result = query.list();
		return result;
	}
	
	@Override
	public synchronized void updateSample(long linenumber, String path, String content, long bicRev, long fixRev, String project){ 
		String sql = "update bugintroducingcode set issample = '1' where " + 
			"linenumber = :param1 and path = :param2 and " +
		    "content = :param3 and revision = :param4 and "
		    + "fixrevision = :param5 and project = :param6";
		List<Object> values = new ArrayList<Object>();
		executeSQLWithParams(sql, linenumber, path, content, bicRev, fixRev, project);
	}	
	
	@Override
	public synchronized void updateSample2(String issuecode){ 
		String sql = "update linkedissuessvn set issample = '1' where " + 
			"issuecode = :param1 " +
		    "and revisionnumber in (select distinct cast(fixrevision as text) from bugintroducingcode)";
		List<Object> values = new ArrayList<Object>();
		executeSQLWithParams(sql, issuecode);
	}
	
	@Override
	public synchronized void updateSample3(long linenumber, String path, String content, long bicRev, long fixRev, String project){ 
		String sql = "update bugintroducingcode set issample2 = '1' where " + 
			"linenumber = :param1 and path = :param2 and " +
		    "content = :param3 and revision = :param4 and "
		    + "fixrevision = :param5 and project = :param6";
		List<Object> values = new ArrayList<Object>();
		executeSQLWithParams(sql, linenumber, path, content, bicRev, fixRev, project);
	}
	
	@Override
	public synchronized void updateSampleBic2(long linenumber, String path, String content, long bicRev, long fixRev, String project){ 
		String sql = "update bicszzse2 set issample = '1' where " + 
			"linenumber = :param1 and path = :param2 and " +
		    "content = :param3 and revision = :param4 and "
		    + "fixrevision = :param5 and project = :param6";
		List<Object> values = new ArrayList<Object>();
		executeSQLWithParams(sql, linenumber, path, content, bicRev, fixRev, project);
	}
	
	@Override
	public synchronized List<String> getLinkedBugsWithBICIdentifiedByProject(String project){
		String sql = "select distinct issuecode "
				+ "from linkedissuessvn "
				+ "where projectname = :project "
				+ "and revisionnumber in (select distinct cast(fixrevision as text) from bugintroducingcode)"
				+ "order by issuecode";		
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		return query.list();	
	}
	
	@Override
	public synchronized void insertDiffLine(DiffLine dl){
		String sql = "insert into diffline values (:param1,:param2,:param3,:param4,:param5,:param6)";
		executeSQLWithParams(sql,dl.getRevision(), dl.getFixRevision(), 
							 	 dl.getNumberline(), dl.getContent(), 
							 	 dl.getProject(), dl.getPath());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<Long> getAllRevisionByProject(String project) {
		String sql = "select distinct cast (revisionnumber as bigint) as rev from linkedissuessvn "
				+ "where projectname = :project "
				+ "union "
				+ "select distinct cast (revision as bigint) as rev from bugintroducingcode "
				+ "where project = :project "				
				+ "order by rev";		
		
		List<Long> revisionsConverted = new ArrayList<Long>();
		List<BigInteger> revisions = new ArrayList<BigInteger>();
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		revisions = query.list();
		
		for (BigInteger revision : revisions) {
			long revisionconverted = revision.longValue();
			revisionsConverted.add(revisionconverted);
		}
		return revisionsConverted;
	}
	
	@Override
	public synchronized void insertCommitIndex(Long revision, Long qtdFilesAdd , Long qtdLinesAdd,
			Long qtdFilesDel, Long qtdLinesDel, Long qtdFilesMod, Long qtdLinesCon, Long qtdFilesRep, String project){
		String sql = "INSERT INTO commitindex "
				+ "values (:param1,:param2, :param3, :param4, :param5, :param6, :param7, :param8, :param9)";
		executeSQLWithParams(sql,revision, qtdFilesAdd , qtdLinesAdd,
				qtdFilesDel, qtdLinesDel, qtdFilesMod, qtdLinesCon, qtdFilesRep, project);	       
	}
	
	@Override
	public synchronized List<Long> getLinkedRevisions(String project) {
		String sql = "select distinct(revisionnumber\\:\\:bigint) from linkedissuessvn lsvn " +
			"where projectname like :project " +
			"and issuetype = 'Bug' " +
		//	"and issuecode like 'TUSCANY-1867'" + //onlytest
		//	"and revisionnumber = '790999' " + //'698203' " + // '638810' " +  //'1045314' " + //'698203' " + '404728' " + // '521426'" + // '1466557'" + //onlytest 
			"order by revisionnumber\\:\\:bigint";

		List<Long> revisionsConverted = new ArrayList<Long>();
		List<BigInteger> revisions = new ArrayList<BigInteger>();
		SQLQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("project", project);
		revisions = query.list();

		for (BigInteger revision : revisions) {
			long revisionconverted = revision.longValue();
			revisionsConverted.add(revisionconverted);
		}
		return revisionsConverted;
	}

}
