package core.connector.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import core.connector.model.GitFileRevision;
import core.connector.service.GitService;
import core.connector.service.impl.GitServiceImpl;

public abstract class GitOperationsUtil {

	private static final String systemFileSeparator = Matcher.quoteReplacement(File.separator);
	
	public static ByteArrayOutputStream catOperation(Repository repository, 
													 String path, String commitId) throws Exception {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		
        ObjectId lastCommitId = repository.resolve(commitId); 
 
        // a RevWalk allows to walk over commits based on some filtering that is defined 
        try (RevWalk revWalk = new RevWalk(repository)) { 
            RevCommit commit = revWalk.parseCommit(lastCommitId); 
            // and using commit's tree find the path 
            RevTree tree = commit.getTree(); 
            //System.out.println("Having tree: " + tree); 
     
            // now try to find a specific file 
            try (TreeWalk treeWalk = new TreeWalk(repository)) { 
                treeWalk.addTree(tree); 
                treeWalk.setRecursive(true); 
                treeWalk.setFilter(PathFilter.create(path)); 
                if (!treeWalk.next()) { 
                    throw new IllegalStateException("Did not find expected file 'README.md'"); 
                } 
         
                ObjectId objectId = treeWalk.getObjectId(0); 
                ObjectLoader loader = repository.open(objectId); 
         
                // and then one can the loader to read the file 
                loader.copyTo(output); 
            } 
     
            revWalk.dispose(); 
        } 
		return output;
		
		/*		
		File metadataFolder = repository.getDirectory();
		File projectFolder = metadataFolder.getParentFile();
		
		GitService gitService = new GitServiceImpl();
		gitService.checkout(repository, commitId);

		String filesArray = projectFolder + File.separator + path.replaceAll("/", systemFileSeparator);
		
		Path p = Paths.get(filesArray);
	    byte[] data = Files.readAllBytes(p);
		output.write(data);
		return output;
	    
	    /*
		File file = new File(repository.getWorkTree(), path);
		file.createNewFile();

	    AnyObjectId commit = repository.resolve(commitId);		
	    RevWalk walk = new RevWalk(repository);
		RevCommit revCommit =  walk.parseCommit(commit);
		ObjectId tree = revCommit.getTree();
	
		ObjectReader reader = repository.newObjectReader();
		CanonicalTreeParser treeIter = new CanonicalTreeParser();
		treeIter.reset(reader, tree);
		treeIter.findFile(path);
		*/
	}	

