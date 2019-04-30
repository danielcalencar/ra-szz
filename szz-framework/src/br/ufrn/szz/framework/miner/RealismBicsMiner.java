package br.ufrn.szz.framework.miner;

import java.util.*;
import java.text.ParseException;

import org.joda.time.*;

import br.ufrn.raszz.miner.Miner;
import br.ufrn.raszz.model.szz.Bug;
import br.ufrn.raszz.model.szz.BugIntroducingCode;
import br.ufrn.szz.framework.persistence.DAOType;
import br.ufrn.szz.framework.persistence.FactoryDAO;
import br.ufrn.szz.framework.persistence.MetricDAO;
import br.ufrn.szz.framework.utils.SzzImplementationType;

import java.io.*;
import org.apache.log4j.*;

/**
 * for a given bug, calculates the bug-introducing changes and the timespan
 * between them
 */

public class RealismBicsMiner extends Miner {

	private static final Logger log = Logger.getLogger(RealismBicsMiner.class);
	private MetricDAO dao;
	private String[] projects;
	private String output;
	private String szztable;

	private Console c = System.console();

	private Comparator<BugIntroducingCode> dateComp = new Comparator<BugIntroducingCode>() {
		public int compare(BugIntroducingCode bic1, BugIntroducingCode bic2) {
			Date date1 = bic1.getSzzDate();
			Date date2 = bic2.getSzzDate();
			return date1.compareTo(date2);
		}
	};

	public static void main(String[] args) {
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA",
				"Pig", "Tuscany" };

		String output = "resource/";
		SzzImplementationType szzImplementationType = SzzImplementationType.BSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.AGSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.MASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RASZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.RSZZ;
		//SzzImplementationType szzImplementationType = SzzImplementationType.LSZZ;

		RealismBicsMiner miner = new RealismBicsMiner();
		Map p = new HashMap();
		p.put("projects", projects);
		p.put("output", output);
		p.put("szztable", szzImplementationType.getTableName());
		miner.setParameters(p);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performSetup() throws Exception {
		dao = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getMetricDAO();
		this.projects = (String[]) this.getParameters().get("projects");
		this.output = (String) parameters.get("output");
		this.szztable = (String) parameters.get("szztable");

	}

	public void performMining() throws Exception {

		File file = new File(this.output + "RQ3_realismbics_" + szztable + ".csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write("project,bug,bug-introducing_changes,days\n");

		for (int j = 0; j < projects.length; j++) {

			String project = projects[j];
			log.info("stating for project: " + project);

			/*
			 * 2. Take each bug, look at its bug-introducing changes a. how many
			 * bug-introducing changes are associated with a bug b. how far
			 * apart are these bug-introducing changes for each bug?
			 */

			List<Bug> bugs = dao.getBugsWithBicodes(project, szztable);

			log.info(bugs.size() + " bugs found ");

			//int logcount = 1;

			for (Bug bug : bugs) {
				List<BugIntroducingCode> bugchanges = bug.getBicodes();

				// #debug
				// c.readLine("issuecode: " + bug.getCode());
				// c.readLine("bugchanges: " + bug.getBicodes().size());
				Collections.sort(bugchanges, dateComp);

				// get first
				BugIntroducingCode first = bugchanges.get(0);
				DateTime firstChange = new DateTime(first.getSzzDate());

				// get last
				BugIntroducingCode last = bugchanges.get(bugchanges.size() - 1);
				DateTime lastChange = new DateTime(last.getSzzDate());

				int number_of_days = Days.daysBetween(firstChange, lastChange).getDays();
				int number_of_changes = bugchanges.size();
				bw.write(project + "," + bug.getCode() + "," + number_of_changes + "," + number_of_days + "\n");
				//log.info(logcount + " bugs processed of " + bugs.size());
				//logcount++;
			}			
		}
		bw.close();
	}
}