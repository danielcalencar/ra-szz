package br.ufrn.raszz.model.szz;
import java.util.*;
public class NodeDb {

	public NodeDb(String path, long revision, int linenumber, String content, String project,
			String id){
		this.path = path;
		this.revision = revision;
		this.linenumber = linenumber;
		this.content = content;
		this.project = project;
		this.id = id;
		evolutions = new ArrayList<String>();
		origins = new ArrayList<String>();
	}

	public String path;
	public long revision;
	public int linenumber;
	public String content;
	public String project;
	public String id;
	public List<String> evolutions;
	public List<String> origins;

        @Override
	public boolean equals(Object obj){
		NodeDb other = (NodeDb) obj;
		if(this.getId().equals(other.getId())){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}
	/**
	 * Get path.
	 *
	 * @return path as Sting.
	 */
	public String getPath()
	{
	    return path;
	}

	/**
	 * Set path.
	 *
	 * @param path the value to set.
	 */
	public void setPath(String path)
	{
	    this.path = path;
	}

	/**
	 * Get revision.
	 *
	 * @return revision as long.
	 */
	public long getRevision()
	{
	    return revision;
	}

	/**
	 * Set revision.
	 *
	 * @param revision the value to set.
	 */
	public void setRevision(long revision)
	{
	    this.revision = revision;
	}

	/**
	 * Get linenumber.
	 *
	 * @return linenumber as long.
	 */
	public int getLinenumber()
	{
	    return linenumber;
	}

	/**
	 * Set linenumber.
	 *
	 * @param linenumber the value to set.
	 */
	public void setLinenumber(int linenumber)
	{
	    this.linenumber = linenumber;
	}

	/**
	 * Get content.
	 *
	 * @return content as String.
	 */
	public String getContent()
	{
	    return content;
	}

	/**
	 * Set content.
	 *
	 * @param content the value to set.
	 */
	public void setContent(String content)
	{
	    this.content = content;
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
	 * Get id.
	 *
	 * @return id as String.
	 */
	public String getId()
	{
	    return id;
	}

	/**
	 * Set id.
	 *
	 * @param id the value to set.
	 */
	public void setId(String id)
	{
	    this.id = id;
	}

	/**
	 * Get evolutions.
	 *
	 * @return evolutions as String.
	 */
	public List<String> getEvolutions()
	{
	    return evolutions;
	}

	/**
	 * Set evolutions.
	 *
	 * @param evolutions the value to set.
	 */
	public void setEvolutions(List<String> evolutions)
	{
	    this.evolutions = evolutions;
	}

	/**
	 * Get origins.
	 *
	 * @return origins as String.
	 */
	public List<String> getOrigins()
	{
	    return origins;
	}

	/**
	 * Set origins.
	 *
	 * @param origins the value to set.
	 */
	public void setOrigins(List<String> origins)
	{
	    this.origins = origins;
	}
}