	public static ByteArrayOutputStream diffOperation(Repository repository, String currentPath, 
			String currentCommitId, String nextPath, String nextCommitId) throws GitAPIException, IOException {
		
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		
        // the diff works on TreeIterators, we prepare two for the two branches 
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, currentCommitId); 
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, nextCommitId); 
 
        // then the procelain diff-command returns a list of diff entries 
        try (Git git = new Git(repository)) { 
            /*List<DiffEntry> diffs = git.diff(). 
                    setOldTree(oldTreeParser). 
                    setNewTree(newTreeParser). 
                    setPathFilter(PathFilter.create("README.md")). 
                    call(); */
            
            List<DiffEntry> diffs = git.diff()
		            .setNewTree(newTreeParser)
		            .setOldTree(oldTreeParser)
		            //.setShowNameAndStatusOnly(true)
		            .setPathFilter(PathFilter.create(currentPath))
		            .call();
            
            
            for (DiffEntry entry : diffs) { 
                //System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId()); 
            	DiffFormatter formatter = new DiffFormatter(output);//System.out);
                formatter.setRepository(repository); 
                formatter.format(entry);
            } 
        } 
        return output;		
	}
	
    private static AbstractTreeIterator prepareTreeParser(final Repository repository, final String objectId) 
    		throws IOException, MissingObjectException, IncorrectObjectTypeException { 
    	// from the commit we can build the tree which allows us to construct the TreeParser 
		try (RevWalk walk = new RevWalk(repository)) { 
		    RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId)); 
		    RevTree tree = walk.parseTree(commit.getTree().getId());
		
		    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser(); 
		    try (ObjectReader oldReader = repository.newObjectReader()) { 
		        oldTreeParser.reset(oldReader, tree.getId()); 
		    } 		     
		    walk.dispose(); 
		
		    return oldTreeParser; 
		} 
    }
	
	
	public static LinkedList<GitFileRevision> getHistoryFileRevisions(Repository repository, String path, String commitId) throws NoHeadException, GitAPIException, IOException {
		Git git = new Git(repository);
		Iterable<RevCommit> logs;
        logs = git.log() //for all log.all()
        		.add(ObjectId.fromString(commitId))
                .addPath(path) 
                .call();         
        LinkedList<GitFileRevision> gitFileRevisions = new LinkedList<GitFileRevision>();
        //Iterator<RevCommit> itr = logs.iterator();
        
        List<RevCommit> list = JavaUtil.toList(logs);
        ReverseListIterator itr = new ReverseListIterator(list);
        
        while(itr.hasNext()){
        	RevCommit rev = (RevCommit) itr.next();
        	GitFileRevision gfr = new GitFileRevision(path, rev);
        	gitFileRevisions.add(gfr);
        }
        /*
        for (RevCommit rev : logs) { 
        	GitFileRevision gfr = new GitFileRevision(path, rev);
        	gitFileRevisions.add(gfr);
        } */
        return gitFileRevisions;
	}
		
	public static List<String> parseBlame(Repository repository, String commitId, String path) throws Exception {
		
		//BlameCommand
		/*
		//final WalkingContext context = new WalkingContext(rev);
		AnyObjectId commit = repository.resolve(commitId);
		if (commit != null) {
			try {
				Git g = new Git(repository);
				BlameResult br = g.blame().setFilePath(path)
						.setStartCommit(commit).setFollowFileRenames(true).call();
				RawText rt = br.getResultContents();
				List<String> list = new ArrayList<>();
				for (int i = 0, l = rt.size(); i < l; i++) {
					RevCommit rc = br.getSourceCommit(i);
					//BlameModel bm = new BlameModel(rc);
					//bm.setContent(rt.getString(i));
					list.add(rc.getName());
				}
				return list;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return null;*/
		
		// prepare a new test-repository
        BlameCommand blamer = new BlameCommand(repository);
        ObjectId commitID = repository.resolve(commitId);
        blamer.setStartCommit(commitID);
        blamer.setFilePath(path);
        BlameResult blame = blamer.call();

        // read the number of lines from the commit to not look at changes in the working copy
        int lines = countFiles(repository, commitID, path);
        for (int i = 0; i < lines; i++) {
            RevCommit commit = blame.getSourceCommit(i);
            System.out.println("Line: " + i + ": " + commit);
        }

        System.out.println("Displayed commits responsible for " + lines + " lines of " + path);
		return new ArrayList<String>(); 
	}
	
	
	private static int countFiles(Repository repository, ObjectId commitID, String name) throws IOException { 
        try (RevWalk revWalk = new RevWalk(repository)) { 
            RevCommit commit = revWalk.parseCommit(commitID); 
            RevTree tree = commit.getTree(); 
            System.out.println("Having tree: " + tree); 
     
            // now try to find a specific file 
            try (TreeWalk treeWalk = new TreeWalk(repository)) { 
                treeWalk.addTree(tree); 
                treeWalk.setRecursive(true); 
                treeWalk.setFilter(PathFilter.create(name)); 
                if (!treeWalk.next()) { 
                    throw new IllegalStateException("Did not find expected file 'README.md'"); 
                } 
         
                ObjectId objectId = treeWalk.getObjectId(0); 
                ObjectLoader loader = repository.open(objectId); 
         
                ByteArrayOutputStream stream = new ByteArrayOutputStream(); 
                // and then one can the loader to read the file 
                loader.copyTo(stream); 
                 
                revWalk.dispose(); 
                 
                return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray())).size(); 
            } 
        } 
    } 
	
}
