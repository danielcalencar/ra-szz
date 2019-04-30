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

public class EarliestBugAppearanceMiner extends Miner {

	private static final Logger log = Logger
			.getLogger(EarliestBugAppearanceMiner.class);

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
		//String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA", "Pig", "Tuscany" };	
		String[] projects = { "Camel" };	

		String output = "resource/";
		//SzzImplementationType szzImplementationType = SzzImplementationType.BSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.AGSZZ;
		SzzImplementationType szzImplementationType = SzzImplementationType.MASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.LSZZ;
		
		Map p = new HashMap();
		p.put("projects", projects);
		p.put("output", output);
		p.put("szztable", szzImplementationType.getTableName());
				
		EarliestBugAppearanceMiner miner = new EarliestBugAppearanceMiner();
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
		File file = new File(this.output + "RQ1_"+ szztable +"_CAMEL.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write("project,totalbugs,disagreementratio\n");				
		for (int j = 0; j < projects.length; j++) {			
			String project = projects[j];
			log.info("stating for project: " + project);
	
			//1) recupera todas as bugs de um S
			List<String> issuecodes = dao.getIssueCodesWithAffectedVersionForProject(project, szztable);
			log.info(issuecodes.size() + " bugs found with affected version");
			
			//int bugswithaffectedversion = 0;	
			int disagreementnumber = 0;			
			for (String issuecode : issuecodes) {	
				//2) recupera a releasedate da earliest affected version de uma bug
				Date row = dao.getReleaseDateEarliestAffectedVersion(issuecode, szztable);
				//if (row == null) continue; //quando não há affected version para o bug
				DateTime releasedate = new DateTime(row);
				//bugswithaffectedversion++;
				
				//3) recupera o total de  potential BICs de um bug
				Long countPotentialRevs = dao.getCountRevisionsForBug(issuecode, szztable);

				//4) recupera a qtd de potential BIC after releasedate, i.e. incorrets
				Long countIncorrectRevs = dao.getCountRevisionsForBugAfterReleaseDate(issuecode, releasedate, szztable);

				if (countPotentialRevs == countIncorrectRevs) { 
					disagreementnumber++;
					log.info("  - bug in disagreement: " + issuecode);
				}
			}
				
			System.out.println(project + "," + issuecodes.size() + "," + disagreementnumber + "," + (double)((double)disagreementnumber/(double)issuecodes.size()));
			bw.write(project + "," + issuecodes.size() + "," + disagreementnumber + "\n");
		}
		bw.close();
	}
}