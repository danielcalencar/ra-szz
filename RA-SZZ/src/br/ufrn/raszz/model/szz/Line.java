package br.ufrn.raszz.model.szz;

import java.util.ArrayList;
import java.util.List;

public class Line {
	//distance related to the modification point ( in the diffhunk header)
	private boolean foundInDiffHunks;
	private int previousNumber = -1;
	private int number = -1;	
	private int cachedNumber = -1;
	private String content;
	private LineType type;
	private List<Line> evolutions;
	private List<Line> origins;
	private String revision;
	private String previousRevision;
	private String previousPath;
	private String nextPath;
	boolean contentAdjusted = false;
	private int adjustmentIndex = 0;
	private int context_difference; //in case we think this line didn't die in the next revision
	//these are needed to find the positions of lines that had format changes only
	private int deletions;
	private int additions;

	public Line(){
		evolutions = new ArrayList<Line>();
		origins = new ArrayList<Line>();
	}

	@Override
	public boolean equals(Object obj){
		Line other = (Line) obj;
		boolean result = false;
		if(other.getId().equals(this.getId())){
			result = true;
		}
		return result;
	}


        private String getSvnFileName(String path){
		String[] tokens = path.split("/");
		String lastPart = tokens[tokens.length - 1];
		return lastPart;
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}
	
	public LineType getType() {
		return type;
	}
	public void setType(LineType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}	
	public int getPreviousNumber() {
		return previousNumber;
	}
	public void setPreviousNumber(int previousNumber) {
		this.previousNumber = previousNumber;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public List<Line> getEvolutions() {
		return evolutions;
	}
	public void setEvolutions(List<Line> evolutions) {
		this.evolutions = evolutions;
	}
	public List<Line> getOrigins() {
		return origins;
	}
	public void setOrigins(List<Line> origins) {
		this.origins = origins;
	}
	public String getRevision() {
		return revision;
	}
	
	public int getAdjustmentIndex(){
		return adjustmentIndex;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("###############################################");
		sb.append("\nprevious_number :" + previousNumber);
		sb.append("\nnumber :" + number);
		sb.append("\ncontent :" + content);
		sb.append("\nrevision :" + revision);
		sb.append("\nprevious_revision :" + previousRevision);
		sb.append("\n###############################################\n");
		return sb.toString();
	}
	
	public String getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(String previousRevision) {
		this.previousRevision = previousRevision;
	}

	/**
	 * Get id.
	 *
	 * @return id as String.
	 */
	public String getId()
	{
		return this.getSvnFileName(previousPath) + "#" + previousRevision + 
			"#" + previousNumber;
	}

	/**
	 * Get previousPath.
	 *
	 * @return previousPath as String.
	 */
	public String getPreviousPath()
	{
	    return previousPath;
	}

	/**
	 * Set previousPath.
	 *
	 * @param previousPath the value to set.
	 */
	public void setPreviousPath(String previousPath)
	{
	    this.previousPath = previousPath;
	}

	/**
	 * Get nextPath.
	 *
	 * @return nextPath as String.
	 */
	public String getNextPath()
	{
	    return nextPath;
	}

	/**
	 * Set nextPath.
	 *
	 * @param nextPath the value to set.
	 */
	public void setNextPath(String nextPath)
	{
	    this.nextPath = nextPath;
	}

	/**
	 * Get contentAdjusted.
	 *
	 * @return contentAdjusted as boolean.
	 */
	public boolean getContentAdjusted()
	{
	    return contentAdjusted;
	}

	/**
	 * Set contentAdjusted.
	 *
	 * @param contentAdjusted the value to set.
	 */
	public void setContentAdjusted(boolean contentAdjusted)
	{
	    this.contentAdjusted = contentAdjusted;
	    this.adjustmentIndex++;
	}

	/**
	 * Get context_difference.
	 *
	 * @return context_difference as int.
	 */
	public int getContext_difference()
	{
	    return context_difference;
	}

	/**
	 * Set context_difference.
	 *
	 * @param context_difference the value to set.
	 */
	public void setContext_difference(int context_difference)
	{
	    this.context_difference = context_difference;
	}

	/**
	 * Get deletions.
	 *
	 * @return deletions as int.
	 */
	public int getDeletions()
	{
	    return deletions;
	}

	/**
	 * Set deletions.
	 *
	 * @param deletions the value to set.
	 */
	public void setDeletions(int deletions)
	{
	    this.deletions = deletions;
	}

	/**
	 * Get additions.
	 *
	 * @return additions as int.
	 */
	public int getAdditions()
	{
	    return additions;
	}

	/**
	 * Set additions.
	 *
	 * @param additions the value to set.
	 */
	public void setAdditions(int additions)
	{
	    this.additions = additions;
	}

	public int getPosition(){
		return this.getAdditions() - this.getDeletions();
	}

	/**
	 * Get foundInDiffHunks.
	 *
	 * @return foundInDiffHunks as boolean.
	 */
	public boolean getFoundInDiffHunks()
	{
	    return foundInDiffHunks;
	}

	/**
	 * Set foundInDiffHunks.
	 *
	 * @param foundInDiffHunks the value to set.
	 */
	public void setFoundInDiffHunks(boolean foundInDiffHunks)
	{
	    this.foundInDiffHunks = foundInDiffHunks;
	}

	/**
	 * Get cachedNumber.
	 *
	 * @return cachedNumber as int.
	 */
	public int getCachedNumber()
	{
	    return cachedNumber;
	}

	/**
	 * Set cachedNumber.
	 *
	 * @param cachedNumber the value to set.
	 */
	public void setCachedNumber(int cachedNumber)
	{
	    this.cachedNumber = cachedNumber;
	}
}
