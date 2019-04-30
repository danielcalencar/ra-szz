package br.ufrn.raszz.model.szz;

import java.util.Date;

public class Issue {
	
	private String issuecode;
	private String issuetype;
	private String revisionnumber;
	private Date createddate;
	private Date commitdate;
	
	public String getIssuecode() {
		return issuecode;
	}
	public void setIssuecode(String issuecode) {
		this.issuecode = issuecode;
	}
	public String getIssuetype() {
		return issuetype;
	}
	public void setIssuetype(String issuetype) {
		this.issuetype = issuetype;
	}
	public String getRevisionnumber() {
		return revisionnumber;
	}
	public void setRevisionnumber(String revisionnumber) {
		this.revisionnumber = revisionnumber;
	}
	public Date getCreateddate() {
		return createddate;
	}
	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}
	public Date getCommitdate() {
		return commitdate;
	}
	public void setCommitdate(Date commitdate) {
		this.commitdate = commitdate;
	}
}
