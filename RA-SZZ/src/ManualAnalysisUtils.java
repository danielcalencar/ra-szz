import static br.ufrn.raszz.util.FileOperationsUtil.getDiffHunks;
import static br.ufrn.raszz.util.FileOperationsUtil.getHeaders;
import static br.ufrn.raszz.util.FileOperationsUtil.isCommentOrBlankLine;
import static br.ufrn.raszz.util.FileOperationsUtil.isImport;
import static br.ufrn.raszz.util.FileOperationsUtil.isTestFile;
import static br.ufrn.raszz.util.FileOperationsUtil.runRegex;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.incava.analysis.Report;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.miner.szz.RaSZZ;
import br.ufrn.raszz.model.DiffLine;
import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.szz.DiffHunk;
import br.ufrn.raszz.model.szz.Line;
import br.ufrn.raszz.model.szz.LineType;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import br.ufrn.raszz.persistence.DAOType;
import br.ufrn.raszz.persistence.FactoryDAO;
import br.ufrn.raszz.persistence.UtilQueryDAO;
import br.ufrn.raszz.util.DiffJOperationsUtil;
import br.ufrn.raszz.util.FileOperationsUtil;
import br.ufrn.razszz.connectoradapter.SvnRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import core.connector.factory.SubversionConnectorFactory;
import core.connector.model.Connector;
import core.connector.model.SubversionConnector;
import core.connector.model.enums.ConnectorType;
import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;

public class ManualAnalysisUtils extends Miner {

	private static final Logger log = Logger.getLogger(ManualAnalysisUtils.class);
	private String repoUrl;
	private SzzRepository repository;
	private static UtilQueryDAO queryDao;

