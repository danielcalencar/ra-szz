package core.connector.model;

import org.eclipse.jgit.revwalk.RevCommit;

public class GitFileRevision {

	private String path;
	private RevCommit commit;
	
	public GitFileRevision(String path, RevCommit commit) {
		this.path = path;
		this.commit = commit;
	}
	public String getPath() {
		return path;
	}
	public RevCommit getCommit() {
		return commit;
	}
}
