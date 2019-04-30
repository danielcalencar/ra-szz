package br.ufrn.raszz.model;

public class Line {
	private int previousNumber = -1;
	private int number = -1;	
	private int cachedNumber = -1;
	private String content;
	boolean contentAdjusted = false;

	@Override
	public int hashCode(){
		return super.hashCode();
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
	
	public boolean isMultipleLine() {
		return (previousNumber-number==1)?false:true;
	}
	
	@Override
	public String toString() {
		return this.content;
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
	    this.previousNumber++;
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
