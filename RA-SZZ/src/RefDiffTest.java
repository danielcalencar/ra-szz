

import java.util.Date;
import java.util.List;

import br.ufrn.raszz.miner.refdiff.RefDiffService;
import br.ufrn.raszz.persistence.DAOType;
import br.ufrn.raszz.persistence.FactoryDAO;
import br.ufrn.razszz.connectoradapter.SvnRepositoryAdapter;
import br.ufrn.razszz.connectoradapter.SzzRepository;
import core.connector.model.enums.ConnectorType;

public class RefDiffTest {
	
	
	public static void main(String[] args) {

		RefDiffService refDiffService = new RefDiffService(null);

		
		//svnDAO = (FactoryDAO.getFactoryDAO(DAOType.HIBERNATE)).getSvnDAO();
		//ConnectorType connectorType = ConnectorType.SVN;
		//String repoUrl = "https://LG-ED/svn/bckrepo/"; 
		//ConnectorType connectorType = ConnectorType.GIT;
		String repoUrl = "C:/tmp/clojure2.git";
		String user = "syncuser";
		String password = "";
		String[] projects = { "ActiveMQ", "Camel", "Derby", "Geronimo", "Hadoop Common", "HBase", "Mahout", "OpenJPA", "Pig", "Tuscany" };
		//String[] projects = { "HBase" };

		String folder = "C:\\tmp\\svnfiles\\";		
		//String[] projects = { "Hadoop Common", "HBase", "Mahout", "OpenJPA", "Pig", "Tuscany" };
		// Long commitId = (long) 682474; // 395597;// 384220;
		String revisionType = "new";

		//SzzRepository repository = new SvnRepositoryAdapter(encapsulation, user, password, repoUrl)
		/*switch (connectorType) {
		case GIT:
			executeGit();
			break;
		case SVN:
			for (int j = 0; j < projects.length; j++) {
				String project = projects[j];
				List<Object> commitIds = null;
				synchronized (svnDAO) {
					if (revisionType.equals("bic")) {
						commitIds = svnDAO.getSampledRevisionWithBugsWithBICIdentifiedByProject(project);
						//commitIds = svnDAO.getNotSampledRevisionWithBugsWithBICIdentifiedByProject(project);
					} else if (revisionType.equals("fix3")) {
						//commitIds = svnDAO.getFixRevisionWithBugsWithBICIdentifiedByProject(project);
						commitIds = svnDAO.getFixRevisionWithBugsWithoutBICIdentifiedByProject(project);
					} else if (revisionType.equals("new")) {
						commitIds = svnDAO.getAllRevisionWithBugsWithBICIdentifiedByProject(project);
					}		
				}
				for (int i = 0; i < commitIds.size(); i++) {
					//if (Long.parseLong(commitIds.get(i).toString()) < 720670) continue;
					//Long commitId = Long.parseLong(commitIds.get(i).toString());
					String commitId = commitIds.get(i).toString();
					System.out.println("=[" + project + "]= (" + (i + 1) + "/" + commitIds.size() + ") CommitId: " + commitId + " =======");
					executeSvn(repoUrl, user, password, project, commitId, folder, revisionType);
				}
			}
			//executeSvn(repoUrl, user, password, projects[5], (long) 747672, folder, revisionType);
			break;
		}*/
		System.out.println("\n" + "Ended in " + new Date());
	}

}
