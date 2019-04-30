package br.ufrn.raszz.model;

public class Refac {

	private long revision;
	private String refactoringtype;
	private String entitybefore;
	private String entityafter;
	private String elementtype;
	private long startline;
	private long endline;
	private String pathfile;
	private String beforepathfile;
	private long startscope;
	private int beforestartscope;
	private String simplename;
	private int nestinglevel;	
	
	public Refac() {
		super();
	}
	
	public Refac(long revision, String refactoringtype) {
		this.revision = revision;
		this.refactoringtype = refactoringtype;
	}
	
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
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
	public long getStartline() {
		return startline;
	}
	public void setStartline(long afterstartline) {
		this.startline = afterstartline;
	}
	public long getEndline() {
		return endline;
	}
	public void setEndline(long afterendline) {
		this.endline = afterendline;
	}
	public String getPathfile() {
		return pathfile;
	}
	public void setPathfile(String afterpathfile) {
		this.pathfile = afterpathfile;
	}
	public long getStartscope() {
		return startscope;
	}
	public void setStartscope(long afterstartscope) {
		this.startscope = afterstartscope;
	}
	public String getSimplename() {
		return simplename;
	}
	public void setSimplename(String aftersimplename) {
		this.simplename = aftersimplename;
	}

	public int getNestinglevel() {
		return nestinglevel;
	}

	public void setNestinglevel(int nestinglevel) {
		this.nestinglevel = nestinglevel;
	}

	public String getBeforepathfile() {
		return beforepathfile;
	}

	public void setBeforepathfile(String beforepathfile) {
		this.beforepathfile = beforepathfile;
	}

	public int getBeforestartscope() {
		return beforestartscope;
	}

	public void setBeforestartscope(int beforestartscope) {
		this.beforestartscope = beforestartscope;
	}

	
}
