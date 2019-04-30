package br.ufrn.raszz.miner.szz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.Repository;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.miner.Miner;
import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.SZZImplementationType;
import br.ufrn.raszz.persistence.DAOType;
import br.ufrn.raszz.persistence.FactoryDAO;
import br.ufrn.raszz.persistence.SzzDAO;
import br.ufrn.razszz.connectoradapter.GitRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SvnRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import core.connector.service.GitService;
import core.connector.service.SvnService;
import core.connector.service.impl.GitServiceImpl;
import core.connector.service.impl.SvnServiceImpl;

public class RaSZZ extends Miner {

	private static final Logger log = Logger.getLogger(RaSZZ.class);
	private SzzDAO szzDAO;
	private SzzRepository repository;

	public static void main(String[] args) throws Exception {
		RaSZZ szz = new RaSZZ();
		
		//String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase" }; 
		//String[] projects = { "Geronimo", "Hadoop Common", "HBase" };
		//String[] projects = { "Mahout", "OpenJPA", "Pig", "Tuscany" };
		
		//String[] projects = { "joda-time", "closure-compiler", "mockito" }; //git
		//String[] projects = { "jfreechart", "commons-lang", "commons-math"}; //svn

		String[] projects = { "jfreechart" };
		
		RepositoryType repoType = RepositoryType.SVN;
		SZZImplementationType szzType = SZZImplementationType.RASZZ;
		szz.init(projects, repoType, szzType, false, null);
	}	
	
	public void init(String[] projects, RepositoryType repoType, SZZImplementationType szzType, boolean isTest, String[] debugInfos) throws Exception {
		String user = this.getProperty("user","./backhoe.properties");
		String password = this.getProperty("password","./backhoe.properties");		
		String tmpfolder =  this.getProperty("tmpfolder","./backhoe.properties");
		String url = "";
		switch (repoType) {
		case GIT:
			tmpfolder += "gitfiles\\" + projects[0]; //TODO não dar rodar mais um assim
			url = tmpfolder + ".git";
			break;
		case SVN:
			tmpfolder += "svnfiles\\";
			url = this.getProperty("svn_url","./backhoe.properties");
			break;
		}	
		boolean entireDb = Boolean.valueOf(this.getProperty("entire_db", "./backhoe.properties"));
		String linkedRev = null, debugPath = null, debugContent = null;
		if (isTest) {
			linkedRev = debugInfos[0];
			debugPath = debugInfos[1]; 
			debugContent = debugInfos[2];
		} 
		
		Map<String, Object> p = new HashMap<String, Object>();
		try {
			p.put("user", user);
			p.put("password", password);
			p.put("tmpfolder", tmpfolder);
			p.put("repoType", repoType);
			p.put("szzType", szzType);
			p.put("repoUrl", url);
			p.put("projects", projects);
			p.put("entireDb", entireDb);
			p.put("isTest", isTest);			
			p.put("debugRev", linkedRev);
			p.put("debugPath", debugPath);
			p.put("debugContent", debugContent);
			this.setParameters(p);
			this.executeMining();
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
			String tmpfolder = (String) this.getParameters().get("tmpfolder");
			RepositoryType repoType = (RepositoryType) this.getParameters().get("repoType");
			String repoUrl = (String) this.getParameters().get("repoUrl");
			
			switch (repoType) {
			case GIT:
				GitService gitService = new GitServiceImpl();
				Repository gitRepository = gitService.cloneIfNotExists(tmpfolder, repoUrl); //TODO Pode melhorar!
				repository = new GitRepositoryAdapter(gitRepository, repoUrl, tmpfolder);
				break;
			case SVN:
				SvnService svnService = new SvnServiceImpl();
				SVNRepository svnRepository = svnService.openRepository(repoUrl, user, password);
				repository = new SvnRepositoryAdapter(svnRepository, user, password, repoUrl, tmpfolder);
				break;
			}			
			
			szzDAO = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getSzzDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void performMining() throws Exception {
		log.info("perform mining...");
		final boolean buildAnnotationGraph = Boolean.valueOf(getProperty("build_graph", "./backhoe.properties"));
		//final boolean findBugIntroducingChanges = Boolean.valueOf(getProperty("find_bug_code", "./backhoe.properties"));
		if (buildAnnotationGraph) 
			buildAnnotationGraph();
	}
	
	private void buildAnnotationGraph() throws Exception {
		try {
			String[] projects = (String[]) this.getParameters().get("projects");
			boolean entireDb = (Boolean) this.getParameters().get("entireDb");
			String repoUrl = (String) this.getParameters().get("repoUrl");
			RepositoryType repoType = (RepositoryType) this.getParameters().get("repoType");	
			
			for (int j = 0; j < projects.length; j++) {			
				List<String> linkedRevs = null;
				synchronized(szzDAO){
					//linkedRevs = new ArrayList<String>();
					//linkedRevs.add("9a62b06be5d0df8e833ff8583398cca386608cac");//687b2e62b7c6e81cd9d5c872b7fa9cc8fd3f1509");			
					switch (repoType) {
						case GIT:
							linkedRevs = szzDAO.getGitLinkedRevision(projects[j]);
							break;
						case SVN:
								linkedRevs = szzDAO.getGitLinkedRevision(projects[j]);
							/*linkedRevs = (entireDb)? szzDAO.getLinkedRevisions(projects[j]):
													 //szzDAO.getLinkedRevisionWAffectedVersions(project);
													 szzDAO.getLinkedRevisionsWProcessedBIC(projects[j]);*/
							break;									
					}						
				}	
				String debugPath = null;
				String debugContent = null;
				boolean isTest = (Boolean) this.getParameters().get("isTest");				
				if (isTest) {
					String linkedTestRev = (String) this.getParameters().get("debugRev");
					linkedRevs = linkedRevs.stream()
						.filter(r -> r.equals(linkedTestRev)).collect(Collectors.toList());
					debugPath = (String) this.getParameters().get("debugPath");
					debugContent = (String) this.getParameters().get("debugContent");
				}
				
				SZZImplementationType szzType = (SZZImplementationType) this.getParameters().get("szzType");		
				AnnotationGraphService worker = null;
				switch (szzType) {
					case RASZZ:
						worker = new AnnotationGraphServiceRaSZZ(repository, szzDAO, projects[j], 
								linkedRevs, repoUrl, debugPath, debugContent, szzType);
						break;
					case MASZZ:
						worker = new AnnotationGraphServiceMaSZZ(repository, szzDAO, projects[j], 
								linkedRevs, repoUrl, debugPath, debugContent, szzType);
						break;									
				}
				worker.run();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
