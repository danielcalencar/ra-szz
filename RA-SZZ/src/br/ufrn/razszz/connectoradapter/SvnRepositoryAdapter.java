package br.ufrn.razszz.connectoradapter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.szz.SvnFileRevisionAdapter;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import core.connector.service.SvnService;
import core.connector.service.impl.SvnServiceImpl;

public class SvnRepositoryAdapter extends SzzRepositoryImpl implements SzzRepository {
	
	private SvnService svnService = new SvnServiceImpl();
	private SVNRepository encapsulation;
	
	public SvnRepositoryAdapter(SVNRepository encapsulation, String user, String password, String repoUrl, String repoTemp) {
		this.encapsulation = encapsulation;
		this.connectorType = RepositoryType.SVN;
		this.url = repoUrl;
		this.user = user;
		this.password = password;
		this.repositoryFolder = repoTemp;
	}	

	@Override
	public List<String> getChangedPaths(String revisionnumber) throws SVNException{			
		return svnService.getChangedFilePaths(encapsulation, Long.parseLong(revisionnumber));
	}	

	@Override
	public ByteArrayOutputStream catOperation(String repoUrl, String path, String revision) throws Exception {
		return svnService.catOperation(repoUrl, path, Long.parseLong(revision));
	}

	@Override
	public ByteArrayOutputStream catOperation(String repoUrl, SzzFileRevision szzFileRevision) throws Exception {
		return svnService.catOperation(repoUrl, szzFileRevision.getPath(), Long.parseLong(szzFileRevision.getRevision()));
	}
	
	@Override
	public ByteArrayOutputStream diffOperation(String repoUrl, SzzFileRevision currentSzzFileRevision, SzzFileRevision nextSzzFileRevision) throws Exception {
		return svnService.diffOperation(repoUrl, currentSzzFileRevision.getPath(), Long.parseLong(currentSzzFileRevision.getRevision()),
				nextSzzFileRevision.getPath(), Long.parseLong(nextSzzFileRevision.getRevision()));
	}
		
	@Override
	public LinkedList<SzzFileRevision> convertFileRevisions(LinkedList<?> fileRevisions) {
		LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		for(Object svnfr : fileRevisions){
			SzzFileRevision szzfr = new SvnFileRevisionAdapter((SVNFileRevision)svnfr);
			szzFileRevisions.add(szzfr);
		}
		return szzFileRevisions;
	}

	private final Comparator<SzzFileRevision> revisionComp = new Comparator<SzzFileRevision>() {
		@Override
		public int compare(SzzFileRevision o1, SzzFileRevision o2) {
			long revId1 = Long.parseLong(o1.getRevision());
			long revId2 = Long.parseLong(o2.getRevision());			
			if (revId1 > revId2) {
				return 1;
			} else if (revId1 < revId2) {
				return -1;
			}
			return 0;
		}
	};
	
	@Override
	protected LinkedList<SzzFileRevision> getSzzFileRevisions(String path, String commitId) throws Exception {
		final LinkedList<SVNFileRevision> fileRevisions = new LinkedList<SVNFileRevision>();
		try {
			encapsulation.getFileRevisions(path, fileRevisions, 0L, Long.parseLong(commitId));
		} catch (SVNException e) {
			if (e.getMessage().contains("is not a file in revision")) 
				return null;
			else throw e;
		}
		final LinkedList<SzzFileRevision> szzFileRevisions = convertFileRevisions(fileRevisions);
		fileRevisions.clear();
		Collections.sort(szzFileRevisions, revisionComp);
		return szzFileRevisions;
	}
	
}
