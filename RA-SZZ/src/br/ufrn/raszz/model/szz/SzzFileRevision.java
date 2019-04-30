package br.ufrn.raszz.model.szz;

import java.util.Date;

import org.tmatesoft.svn.core.SVNRevisionProperty;

import br.ufrn.raszz.util.FileOperationsUtil;

public abstract class SzzFileRevision {
	
	protected boolean branchrev;
	protected boolean mergerev;
	protected boolean changeproperty;
	protected boolean first;

	/**
	 * Get branchrev.
	 *
	 * @return branchrev as boolean.
	 */
	public boolean getBranchrev() {
	    return branchrev;
	}

	/**
	 * Set branchrev.
	 *
	 * @param branchrev the value to set.
	 */
	public void setBranchrev(boolean branchrev) {
	    this.branchrev = branchrev;
	}

	/**
	 * Get mergerev.
	 *
	 * @return mergerev as boolean.
	 */
	public boolean getMergerev() {
	    return mergerev;
	}

	/**
	 * Set mergerev.
	 *
	 * @param mergerev the value to set.
	 */
	public void setMergerev(boolean mergerev) {
	    this.mergerev = mergerev;
	}

	/**
	 * wrapper method to return path
	 */
	public abstract String getPath();

	/**
	 * wrapper method to return revision
	 */
	public abstract String getRevision();

	/**
	 * wrapper method to return revisionProperties
	 */
	//public SVNProperties getRevisionProperties() {
	//	return filerev.getRevisionProperties();
	//}
	public abstract Date getCreateDate();

	/**
	 * Get changeproperty.
	 *
	 * @return changeproperty as boolean.
	 */
	public boolean getChangeproperty() {
	    return changeproperty;
	}

	/**
	 * Set changeproperty.
	 *
	 * @param changeproperty the value to set.
	 */
	public void setChangeproperty(boolean changeproperty) {
	    this.changeproperty = changeproperty;
	}

	/**
	 * Get first.
	 *
	 * @return first as boolean.
	 */
	public boolean getFirst() {
	    return first;
	}

	/**
	 * Set first.
	 *
	 * @param first the value to set.
	 */
	public void setFirst(boolean first) {
	    this.first = first;
	}
}
