package br.ufrn.raszz.model;

public class RefCaller {
	
	private String revision;
	private String project;
	private String summary;
	private String refactoringtype;
	private String callermethod;
	private long callerstartline;
	private long callerendline;
	private String callerpath;
	private String entityafter;
	
	private long callerline;
	private String simplename;
	private long nestingLevel;
	
	private String type;
	
	public RefCaller(String revision, String project, String type) {
		this.revision = revision;
		this.project = project;
		this.type = type;
	}
	
	public String getRevision() {
		return revision;
	}
	public String getProject() {
		return project;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCallermethod() {
		return callermethod;
	}

	public void setCallermethod(String callermethod) {
		this.callermethod = callermethod;
	}

	public long getCallerstartline() {
		return callerstartline;
	}

	public void setCallerstartline(long callerstartline) {
		this.callerstartline = callerstartline;
	}

	public long getCallerendline() {
		return callerendline;
	}

	public void setCallerendline(long callerendline) {
		this.callerendline = callerendline;
	}

	public String getCallerpath() {
		return callerpath;
	}

	public void setCallerpath(String callerpath) {
		this.callerpath = callerpath;
	}

	public String getRefactoringtype() {
		return refactoringtype;
	}

	public void setRefactoringtype(String refactoringtype) {
		this.refactoringtype = refactoringtype;
	}

	public String getEntityafter() {
		return entityafter;
	}

	public void setEntityafter(String entityafter) {
		this.entityafter = entityafter;
	}

	public long getCallerline() {
		return callerline;
	}

	public void setCallerline(long callerline) {
		this.callerline = callerline;
	}

	public String getSimplename() {
		return simplename;
	}

	public void setSimplename(String simplename) {
		this.simplename = simplename;
	}

	public long getNestingLevel() {
		return nestingLevel;
	}

	public void setNestingLevel(long nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
		
}
