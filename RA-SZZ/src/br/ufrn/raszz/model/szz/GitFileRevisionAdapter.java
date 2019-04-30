package br.ufrn.raszz.model.szz;

import java.util.Date;

import org.eclipse.jgit.lib.PersonIdent;

import core.connector.model.GitFileRevision;

public class GitFileRevisionAdapter extends SzzFileRevision {
	
	private GitFileRevision filerev;
	
	public GitFileRevisionAdapter(GitFileRevision filerev){
		this.filerev = filerev;
		this.first = false;
	}
		
	@Override
	public String getPath(){
		return filerev.getPath();
	}

	@Override
	public String getRevision(){
		return filerev.getCommit().getName();
	}

	@Override
	public Date getCreateDate() {	
		/*PersonIdent authorIdent = filerev.getCommit().getAuthorIdent();
		Date authorDate = authorIdent.getWhen();
		TimeZone authorTimeZone = authorIdent.getTimeZone();*/
		PersonIdent committerIdent = filerev.getCommit().getCommitterIdent();
		Date commitDate = committerIdent.getWhen();
		return commitDate;
	}

}
