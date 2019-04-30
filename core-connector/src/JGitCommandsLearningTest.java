/***************************************************************************************************
 * Copyright (c) 2015 Rüdiger Herrmann
 * All rights reserved. This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 **************************************************************************************************/

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;
import org.eclipse.jgit.transport.TrackingRefUpdate;


/**
 * All test within this class share the same setup. A remote repository is initialized (see 
 * {@code iniRemote]) that is referenced as {@code remote}. Usually the remote repository would
 * be hosted on a remote computer but for the sake of simplicity and in oder to be able to run
 * the tests locally, the remote repository is stored locally.
 * 
 * Thereof a local clone is obtained (see @code cloneRepository}). This is the repository
 * to which files are added and removed, commis are being made, the log is listed for, etc.
 */
public class JGitCommandsLearningTest {

  private static final String MASTER = "refs/heads/master";
  private static final String ORIGIN_MASTER = "refs/remotes/origin/master";

  //@Rule
  //public final TemporaryFolder tempFolder = new TemporaryFolder();

  private Git remote;
  private Git local;
  
  //@Before
  public void setUp() throws GitAPIException, IOException {
    remote = initRepository();
    local = cloneRepository();
  }
  
  public void tearDown() {
    remote.close();
    local.close();
  }
  
  //@Test
  public void testAdd() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    
    DirCache index = local.add().addFilepattern( file.getName() ).call();
    
