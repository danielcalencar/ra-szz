package br.ufrn.razszz.connectoradapter;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;

import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.szz.GitFileRevisionAdapter;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import core.connector.model.GitFileRevision;
import core.connector.service.GitService;
import core.connector.service.impl.GitServiceImpl;

public class GitRepositoryAdapter extends SzzRepositoryImpl implements SzzRepository {
	
	private GitService gitService = new GitServiceImpl();
	private Repository repository;
	
	public GitRepositoryAdapter(Repository repository, String repoUrl, String repoFolder) {
		this.repository = repository;
		this.connectorType = RepositoryType.GIT;
		this.url = repoUrl;
		this.repositoryFolder = repoFolder;
	}

	@Override
	public List<String> getChangedPaths(String commitId) throws Exception {
		return gitService.getChangedFilePaths(repository, commitId);
	}
	
	@Override
	public ByteArrayOutputStream catOperation(String repoUrl, String path, String commitId) throws Exception {
		return gitService.catOperation(repository, path, commitId);
	}

	@Override
	public ByteArrayOutputStream catOperation(String repoUrl, SzzFileRevision szzFileRevision) throws Exception {
		return gitService.catOperation(repository, szzFileRevision.getPath(), szzFileRevision.getRevision());
	}

	@Override
	public ByteArrayOutputStream diffOperation(String repoUrl, SzzFileRevision currentSzzFileRevision,
			SzzFileRevision nextSzzFileRevision) throws Exception {
		return gitService.diffOperation(repository, currentSzzFileRevision.getPath(), currentSzzFileRevision.getRevision(),
				nextSzzFileRevision.getPath(), nextSzzFileRevision.getRevision());
	}

	@Override
	public LinkedList<SzzFileRevision> convertFileRevisions(LinkedList<?> fileRevisions) {
		LinkedList<SzzFileRevision> szzFileRevisions = new LinkedList<SzzFileRevision>();
		for(Object fr : fileRevisions){
			SzzFileRevision gitfr = new GitFileRevisionAdapter((GitFileRevision)fr);
			szzFileRevisions.add(gitfr);
		}
		return szzFileRevisions;
	}

	@Override
	protected LinkedList<SzzFileRevision> getSzzFileRevisions(String path, String commitId) throws Exception {
		final LinkedList<GitFileRevision> fileRevisions = gitService.getHistoryFileRevisions(repository, path, commitId);
		final LinkedList<SzzFileRevision> szzFileRevisions = convertFileRevisions(fileRevisions);
		fileRevisions.clear();
		return szzFileRevisions;
	}

}
