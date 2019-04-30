package br.ufrn.raszz.model.szz;

import java.util.*;

public class BugIntroducingCode {
	
	private int linenumber;
	private String path;
	private String content;
	private String revision;
	private String fixRevision;
	private String project;
	private Date szzDate;
	private String copypath;
	private long copyrevision;
	private boolean branchrev;
	private boolean mergerev;
	private boolean changeproperty;
	private boolean missed;
	private boolean furtherback;
	private String diffjmessage;
	private String diffjlocation;
	private int adjustmentindex;
	private int indexPosRefac;
	private int indexChangePath;
	private boolean isrefac;
	private int indexFurtherBack;
	
	private String startRevision;
	private String startPath;
	private int startlinenumber;
	private String startContent;

	public int getLinenumber() {
		return linenumber;
	}

	public void setLinenumber(int linenumber) {
		this.linenumber = linenumber;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getFixRevision() {
		return fixRevision;
	}
	
	public String getDiffjmessage() {
		return diffjmessage;
	}
	

	public String getDiffjlocation() {
		return diffjlocation;
	}
	
	/**
	 * Get project.
	 *
	 * @return project as String.
	 */
	public String getProject()
	{
	    return project;
	}

	public void setFixRevision(String fixRevision) {
		this.fixRevision = fixRevision;
	}

	/**
	 * Set project.
	 *
	 * @param project the value to set.
	 */
	public void setProject(String project)
	{
	    this.project = project;
	}

	/**
	 * Get szzDate.
	 *
	 * @return szzDate as Date.
	 */
	public Date getSzzDate()
	{
	    return szzDate;
	}

	/**
	 * Set szzDate.
	 *
	 * @param szzDate the value to set.
	 */
	public void setSzzDate(Date szzDate)
	{
	    this.szzDate = szzDate;
	}

	/**
	 * Get copypath.
	 *
	 * @return copypath as String.
	 */
	public String getCopypath()
	{
	    return copypath;
	}

	/**
	 * Set copypath.
	 *
	 * @param copypath the value to set.
	 */
	public void setCopypath(String copypath)
	{
	    this.copypath = copypath;
	}

	/**
	 * Get copyrevision.
	 *
	 * @return copyrevision as long.
	 */
	public long getCopyrevision()
	{
	    return copyrevision;
	}

	/**
	 * Set copyrevision.
	 *
	 * @param copyrevision the value to set.
	 */
	public void setCopyrevision(long copyrevision)
	{
	    this.copyrevision = copyrevision;
	}

	/**
	 * Get branchrev.
	 *
	 * @return branchrev as boolean.
	 */
	public boolean getBranchrev()
	{
	    return branchrev;
	}

	/**
	 * Set branchrev.
	 *
	 * @param branchrev the value to set.
	 */
	public void setBranchrev(boolean branchrev)
	{
	    this.branchrev = branchrev;
	}

	/**
	 * Get mergerev.
	 *
	 * @return mergerev as boolean.
	 */
	public boolean getMergerev()
	{
	    return mergerev;
	}

	/**
	 * Set mergerev.
	 *
	 * @param mergerev the value to set.
	 */
	public void setMergerev(boolean mergerev)
	{
	    this.mergerev = mergerev;
	}

	/**
	 * Get changeproperty.
	 *
	 * @return changeproperty as boolean.
	 */
	public boolean getChangeproperty()
	{
	    return changeproperty;
	}

	/**
	 * Set changeproperty.
	 *
	 * @param changeproperty the value to set.
	 */
	public void setChangeproperty(boolean changeproperty)
	{
	    this.changeproperty = changeproperty;
	}

	/**
	 * Get missed.
	 *
	 * @return missed as boolean.
	 */
	public boolean getMissed()
	{
	    return missed;
	}

	/**
	 * Set missed.
	 *
	 * @param missed the value to set.
	 */
	public void setMissed(boolean missed)
	{
	    this.missed = missed;
	}

	/**
	 * Get furtherback.
	 *
	 * @return furtherback as boolean.
	 */
	public boolean getFurtherback()
	{
	    return furtherback;
	}

	/**
	 * Set furtherback.
	 *
	 * @param furtherback the value to set.
	 */
	public void setFurtherback(boolean furtherback)
	{
	    this.furtherback = furtherback;
	}

	public void setDiffjmessage(String diffjmessage) {
		this.diffjmessage = diffjmessage;
	}

	public void setDiffjlocation(String diffjlocation) {
		this.diffjlocation = diffjlocation;
	}

	public int getAdjustmentIndex() {
		return adjustmentindex;
	}

	public void setAdjustmentIndex(int adjustementindex) {
		this.adjustmentindex = adjustementindex;
	}

	public int getIndexPosRefac() {
		return indexPosRefac;
	}

	public void setIndexPosRefac(int indexPosRefac) {
		this.indexPosRefac = indexPosRefac;
	}

	public int getIndexChangePath() {
		return indexChangePath;
	}

	public void setIndexChangePath(int indexChangePath) {
		this.indexChangePath = indexChangePath;
	}

	public boolean isIsrefac() {
		return isrefac;
	}

	public void setIsrefac(boolean isrefac) {
		this.isrefac = isrefac;
	}

	public void setIndexFurtherBack(int indexFurtherBack) {
		this.indexFurtherBack = indexFurtherBack;		
	}
	
	public int getIndexFurtherBack() {
		return indexFurtherBack;
	}

	public String getStartRevision() {
		return startRevision;
	}

	public void setStartRevision(String startRevision) {
		this.startRevision = startRevision;
	}

	public String getStartPath() {
		return startPath;
	}

	public void setStartPath(String startPath) {
		this.startPath = startPath;
	}

	public int getStartlinenumber() {
		return startlinenumber;
	}

	public void setStartlinenumber(int startlinenumber) {
		this.startlinenumber = startlinenumber;
	}

	public String getStartContent() {
		return startContent;
	}

	public void setStartContent(String startContent) {
		this.startContent = startContent;
	}
		
}
