import java.io.ByteArrayOutputStream;
import java.util.List;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import core.connector.model.GitFileRevision;
import core.connector.service.GitService;
import core.connector.service.impl.GitServiceImpl;
import core.connector.util.GitOperationsUtil;

public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GitService gitService = new GitServiceImpl();
		try {
			
			//String repoUrl = "C:/tmp/clojure2.git";
			//String repoFolder = "C:/tmp/clojure2";
			String repoUrl = "https://github.com/wordpress-mobile/WordPress-Android.git";
			String repoFolder = "C:/tmp/wordpress";
			String commitId = "989fb7e"; //"d3b8518"; //"d570842"; //"17217a1"
			
			Repository repository = gitService.cloneIfNotExists(repoFolder, repoUrl);
			
			AnyObjectId head = repository.resolve(commitId);
			//System.out.println(head.getName());
			//System.out.println(head.ge);
			
			RevWalk walk = new RevWalk(repository);
			
			//walk.parseCommit("d5708425995e8c83157ad49007ec2f8f43d8eac8");
			//System.out.println(walk.parseCommit(head).getShortMessage());
			RevCommit commit = walk.parseCommit(head);
			
			System.out.println(commit.getId());
			System.out.println(commit.name());
			//RevTree tree = commit.getTree();
			//Git git = new Git(repository);
			
			List<String> files = gitService.getChangedFilePaths(repository, commitId);
			for (String file : files) {
				System.out.println(file);
				ByteArrayOutputStream output = GitOperationsUtil.catOperation(repository, file, commit.getName());
				System.out.println(output.toString());
				
				//List<String> prevCommits = GitOperationsUtil.parseBlame(repository, commitId, file);
				List<GitFileRevision> prevCommits = GitOperationsUtil.getHistoryFileRevisions(repository, file, commitId);
				int count = 0;
				for (GitFileRevision com : prevCommits) {
					System.out.println(++count + " - " + com.getPath() + " - " + com.getCommit().name());
				}
			}
			
			
			
			//git.diff().
			//tree.
			//for (RevCommit revCommit : tree) {
				
			//}
			//RevCommit commit = walk.parseCommit(objectIdOfCommit);			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
