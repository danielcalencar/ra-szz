package br.ufrn.raszz.persistence;

import java.util.List;

import br.ufrn.raszz.model.DiffLine;

public abstract class UtilQueryDAO extends AbstractDAO {
	
    public abstract List<Object[]> getBugcodesByProject(String project);
    
    public abstract List<Object[]> getBICwithRefacByProject(String project);
    
    public abstract List<Object[]> getBICwithRefacByProject2(String project);
    
    public abstract List<Long> getSampledRevisionWithBugsWithBICIdentifiedByProject(String project);
    
    public abstract void updateSample(long linenumber, String path, String content, long bicRev, long fixRev, String project); 
    
    public abstract void updateSample2(String issuecode);
    
    public abstract void updateSample3(long linenumber, String path, String content, long bicRev, long fixRev, String project); 

    public abstract void updateSampleBic2(long linenumber, String path, String content, long bicRev, long fixRev, String project); 
    
    public abstract void updateContentDiff(long bicRev, long linenumber, String path, String project, String contentDiff);
		
    public abstract List<Object[]> getBicWithContentDiffNullByProject(String project);
	
    public abstract List<Object[]> getRealeasesByProject(String project);
    
    public abstract List<String> getLinkedBugsWithBICIdentifiedByProject(String project);

    public abstract void insertDiffLine(DiffLine diffline);

	public abstract List<Long> getAllRevisionByProject(String project);

	public abstract void insertCommitIndex(Long revision, Long qtdFilesAdd , Long qtdLinesAdd,
			Long qtdFilesDel, Long qtdLinesDel, Long qtdFilesMod, Long qtdLinesCon, Long qtdFilesRep, String project);
    
	public abstract List<Long> getLinkedRevisions(String project);

}
