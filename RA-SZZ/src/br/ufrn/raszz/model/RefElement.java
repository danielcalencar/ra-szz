package br.ufrn.raszz.model;

import java.util.ArrayList;
import java.util.List;

public class RefElement {

	private String revision;
	private String project;
	private String summary;
	private String refactoringtype;
	private String entitybefore;
	private String entityafter;
	private String elementtype;
	private long callers;
	private long afterstartline;
	private long afterendline;
	private String afterpathfile;
	private long beforestartline;
	private long beforeendline;
	private String beforepathfile;
	
	private long afterstartscope;
	private String aftersimpleName;
	private String aftercontent;
	private long afternestingLevel;
	
	private long beforestarscope;
	private String beforesimpleName;
	private String beforecontent;
	private long beforenestingLevel;
	
	private List<RefCaller> callerList;
	
	public RefElement(String revision, String project) {
		this.revision = revision;
		this.project = project;
		this.setCallerList(new ArrayList<>());
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
	public String getRefactoringtype() {
		return refactoringtype;
	}
	public void setRefactoringtype(String refactoringtype) {
		this.refactoringtype = refactoringtype;
	}
	public String getEntitybefore() {
		return entitybefore;
	}
	public void setEntitybefore(String entitybefore) {
		this.entitybefore = entitybefore;
	}
	public String getEntityafter() {
		return entityafter;
	}
	public void setEntityafter(String entityafter) {
		this.entityafter = entityafter;
	}
	public String getElementtype() {
		return elementtype;
	}
	public void setElementtype(String elementtype) {
		this.elementtype = elementtype;
	}
	public long getCallers() {
		return callers;
	}
	public void setCallers(long callers) {
		this.callers = callers;
	}
	public long getAfterstartline() {
		return afterstartline;
	}
	public void setAfterstartline(long afterstartline) {
		this.afterstartline = afterstartline;
	}
	public long getAfterendline() {
		return afterendline;
	}
	public void setAfterendline(long afterendline) {
		this.afterendline = afterendline;
	}
	public String getAfterpathfile() {
		return afterpathfile;
	}
	public void setAfterpathfile(String afterpathfile) {
		this.afterpathfile = afterpathfile;
	}
	public long getBeforestartline() {
		return beforestartline;
	}
	public void setBeforestartline(long beforestartline) {
		this.beforestartline = beforestartline;
	}
	public long getBeforeendline() {
		return beforeendline;
	}
	public void setBeforeendline(long beforeendline) {
		this.beforeendline = beforeendline;
	}
	public String getBeforepathfile() {
		return beforepathfile;
	}
	public void setBeforepathfile(String beforepathfile) {
		this.beforepathfile = beforepathfile;
	}
	public List<RefCaller> getCallerList() {
		return callerList;
	}
	public void setCallerList(List<RefCaller> callerList) {
		this.callerList = callerList;
	}

	public long getAfterstartscope() {
		return afterstartscope;
	}

	public void setAfterstartscope(long firststatementline) {
		this.afterstartscope = firststatementline;
	}

	public String getAftersimplename() {
		return aftersimpleName;
	}

	public void setAftersimpleName(String simpleName) {
		this.aftersimpleName = simpleName;
	}

	public String getContent() {
		return aftercontent;
	}

	public void setAftercontent(String content) {
		this.aftercontent = content;
	}

	public long getAfternestingLevel() {
		return afternestingLevel;
	}

	public void setAfternestingLevel(long nestingLevel) {
		this.afternestingLevel = nestingLevel;
	}

	public long getBeforestarscope() {
		return beforestarscope;
	}

	public void setBeforestarscope(long beforestarscope) {
		this.beforestarscope = beforestarscope;
	}

	public String getBeforesimpleName() {
		return beforesimpleName;
	}

	public void setBeforesimpleName(String beforesimpleName) {
		this.beforesimpleName = beforesimpleName;
	}

	public String getBeforecontent() {
		return beforecontent;
	}

	public void setBeforecontent(String beforecontent) {
		this.beforecontent = beforecontent;
	}

	public long getBeforenestingLevel() {
		return beforenestingLevel;
	}

	public void setBeforenestingLevel(long beforenestingLevel) {
		this.beforenestingLevel = beforenestingLevel;
	}
	
}
