// java -Xms3g -Xmx6g -cp szz.jar:szz_lib/* br.ufrn.backhoe.miner.repminer.szz.BicodeFutureBugsMiner output/ project

package br.ufrn.szz.framework.miner;

import java.util.*;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.miner.Miner;
import br.ufrn.raszz.model.szz.Issue;
import br.ufrn.razszz.connectoradapter.SvnRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import br.ufrn.szz.framework.persistence.DAOType;
import br.ufrn.szz.framework.persistence.FactoryDAO;
import br.ufrn.szz.framework.persistence.MetricDAO;
import br.ufrn.szz.framework.utils.SzzImplementationType;
import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;

import java.io.*;

import org.apache.log4j.*;

public class FifthMetricMiner extends Miner {

	private static final Logger log = Logger
			.getLogger(FifthMetricMiner.class);

	private MetricDAO dao;
	private String[] projects;
	private String output;
	private String szztable;
	private SzzRepository repository;

	private Comparator<Object[]> dateComp = new Comparator<Object[]>() {
		@Override
		public int compare(Object[] row1, Object[] row2) {
			Date date1 = (Date) row1[1];
			Date date2 = (Date) row2[1];
			return date1.compareTo(date2);
		}
	};
	
	public static void main(String[] args) {
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", 
							  "Hadoop Common", "HBase", "Mahout", 
							  "OpenJPA", "Pig", "Tuscany" };	

		String output = "resource/";
		SzzImplementationType szzImplementationType = SzzImplementationType.BSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.AGSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.MASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.LSZZ;
		
		Map p = new HashMap();
		p.put("projects", projects);
		p.put("output", output);
		p.put("szztable", szzImplementationType.getTableName());
				
		FifthMetricMiner miner = new FifthMetricMiner();
		miner.setParameters(p);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void validateParameter() {
	}

	@Override
	public void performSetup() throws Exception {
		dao = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getMetricDAO();
		this.projects = (String[]) this.getParameters().get("projects");
		this.output = (String) parameters.get("output");
		this.szztable = (String) parameters.get("szztable");
		
		String repoUrl = this.getProperty("svn_url","./backhoe.properties");
		String user = this.getProperty("user","./backhoe.properties");
		String password = this.getProperty("password","./backhoe.properties");
		String tmpfolder =  this.getProperty("tmpfolder","./backhoe.properties");
		
		SvnService svnService = new SvnServiceImpl();
		SVNRepository svnRepository = svnService.openRepository(repoUrl, user, password);
		repository = new SvnRepositoryAdapter(svnRepository, user, password, repoUrl, tmpfolder);
	}

	@Override
	public void performMining() throws Exception {
		
		File file = new File(this.output + "RQ5_"+ szztable +".csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write("project,revision,firtbug,countabove50,countabovezero,countissues\n");
				
		for (int j = 0; j < projects.length; j++) {	
		
		String project = projects[j];
		log.info("stating for project: " + project);
	
			List<String> revisions = dao.getBicodeRevisionsForProject(project, szztable);
			log.info(revisions.size() + " bug introducing revisions found ");
			
			for (String revision : revisions) {				
				Date row = dao.getDateRev(revision, szztable);
				DateTime dateRev = new DateTime(row);
				
				List<Object[]> rows = dao.getFutureBugsRev(revision, project, szztable);
				Collections.sort(rows, dateComp);
				
				// get first
				Object[] first = rows.get(0);
				DateTime dateFirstBug = new DateTime((Date) first[1]);

				List<Issue> issues = dao.getIssuesBetweenDates(project, dateRev, dateFirstBug);
				
				int[] counts = countIssueAbovePercentange(revision, issues, 50.0);
				
				//System.out.println(revision + "," + (String) first[0] + "," + counts[0] + "," + counts[1] + "," + issues.size());
							
				bw.write(revision + "," + (String) first[0] + "," + counts[0] + "," + counts[1] + "," + issues.size() + "\n");
			}
		}
		bw.close();
	}
	
	private int[] countIssueAbovePercentange(String revision, List<Issue> issues, double refPercentange) throws Exception {		
		List<String> basePaths = repository.getChangedPaths(revision);
		
		basePaths = basePaths.stream()
	     .filter(path -> path.contains(".java"))
	     .collect(Collectors.toList());		
		int countBase = basePaths.size();
					
		int countIssueRefperc = 0;
		int countIssueZeroperc = 0;
		for(Issue issue: issues) {
			List<String> paths = repository.getChangedPaths(issue.getRevisionnumber());
			paths = paths.stream()
				     .filter(path -> path.contains(".java"))
				     .collect(Collectors.toList());	
			int countMathPaths = 0;	
			for(String path: paths) {
				countMathPaths = basePaths.contains(path)? countMathPaths+1: countMathPaths;
			}
			double percentage = ((double) countMathPaths/countBase)*100;
			if (percentage >= refPercentange) {
				//System.out.println(issue.getRevisionnumber() + ": " +countMathPaths + "/" + countBase + ": " + percentage +"%");
				countIssueRefperc++;
			}
			if (percentage > 0) {
				//System.out.println(issue.getRevisionnumber() + ": " +countMathPaths + "/" + countBase + ": " + percentage +"%");
				countIssueZeroperc++;
			}
		}
		int[] r = {countIssueRefperc, countIssueZeroperc};
		return r;
	}

}