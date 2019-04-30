// java -Xms3g -Xmx6g -cp szz.jar:szz_lib/* br.ufrn.backhoe.miner.repminer.szz.BicodeFutureBugsMiner output/ project

package br.ufrn.szz.framework.miner;

import java.util.*;
import org.joda.time.DateTime;
import org.joda.time.Days;

import br.ufrn.raszz.miner.Miner;
import br.ufrn.szz.framework.persistence.DAOType;
import br.ufrn.szz.framework.persistence.FactoryDAO;
import br.ufrn.szz.framework.persistence.MetricDAO;
import br.ufrn.szz.framework.utils.SzzImplementationType;

import java.io.*;

import org.apache.log4j.*;

public class FutureBugsMiner extends Miner {

	private static final Logger log = Logger
			.getLogger(FutureBugsMiner.class);

	private MetricDAO dao;
	private String[] projects;
	private String output;
	private String szztable;

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
				
		FutureBugsMiner miner = new FutureBugsMiner();
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
	}

	@Override
	public void performMining() throws Exception {
		
		File file = new File(this.output + "RQ2_futurebugs_"+ szztable +".csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write("project,revision,numberofbugs,days\n");
				
		for (int j = 0; j < projects.length; j++) {	
		
		String project = projects[j];
		log.info("stating for project: " + project);

			/* 1. take each bugintroducing change 
			 * a. how many future bugs does it leads to 
			 * b. how far apart are these future bugs? 
			 * 
			 * 2. Take each bug, look at its bug-introducing changes 
			 * a. how many bug-introducing changes are associated with a bug 
			 * b. how far apart are these bug-introducing changes for each bug?
			 */
	
			List<String> revisions = dao.getBicodeRevisionsForProject(project, szztable);
			log.info(revisions.size() + " bug introducing revisions found ");
			//int logcount = 1;
	
			for (String revision : revisions) {
				List<Object[]> rows = dao.getFutureBugsRev(revision, project, szztable);
				Collections.sort(rows, dateComp);
				
				// get first
				Object[] first = rows.get(0);
				DateTime firstBug = new DateTime((Date) first[1]);
	
				// get last
				Object[] last = rows.get(rows.size() - 1);
				DateTime lastBug = new DateTime((Date) last[1]);
	
				int numberofdays = Days.daysBetween(firstBug, lastBug).getDays();
				int numberofbugs = rows.size();
				
				bw.write(project + "," + revision + "," + numberofbugs + "," + numberofdays + "\n");
	
				//log.info(logcount + " revisions processed of " + revisions.size());
				//logcount++;
			}
		}
		bw.close();
	}

}