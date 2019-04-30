package br.ufrn.raszz.model;

public class DiffLine {
	
	private long fixRevision;
	private long revision;
	private String project;
	private String content;
	private long numberline;	
	private String path;
	
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public long getNumberline() {
		return numberline;
	}
	public void setNumberline(long numberline) {
		this.numberline = numberline;
	}
	public long getFixRevision() {
		return fixRevision;
	}
	public void setFixRevision(long fixRevision) {
		this.fixRevision = fixRevision;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}	
	
}
