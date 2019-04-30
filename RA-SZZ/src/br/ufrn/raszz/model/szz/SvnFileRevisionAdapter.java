package br.ufrn.raszz.model.szz;

import java.util.Date;

import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import br.ufrn.raszz.util.FileOperationsUtil;

public class SvnFileRevisionAdapter extends SzzFileRevision {
	
	private SVNFileRevision filerev;
	
	public SvnFileRevisionAdapter(SVNFileRevision filerev){
		this.filerev = filerev;
		this.first = false;
	}
		
	@Override
	public String getPath(){
		return filerev.getPath();
	}

	@Override
	public String getRevision(){
		return filerev.getRevision() + "";
	}

	@Override
	public Date getCreateDate() {		
		String toparse = filerev.getRevisionProperties().getStringValue(SVNRevisionProperty.DATE);
		Date creation = FileOperationsUtil.getRevisionDate(toparse);
		return creation;
	}

}
