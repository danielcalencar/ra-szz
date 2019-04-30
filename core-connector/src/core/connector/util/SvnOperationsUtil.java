package core.connector.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnAnnotate;
import org.tmatesoft.svn.core.wc2.SvnAnnotateItem;
import org.tmatesoft.svn.core.wc2.SvnCat;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public abstract class SvnOperationsUtil {

	public static ByteArrayOutputStream catOperation(String repoUrl, String path, long revision) throws Exception {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		final SvnOperationFactory fac = new SvnOperationFactory();
		//try {
			final SVNURL target = SVNURL.parseURIEncoded(repoUrl + path);
			final SVNRevision svnr = SVNRevision.create(revision);

			final SvnCat cat = fac.createCat();
			cat.setSingleTarget(SvnTarget.fromURL(target, svnr));
			cat.setOutput(output);
			cat.run();
		//} catch (Exception e) {
		//	e.printStackTrace();
		//} finally {
			fac.dispose();
		//}
		return output;
	}

	public static ByteArrayOutputStream diffOperation(String repoUrl, String currentPath, long currentRev, String nextPath, long nextRev) throws Exception {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final SvnOperationFactory fac = new SvnOperationFactory();
		boolean executed = false;
		while (!executed) {
			try {
				final SVNURL target1 = SVNURL.parseURIEncoded(repoUrl + currentPath);
				final SVNURL target2 = SVNURL.parseURIEncoded(repoUrl + nextPath);
				final SVNRevision svnr1 = SVNRevision.create(currentRev);
				final SVNRevision svnr2 = SVNRevision.create(nextRev);

				final SvnDiff diff = fac.createDiff();
				diff.setSources(SvnTarget.fromURL(target1, svnr1), SvnTarget.fromURL(target2, svnr2));
				diff.setOutput(output);
				diff.run();
				executed = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				fac.dispose();
			}
		}
		return output;
	}
	
	public static List<SvnAnnotateItem> annotateOperation(String repoUrl, String beforePath, long beforeRev) throws Exception {
		Collection<SvnAnnotateItem> annotations = new ArrayList<SvnAnnotateItem>();
		final SvnOperationFactory fac = new SvnOperationFactory();
		boolean executed = false;
		while(!executed){
			try{
				final SVNURL url = SVNURL.parseURIEncoded(repoUrl + beforePath);
				final SVNRevision svnr = SVNRevision.create(beforeRev);
				final SvnAnnotate annotate = fac.createAnnotate();
				annotate.setStartRevision(SVNRevision.create(0L));
				annotate.setEndRevision(svnr);
				annotate.addTarget(SvnTarget.fromURL(url,svnr));
				annotations = annotate.run(annotations);
				executed = true;
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				fac.dispose();
			}
		}
		return (List) annotations;
	}
	
}