    //assertEquals( 1, index.getEntryCount() );
    //assertEquals( file.getName(), index.getEntry( 0 ).getPathString() );
  }
  
  //@Test
  public void testStatusBeforeAdd() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );

    org.eclipse.jgit.api.Status status = local.status().call();
    
    //assertEquals( singleton( file.getName() ), status.getUntracked() );
  }
  
  //@Test
  public void testStatusAfterAdd() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    local.add().addFilepattern( file.getName() ).call();
    
    org.eclipse.jgit.api.Status status = local.status().call();
    
    //assertEquals( singleton( file.getName() ), status.getAdded() );
    //assertTrue( status.getUntracked().isEmpty() );
  }
  
  //@Test
  public void testStatusWithPath() throws Exception {
    File file = createFile( "readme.txt" );
    createFile( "unrelated.txt" );
    
    org.eclipse.jgit.api.Status status = local.status().addPath( "readme.txt" ).call();
    
    //assertEquals( singleton( file.getName() ), status.getUntracked() );
  }
  
  //@Test
  public void testStatusWithNonExistingPath() throws Exception {
    createFile( "readme.txt" );

    org.eclipse.jgit.api.Status status = local.status().addPath( "does-not-exist" ).call();

    //assertTrue( status.isClean() );
  }
  
  //@Test
  public void testAddNonExistingFile() throws GitAPIException {
    DirCache index = local.add().addFilepattern( "foo" ).call();
    
    //assertEquals( 0, index.getEntryCount() );
  }
  
  //@Test
  public void testAddPattern() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    
    DirCache index = local.add().addFilepattern( "." ).call();
    
    //assertEquals( 1, index.getEntryCount() );
    //assertEquals( file.getName(), index.getEntry( 0 ).getPathString() );
  }
  
  //@Test
  public void testCommit() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    local.add().addFilepattern( file.getName() ).call();
    
    String message = "create file";
    RevCommit commit = local.commit().setMessage( message ).call();
    
    //assertEquals( message, commit.getFullMessage() );
  }
  
  //@Test(expected=NoMessageException.class)
  public void CommitWithoutMessage() throws Exception {
    local.commit().call();
  }
  
  //@Test
  public void testCommitWithEmptyMessage() throws GitAPIException {
    RevCommit commit = local.commit().setMessage( "" ).call();
    
    //assertEquals( "", commit.getFullMessage() );
  }
  
  //@Test
  public void testRm() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    local.add().addFilepattern( file.getName() ).call();

    DirCache index = local.rm().addFilepattern( file.getName() ).call();
    
    //assertEquals( 0, index.getEntryCount() );
    //assertFalse( file.exists() );
  }
  
  //@Test
  public void testRmCached() throws IOException, GitAPIException {
    File file = createFile( "readme.txt" );
    local.add().addFilepattern( file.getName() ).call();
    
    DirCache index = local.rm().setCached( true ).addFilepattern( file.getName() ).call();
    
    //assertEquals( 0, index.getEntryCount() );
    //assertTrue( file.exists() );
  }
  
  //@Test
  public void testRmNonExistingFile() throws GitAPIException {
    DirCache index = local.rm().addFilepattern( "foo" ).call();
    
    //assertEquals( 0, index.getEntryCount() );
  }
  
  //@Test
  public void testLog() throws GitAPIException {
    RevCommit commit = local.commit().setMessage( "empty commit" ).call();
    
    Iterable<RevCommit> iterable = local.log().call();
    
    List<RevCommit> commits = stream( iterable.spliterator(), false ).collect( toList() );
    //assertEquals( 1, commits.size() );
    //assertEquals( commit, commits.get( 0 ) );
  }
  
  //@Test
  public void testRevWalk() throws IOException, GitAPIException {
    RevCommit initialCommit = local.commit().setMessage( "init commit" ).call();
    Ref branch = local.branchCreate().setName( "side" ).call();
    local.checkout().setName( branch.getName() ).call();
    RevCommit branchCommit = local.commit().setMessage( "commit on side branch" ).call();
    local.checkout().setName( MASTER ).call();

    List<RevCommit> commits;
    try( RevWalk revWalk = new RevWalk( local.getRepository() ) ) {
      ObjectId commitId = local.getRepository().resolve( branch.getName() );
      revWalk.markStart( revWalk.parseCommit( commitId ) );
      commits = stream( revWalk.spliterator(), false ).collect( toList() );
    }
    
    //assertEquals( 2, commits.size() );
    //assertEquals( branchCommit, commits.get( 0 ) );
    //assertEquals( initialCommit, commits.get( 1 ) );
  }
  
  //@Test
  public void testPush() throws IOException, GitAPIException {
    RevCommit commit = local.commit().setMessage( "local commit" ).call();
    
    Iterable<PushResult> iterable = local.push().call();
    
    RemoteRefUpdate remoteUpdate = iterable.iterator().next().getRemoteUpdate( MASTER );
    //assertEquals( Status.OK, remoteUpdate.getStatus() );
    //assertEquals( commit, remoteUpdate.getNewObjectId() );
    //assertEquals( commit.getId(), remote.getRepository().resolve( MASTER ) );
  }
  
  //@Test
  public void testFetch() throws IOException, GitAPIException {
    RevCommit commit = remote.commit().setMessage( "remote commit" ).call();
    
    FetchResult fetchResult = local.fetch().call();
    
    TrackingRefUpdate refUpdate = fetchResult.getTrackingRefUpdate( ORIGIN_MASTER );
    //assertEquals( RefUpdate.Result.NEW, refUpdate.getResult() );
    //assertEquals( commit.getId(), local.getRepository().resolve( ORIGIN_MASTER ) );
  }

  private Git initRepository() throws GitAPIException, IOException {
    //return Git.init().setDirectory( tempFolder.newFolder( "remote" ) ).call();
	  return null;
  }

  private Git cloneRepository() throws GitAPIException, IOException {
    String remoteUri = remote.getRepository().getDirectory().getCanonicalPath();
    //File localDir = tempFolder.newFolder( "local" );
    //return Git.cloneRepository().setURI( remoteUri ).setDirectory( localDir ).call();
    return null;
  }

  private File createFile( String name ) throws IOException {
    File file = new File( local.getRepository().getWorkTree(), name );
    file.createNewFile();
    return file;
  }

}