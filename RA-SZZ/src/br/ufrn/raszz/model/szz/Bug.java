package br.ufrn.raszz.model.szz;
import java.util.*;

public class Bug {
	private String code;
	private int code2;
	private List<BugIntroducingCode> bicodes;
	private List<Bug> futureBugs;
	private String clazz;
	private Date openDate;

	public Bug(){
		bicodes = new ArrayList<BugIntroducingCode>();
		futureBugs = new ArrayList<Bug>();
	}
	/**
	 * Get code.
	 *
	 * @return code as String.
	 */
	public String getCode()
	{
	    return code;
	}

	/**
	 * Set code.
	 *
	 * @param code the value to set.
	 */
	public void setCode(String code)
	{
	    this.code = code;
	}

	/**
	 * Get bicodes.
	 *
	 * @return bicodes as String.
	 */
	public List<BugIntroducingCode> getBicodes()
	{
	    return bicodes;
	}

	/**
	 * Set bicodes.
	 *
	 * @param bicodes the value to set.
	 */
	public void setBicodes(List<BugIntroducingCode> bicodes)
	{
	    this.bicodes = bicodes;
	}

	/**
	 * Get clazz.
	 *
	 * @return clazz as String.
	 */
	public String getClazz()
	{
	    return clazz;
	}

	/**
	 * Set clazz.
	 *
	 * @param clazz the value to set.
	 */
	public void setClazz(String clazz)
	{
	    this.clazz = clazz;
	}

	/**
	 * Get futureBugs.
	 *
	 * @return futureBugs as List.
	 */
	public List<Bug> getFutureBugs()
	{
	    return futureBugs;
	}

	/**
	 * Set futureBugs.
	 *
	 * @param futureBugs the value to set.
	 */
	public void setFutureBugs(List<Bug> futureBugs)
	{
	    this.futureBugs = futureBugs;
	}

	/**
	 * Get code2.
	 *
	 * @return code2 as int.
	 */
	public int getCode2()
	{
	    return code2;
	}

	/**
	 * Set code2.
	 *
	 * @param code2 the value to set.
	 */
	public void setCode2(int code2)
	{
	    this.code2 = code2;
	}

	/**
	 * Get openDate.
	 *
	 * @return openDate as Date.
	 */
	public Date getOpenDate()
	{
	    return openDate;
	}

	/**
	 * Set openDate.
	 *
	 * @param openDate the value to set.
	 */
	public void setOpenDate(Date openDate)
	{
	    this.openDate = openDate;
	}
}
