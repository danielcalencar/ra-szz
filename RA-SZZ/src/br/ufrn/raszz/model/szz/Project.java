package br.ufrn.raszz.model.szz;

import java.util.List;
import java.util.ArrayList;

public class Project {
	
	String project;

	public Project(){
		revisions = new ArrayList<LinkedRevision>();
	}

	List<LinkedRevision> revisions;

	public List<LinkedRevision> getRevisions(){
		return revisions;
	}

	public void setProject(String project){
		this.project = project;
	}

	public String getProject(){
		return project;
	}
}
