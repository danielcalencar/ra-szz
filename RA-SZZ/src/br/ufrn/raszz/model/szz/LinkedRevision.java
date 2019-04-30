package br.ufrn.raszz.model.szz;

import java.util.Date;

public class LinkedRevision {
	private Date reportingDate;
	private long revision;
	
	public Date getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(Date reportingDate) {
		this.reportingDate = reportingDate;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}
}