	public static void main(String[] args) throws Exception {

		RaSZZ szz = new RaSZZ();
		String url = szz.getProperty("svn_url","./backhoe.properties");
		String user = szz.getProperty("user","./backhoe.properties");
		String password = szz.getProperty("password","./backhoe.properties");
		String tmpfolder =  szz.getProperty("tmpfolder","./backhoe.properties");
		
		RepositoryType repoType = RepositoryType.SVN;
			
		Map p = new HashMap();
		try {
			p.put("user", user);
			p.put("password", password);
			p.put("tmpfolder", tmpfolder);
			p.put("repoUrl", url);
			szz.setParameters(p);
			szz.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performSetup() throws Exception {
		log.info("perform setup ... ");
		try {
			String user = (String) this.getParameters().get("user");
			String password = (String) this.getParameters().get("password");
			repoUrl =  (String) this.getParameters().get("repoUrl");
			String tmpfolder = (String) this.getParameters().get("tmpfolder");
			
			SvnService svnService = new SvnServiceImpl();
			SVNRepository svnRepository = svnService.openRepository(repoUrl, user, password);
			repository = new SvnRepositoryAdapter(svnRepository, user, password, repoUrl, tmpfolder);
			
			queryDao = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getUtilQueryDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void performMining() throws Exception {
		// executeDiffJ();
		// executeDiff2();
		// executeSaveFile();
		// radomBIC(50);
		
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA",
				"Pig", "Tuscany" };
		
		//int[] sample = {16, 40, 23,	68, 55, 89, 5, 12, 17, 39};
		int[] sample = {13, 25, 9,	44, 51, 150, 5, 10, 18, 23};
		for(int i = 0; i <10; i++) {
			//radomSampleBICwithRefac(sample[i],projects[i]);
			radomSampleRefac2(sample[i],projects[i]);
		}
		
		//radomBUG(29, project);
		/*List<Long> commitIds = null;
		synchronized (queryDao) {
			commitIds = queryDao.getSampledRevisionWithBugsWithBICIdentifiedByProject(project);
		}
		System.out.println(commitIds.size());*/

		// buildAnnotationGraph();
		// countNumberModifiedLines();
	}

	public void radomSampleBICwithRefac(int qtd, String project) { //amostra de casos que não conseguiu descer
		Random gerador = new Random();
		List<Object[]> bics = null;
		synchronized (queryDao) {
			bics = queryDao.getBICwithRefacByProject(project);
		}
		int size = bics.size();
		synchronized (queryDao) {
			for (int i = 0; i < qtd; i++) {
				int n = gerador.nextInt(size);
				System.out.print(n);
				System.out.println(": " + bics.get(n)[0].toString());
				queryDao.updateSampleBic2(Long.parseLong(bics.get(i)[0].toString()), bics.get(i)[1].toString(),
						bics.get(i)[2].toString(), Long.parseLong(bics.get(i)[3].toString()),
						Long.parseLong(bics.get(i)[4].toString()), bics.get(i)[5].toString());
			}
			Transaction tx = queryDao.beginTransaction();
			tx.commit();
		}
	}
	
	public void radomSampleRefac2(int qtd, String project) { //amostra de casos que não conseguiu descer
		Random gerador = new Random();
		List<Object[]> bics = null;
		synchronized (queryDao) {
			bics = queryDao.getBICwithRefacByProject2(project);
		}
		int size = bics.size();
		synchronized (queryDao) {
			for (int i = 0; i < qtd; i++) {
				int n = gerador.nextInt(size);
				System.out.print(n);
				System.out.println(": " + bics.get(n)[0].toString());
				queryDao.updateSample3(Long.parseLong(bics.get(i)[0].toString()), bics.get(i)[1].toString(),
						bics.get(i)[2].toString(), Long.parseLong(bics.get(i)[3].toString()),
						Long.parseLong(bics.get(i)[4].toString()), bics.get(i)[5].toString());
			}
			Transaction tx = queryDao.beginTransaction();
			tx.commit();
		}
	}
	
	public void radomBIC(int qtd, String project) {
		// instância um objeto da classe Random usando o construtor básico
		Random gerador = new Random();

		List<Object[]> bics = null;
		synchronized (queryDao) {
			bics = queryDao.getBugcodesByProject(project);
		}
		int size = bics.size();
		// imprime sequência de 10 números inteiros aleatórios entre 0 e 25
		synchronized (queryDao) {
			for (int i = 0; i < qtd; i++) {
				int n = gerador.nextInt(size);
				System.out.print(n);
				// linenumber, path, content, revision, fixrevision, project
				// ,szz_date
				System.out.println(": " + bics.get(n)[0].toString());

				queryDao.updateSample(Long.parseLong(bics.get(i)[0].toString()), bics.get(i)[1].toString(),
						bics.get(i)[2].toString(), Long.parseLong(bics.get(i)[3].toString()),
						Long.parseLong(bics.get(i)[4].toString()), bics.get(i)[5].toString());
			}
			Transaction tx = queryDao.beginTransaction();
			tx.commit();
		}
	}

	public void radomBUG(int qtd, String project) {
		// instância um objeto da classe Random usando o construtor básico
		Random gerador = new Random();

		List<String> bugs = null;
		synchronized (queryDao) {
			bugs = queryDao.getLinkedBugsWithBICIdentifiedByProject(project);
		}
		int size = bugs.size();
		// imprime sequência de 10 números inteiros aleatórios entre 0 e 25
		synchronized (queryDao) {
			for (int i = 0; i < qtd; i++) {
				int n = gerador.nextInt(size);
				System.out.print(n);
				System.out.println(project + " : " + bugs.get(n).toString());
				queryDao.updateSample2(bugs.get(i).toString());
			}
			Transaction tx = queryDao.beginTransaction();
			tx.commit();
		}
	}

	private void executeDiffJ() throws SVNException {/*
		final String path = // "/incubator/tuscany/contrib/java/trunk/das/rdb/src/test/java/org/apache/tuscany/das/rdb/test/DefectTests.java";
				"/incubator/activemq/trunk/activemq-core/src/main/java/org/activemq/transport/stomp/StompTransportFactory.java";
		int fixRev = 358217;
		// 411881; //
		// 367766; //
		// int lineNumber = 33;
		String lineContent = "public class StompTransportFactory extends TransportFactory {";
		Line line = new Line();
		line.setContent(lineContent);
		line.setNumber(32);
		line.setPreviousNumber(33);
		line.setType(LineType.DELETION);

		String fname = getSvnFileName(path);
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		repository.getFileRevisions(path, fileRevisions, 0L, fixRev);

		final LinkedList<SzzFileRevision> szzFileRevisions = repository.convertFileRevisions(fileRevisions);
		final SzzFileRevision fr = szzFileRevisions.getLast();
		final SzzFileRevision nextFr = szzFileRevisions.get(szzFileRevisions.indexOf(fr) - 1);

		Report diffReport = DiffJOperationsUtil.diffJOperation(repoUrl, fr, nextFr, fname);
		diffReport.printAll();

		System.out.println("===================== \n");

		System.out.println(DiffJOperationsUtil.hasDiffJType(diffReport, line).toDiffSummaryString());*/

	}

	private void t() {
		if (false) {
			System.out.println("oi");
		} else {
			return;
		}
	}

	private void executeDiff2() {/*
		t();
		long bicRev = 1305424;
		int linenumber = 309;
		final String path = "/geronimo/server/branches/3.0-beta/framework/modules/geronimo-deployment/src/main/java/org/apache/geronimo/deployment/Deployer.java";
		final LinkedList<SzzFileRevision> szzFileRevisions = repository.extractSZZFilesFromPath(repoUrl, path, bicRev, false);
			
		int i = szzFileRevisions.size() - 1;
		if (szzFileRevisions.size() != 1) {
			final SzzFileRevision fr = szzFileRevisions.get(i);
			final SzzFileRevision lastFr = szzFileRevisions.get(--i);
			final ByteArrayOutputStream diff = repository.diffOperation(repoUrl, lastFr, fr);
			if (diff.size() != 0) {
				String contentDiff = diff.toString();
				System.out.println(contentDiff);
			}
		}
*/
	}

	private void executeDiff(String project) {
		/*
		 * int bicRev = 641567; // 369828; // 366216; int fixRev = 656521; //
		 * 396651; // 394728; long linenumber = 80;
		 * 
		 * final String path =
		 * "/incubator/tuscany/java/sca/modules/node/src/main/java/org/apache/tuscany/sca/node/management/SCANodeManagerService.java"
		 * ; //
		 * "/incubator/tuscany/java/sdo/impl/src/main/java/org/apache/tuscany/sdo/impl/SDOFactoryImpl.java"
		 * ; //
		 * "/incubator/tuscany/contrib/java/sandbox/sdo/impl/src/main/java/org/apache/tuscany/sdo/helper/TypeHelperImpl.java"
		 * ; //
		 * "/incubator/tuscany/java/sdo/impl/src/main/java/org/apache/tuscany/sdo/helper/TypeHelperImpl.java"
		 * ;
		 */

		List<Object[]> bicRevs = null;
		synchronized (queryDao) {
			bicRevs = queryDao.getBicWithContentDiffNullByProject(project);
		}
		log.info("Project " + project + " starting...");
		log.info(bicRevs.size() + " BICs found...");
		long i = 0;
		for (Object[] bic : bicRevs) {
			long linenumber = ((java.math.BigInteger) bic[0]).longValue(); // linenumber
			String path = bic[1].toString(); // path
			int bicRev = ((java.math.BigInteger) bic[2]).intValue(); // revision
			int fixRev = ((java.math.BigInteger) bic[3]).intValue(); // fixrevision
			printDiffBetweenLastFileRevisions(bicRev, path, linenumber, project);
			// printDiffBetweenBicRevAndFixRev(bicRev, fixRev, path);
			// printPathOfLog(fixRev);
			// printFileRevisions(bicRev, fixRev, path);
			log.info("BICs " + ++i + " was processed already!");
		}
	}

	private void executeSaveFile(String project) {
		List<Object[]> linkedRevs = null;
		synchronized (queryDao) {
			linkedRevs = queryDao.getRealeasesByProject(project);
		}
		log.info("Project " + project + " starting...");
		log.info(linkedRevs.size() + " Revs found...");
		for (Object[] revInfo : linkedRevs) {
			String path = revInfo[0].toString(); // path
			int rev = ((java.math.BigInteger) revInfo[1]).intValue(); // revision
			saveLastFileRevisions(path, rev);
		}
	}

	private void printDiffBetweenBicRevAndFixRev(int bicRev, int fixRev, String path) throws SVNException {
		/*final LinkedList<SVNFileRevision> bicFileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> bicSzzFileRevisions = new LinkedList<SzzFileRevision>();

		final LinkedList<SVNFileRevision> fixFileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> fixSzzFileRevisions = new LinkedList<SzzFileRevision>();

		try {
			repository.getFileRevisions(path, bicFileRevisions, bicRev, bicRev);
			repository.getFileRevisions(path, fixFileRevisions, fixRev, fixRev);
		} catch (SVNException e) {
			if (e.getMessage().contains("is not a file in revision")) {
			} else {
				throw e;
			}
		}
		convertFileRevisions(bicFileRevisions, bicSzzFileRevisions);
		convertFileRevisions(fixFileRevisions, fixSzzFileRevisions);
		final ByteArrayOutputStream diff = diffOperation(repoUrl, bicSzzFileRevisions.get(0),
				fixSzzFileRevisions.get(0));
		System.out.println(diff.toString());
		System.out.println("===================== \n");*/
	}

	private void printPathOfLog(int rev) {/*
		List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		try {

			repository.log(new String[] { "" }, logEntries, rev, rev, true, false);
		} catch (SVNException svne) {
			svne.printStackTrace();
		}

		SVNLogEntry entry = logEntries.get(0);
		logEntries.clear();
		for (SVNLogEntryPath ep : entry.getChangedPaths().values()) {
			final String path = ep.getPath();
			System.out.println(path);
		}
		System.out.println("===================== \n");*/
	}

	public void printFileRevisions(int bicRev, int fixRev, String path) {/*
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		try {
			repository.getFileRevisions(path, fileRevisions, bicRev, fixRev);
		} catch (SVNException e) {
		}

		convertFileRevisions(fileRevisions, szzFileRevisions);

		for (final SzzFileRevision fr : szzFileRevisions) {
			// final SzzFileRevision nextFr =
			// szzFileRevisions.get(szzFileRevisions.indexOf(fr) + 1);
			// final ByteArrayOutputStream diff = diffOperation(repoUrl, fr,
			// nextFr);
			System.out.println(fr.getPath());

			// System.out.println(diff.toString());
			// final ByteArrayOutputStream nextFrContent = catOperation(repoUrl,
			// nextFr);
		}
		System.out.println("=====================");*/
	}

	public void printDiffBetweenLastFileRevisions(int bicRev, String path, long linenumber, String project) {/*
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		try {
			repository.getFileRevisions(path, fileRevisions, 0L, bicRev);
		} catch (SVNException e) {
		}

		convertFileRevisions(fileRevisions, szzFileRevisions);
		int i = szzFileRevisions.size() - 1;
		// boolean isFinished = true;
		// do {
		if (szzFileRevisions.size() != 1) {
			final SzzFileRevision fr = szzFileRevisions.get(i);
			final SzzFileRevision lastFr = szzFileRevisions.get(--i);
			final ByteArrayOutputStream diff = diffOperation(repoUrl, lastFr, fr);
			if (diff.size() != 0) {
				// i = 0;
				// isFinished = false;
				String contentDiff = diff.toString();
				synchronized (queryDao) {
					Transaction tx = queryDao.beginTransaction();
					queryDao.updateContentDiff(bicRev, linenumber, path, project, contentDiff);
					tx.commit();
				}
				// System.out.println("i: " + i);
				// System.out.println(contentDiff);
			} // else isFinished = true;
		}
		// } while (i != 0); //(!isFinished);
		// System.out.println("=====================");*/
	}

	public void saveLastFileRevisions(String path, long rev) {/*
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		try {
			repository.getFileRevisions(path, fileRevisions, 0L, rev);
		} catch (SVNException e) {
		}
		convertFileRevisions(fileRevisions, szzFileRevisions);
		int i = szzFileRevisions.size() - 1;
		do {
			if (szzFileRevisions.size() != 1) {
				final SzzFileRevision fr = szzFileRevisions.get(i);
				final SzzFileRevision lastFr = szzFileRevisions.get(--i);

				String fname = getSvnFileName(path);
				System.out.println("Current Rev: " + rev);
				ByteArrayOutputStream baous = catOperation(repoUrl, path, rev);
				System.out.println(baous.toString());
				System.out.println("=====================");
				try (OutputStream outputStream = new FileOutputStream("D:\\E3\\" + fname)) {
					baous.writeTo(outputStream);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Previous Rev: " + lastFr.getRevision());
				baous = catOperation(repoUrl, path, lastFr.getRevision());
				System.out.println(baous.toString());
			}
		} while (i != 0);
		System.out.println("=====================");*/
	}

	public void a() throws SVNException {/*
		int i = 366216;
		int j = 394728;

		final String path = "/incubator/pig/trunk/src/org/apache/pig/PigServer.java";
		// "/incubator/tuscany/contrib/java/sandbox/sdo/impl/src/main/java/org/apache/tuscany/sdo/helper/TypeHelperImpl.java";
		// ep.getPath();
		System.out.println(path);
		System.out.println("=====================");
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		final LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		try {
			repository.getFileRevisions(path, fileRevisions, i, i);
			// entry.getRevision());
		} catch (SVNException e) {
			if (e.getMessage().contains("is not a file in revision")) {
				// continue;
			} else {
				throw e;
			}
		}
		convertFileRevisions(fileRevisions, szzFileRevisions);
		ByteArrayOutputStream baous = catOperation(repoUrl, path, i);
		System.out.println(baous.toString());

		for (final SzzFileRevision fr : szzFileRevisions) {
			// final ByteArrayOutputStream frContent = catOperation(repoUrl,
			// fr);
			final SzzFileRevision nextFr = szzFileRevisions.get(szzFileRevisions.indexOf(fr) + 1);
			final ByteArrayOutputStream diff = diffOperation(repoUrl, fr, nextFr);
			System.out.println("=====================");
			System.out.println(diff.toString());
			final ByteArrayOutputStream nextFrContent = catOperation(repoUrl, nextFr);
			// }
		}*/

	}

	private String getSvnFileName(String path) {
		if (path == null)
			return null;
		String[] tokens = path.split("/");
		if (tokens.length == 0)
			return null;
		String lastPart = tokens[tokens.length - 1];
		return lastPart;
	}

	private boolean buildAnnotationGraph() throws Exception {/*
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA",
				"Pig", "Tuscany" };
		for (int j = 0; j < projects.length; j++) {
			List<DiffLine> diffLines = new ArrayList<DiffLine>();
			List<Long> linkedRevs = null;
			synchronized (queryDao) {
				linkedRevs = queryDao.getLinkedRevisions(projects[j]);
			}
			log.info("Project " + projects[j] + " starting...");
			log.info(linkedRevs.size() + " Linked revisions found...");
			int count = 0;
			for (long rev : linkedRevs) {
				List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
				try {
					repository.log(new String[] { "" }, logEntries, rev, rev, true, false);
				} catch (SVNException svne) {
					svne.printStackTrace();
					continue;
				}

				if (logEntries.size() == -1)
					continue;
				SVNLogEntry fixCommitLog = logEntries.get(0);
				logEntries.clear();

				for (SVNLogEntryPath ep : fixCommitLog.getChangedPaths().values()) {
					final String path = ep.getPath();
					if (ep.getType() == SVNLogEntryPath.TYPE_ADDED)
						continue;
					if (ep.getKind() != SVNNodeKind.FILE)
						continue;
					String fname = FileOperationsUtil.getFileName(path);
					if (fname == null || !fname.contains(".java"))
						continue;

					final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
					try {
						repository.getFileRevisions(path, fileRevisions, fixCommitLog.getRevision() - 1,
								fixCommitLog.getRevision());
					} catch (SVNException e) {
						if (e.getMessage().contains("is not a file in revision"))
							continue;
						else
							throw e;
					}
					if (fileRevisions.size() == 1)
						continue;

					final LinkedList<SzzFileRevision> szzFileRevs = new LinkedList<SzzFileRevision>();
					convertFileRevisions(fileRevisions, szzFileRevs);
					ByteArrayOutputStream baous = catOperation(repoUrl, ep.getPath(), fixCommitLog.getRevision());

					if (runRegex(path, "Test.java$") || isTestFile(baous))
						continue;

					Collections.sort(szzFileRevs, new Comparator<SzzFileRevision>() {
						@Override
						public int compare(SzzFileRevision o1, SzzFileRevision o2) {
							if (o1.getRevision() > o2.getRevision())
								return 1;
							else if (o1.getRevision() < o2.getRevision())
								return -1;
							return 0;
						}
					});

					final SzzFileRevision fixSzzFileRev = szzFileRevs.getLast();
					final SzzFileRevision beforeSzzFileRev = szzFileRevs.get(szzFileRevs.indexOf(fixSzzFileRev) - 1);
					final ByteArrayOutputStream diff = diffOperation(repoUrl, beforeSzzFileRev, fixSzzFileRev);
					List<String> headers = getHeaders(diff);
					List<DiffHunk> hunks = getDiffHunks(diff, headers, beforeSzzFileRev.getPath(),
							fixSzzFileRev.getPath(), beforeSzzFileRev.getRevision(), fixSzzFileRev.getRevision());
					for (DiffHunk hunk : hunks) {
						for (Line line : hunk.getContent()) {
							if (line.getType() == LineType.DELETION) {
								if (!isCommentOrBlankLine(line.getContent()) && !isImport(line.getContent())) {
									DiffLine diffline = new DiffLine();
									diffline.setFixRevision(fixSzzFileRev.getRevision());
									diffline.setRevision(beforeSzzFileRev.getRevision());
									diffline.setNumberline(line.getPreviousNumber());
									diffline.setContent(line.getContent());
									diffline.setProject(projects[j]);
									diffline.setPath(beforeSzzFileRev.getPath());
									diffLines.add(diffline);
									// System.out.println("[" +
									// fixSzzFileRev.getRevision() + "]" + path
									// + " (" + line.getPreviousNumber() + ") "
									// + line.getContent());
								}
							}
						}
					}
				}
				log.info("   Finished " + ++count + "/" + linkedRevs.size() + " (" + diffLines.size() + ")");
			}
			synchronized (queryDao) {
				Transaction tx = queryDao.beginTransaction();
				for (DiffLine diffline : diffLines) {
					queryDao.insertDiffLine(diffline);
				}
				tx.commit();
				log.info("[" + projects[j] + "] DATA SAVED with SUCESS!!");
				diffLines.clear();
			}
		}*/
		return true;
	}

	private void countNumberModifiedLines() throws Exception {/*
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA",
				"Pig", "Tuscany" };
		for (int j = 0; j < projects.length; j++) {
			List<Long> linkedRevs = null;
			synchronized (queryDao) {
				linkedRevs = queryDao.getAllRevisionByProject(projects[j]);
			}
			log.info("Project " + projects[j] + " starting...");
			log.info(linkedRevs.size() + " Linked revisions found...");
			int count = 0;
			synchronized (queryDao) {
				Transaction tx = queryDao.beginTransaction();
				for (long rev : linkedRevs) {
					Long qtdFilesAdd = (long) 0;
					Long qtdLinesAdd = (long) 0;
					Long qtdFilesDel = (long) 0;
					Long qtdLinesDel = (long) 0;
					Long qtdFilesMod = (long) 0;
					Long qtdLinesCon = (long) 0;
					Long qtdFilesRep = (long) 0;
					List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
					try {
						repository.log(new String[] { "" }, logEntries, rev, rev, true, false);
					} catch (SVNException svne) {
						svne.printStackTrace();
						continue;
					}

					if (logEntries.size() == -1)
						continue;
					SVNLogEntry fixCommitLog = logEntries.get(0);
					logEntries.clear();

					for (SVNLogEntryPath ep : fixCommitLog.getChangedPaths().values()) {
						final String path = ep.getPath();
						if (ep.getKind() != SVNNodeKind.FILE)
							continue;
						String fname = FileOperationsUtil.getFileName(path);
						if (fname == null || !fname.contains(".java"))
							continue;
						if (ep.getType() == SVNLogEntryPath.TYPE_ADDED)
							qtdFilesAdd++;
						else if (ep.getType() == SVNLogEntryPath.TYPE_DELETED)
							qtdFilesDel++;
						else if (ep.getType() == SVNLogEntryPath.TYPE_MODIFIED)
							qtdFilesMod++;
						else if (ep.getType() == SVNLogEntryPath.TYPE_REPLACED)
							qtdFilesRep++;

						final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
						try {
							repository.getFileRevisions(path, fileRevisions, fixCommitLog.getRevision() - 1,
									fixCommitLog.getRevision());
						} catch (SVNException e) {
							if (e.getMessage().contains("is not a file in revision"))
								continue;
							else
								throw e;
						}
						if (fileRevisions.size() == 1)
							continue;

						final LinkedList<SzzFileRevision> szzFileRevs = new LinkedList<SzzFileRevision>();
						convertFileRevisions(fileRevisions, szzFileRevs);
						ByteArrayOutputStream baous = catOperation(repoUrl, ep.getPath(), fixCommitLog.getRevision());

						if (runRegex(path, "Test.java$") || isTestFile(baous))
							continue;

						Collections.sort(szzFileRevs, new Comparator<SzzFileRevision>() {
							@Override
							public int compare(SzzFileRevision o1, SzzFileRevision o2) {
								if (o1.getRevision() > o2.getRevision())
									return 1;
								else if (o1.getRevision() < o2.getRevision())
									return -1;
								return 0;
							}
						});

						final SzzFileRevision fixSzzFileRev = szzFileRevs.getLast();
						final SzzFileRevision beforeSzzFileRev = szzFileRevs
								.get(szzFileRevs.indexOf(fixSzzFileRev) - 1);
						final ByteArrayOutputStream diff = diffOperation(repoUrl, beforeSzzFileRev, fixSzzFileRev);
						List<String> headers = getHeaders(diff);
						List<DiffHunk> hunks = getDiffHunks(diff, headers, beforeSzzFileRev.getPath(),
								fixSzzFileRev.getPath(), beforeSzzFileRev.getRevision(), fixSzzFileRev.getRevision());
						for (DiffHunk hunk : hunks) {
							for (Line line : hunk.getContent()) {
								if (!isCommentOrBlankLine(line.getContent()) && !isImport(line.getContent())) {
									if (line.getType() == LineType.DELETION)
										qtdLinesDel++;
									else if (line.getType() == LineType.ADDITION)
										qtdLinesAdd++;
									else if (line.getType() == LineType.CONTEXT)
										qtdLinesCon++;
								}
							}
						}
					}
					log.info("   Finished " + ++count + "/" + linkedRevs.size() + " (" + rev + ")");
					// log.info(" Commit: " + rev + "(" + qtdFilesAdd + " | " +
					// qtdLinesAdd + " | " + qtdFilesDel + " | "
					// + qtdLinesDel + " | " + qtdFilesMod + " | " + qtdLinesCon
					// + " | " + qtdFilesRep + ")");
					queryDao.insertCommitIndex(rev, qtdFilesAdd, qtdLinesAdd, qtdFilesDel, qtdLinesDel, qtdFilesMod,
							qtdLinesCon, qtdFilesRep, projects[j]);
				}
				tx.commit();
				log.info("[" + projects[j] + "] DATA SAVED with SUCESS!!");
			}
		}*/
	}
}
