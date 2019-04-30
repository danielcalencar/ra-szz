import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.tmatesoft.svn.core.io.SVNRepository;

import core.connector.service.GitService;
import core.connector.service.SvnService;
import core.connector.service.impl.GitServiceImpl;
import core.connector.service.impl.SvnServiceImpl;
import refdiff.core.RefDiff;
import refdiff.core.RefDiffSVN;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

public class Main {

	public static void main(String[] args) {
		String repo = "git";
		if (repo.equals("git")) {
			
			RefDiff refDiff = new RefDiff();
			GitService gitService = new GitServiceImpl();
			//try (Repository repository = gitService.cloneIfNotExists("C:/tmp/clojure2", "C:/tmp/clojure2.git")) {
			//try (Repository repository = gitService.cloneIfNotExists("C:/tmp/gitfiles/commons-lang", "https://github.com/apache/commons-lang.git")){ //"C:/tmp/gitfiles/commons-lang.git")) {
			try (Repository repository = gitService.cloneIfNotExists("C:/tmp/gitfiles/time", "C:/tmp/gitfiles/time.git"))	{
				//List<SDRefactoring> refactorings = refDiff.detectAtCommit(repository, "ca7dfa33863744cd30f1e32f3e50012de8656f41");//17217a1");
				//List<SDRefactoring> refactorings = refDiff.detectAtCommit(repository, "09d3902");//687b2e6");
				List<SDRefactoring> refactorings = refDiff.detectAtCommit(repository, "9a62b06be5d0df8e833ff8583398cca386608cac");
				for (SDRefactoring refactoring : refactorings) {
					System.out.println(
							refactoring.toString() + " " + refactoring.getEntityAfter().sourceCode().toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (repo.equals("svn")) {
			
			RefDiffSVN refDiffSVN = new RefDiffSVN();
			SvnService svnService = new SvnServiceImpl();
			try {
				SVNRepository repository = svnService.openRepository("https://LG-ED/svn/bckrepo/", "syncuser", "");
				List<SDRefactoring> refactorings = refDiffSVN.detectAtCommit(repository, /*"384220"*/"747672", "C:\\tmp\\svnfiles\\");
				for (SDRefactoring refactoring : refactorings) {
					System.out.println(refactoring.toString());// + " " + refactoring.getEntityAfter().sourceCode().toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		System.out.println("Ended in " + new Date());
	}

}
