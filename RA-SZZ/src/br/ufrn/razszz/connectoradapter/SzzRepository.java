package br.ufrn.razszz.connectoradapter;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.raszz.model.RepositoryType;
import br.ufrn.raszz.model.szz.SzzFileRevision;

public interface SzzRepository {

	RepositoryType getConnectorType();
	String getUrl();
	String getUser();
	String getPassword();
	String getRepositoryFolder();
		
	List<String> getChangedPaths(String commitId) throws Exception;
	LinkedList<SzzFileRevision> extractSZZFilesFromPath(String repoUrl, String path, String commitId, boolean isReTrace) throws Exception;	
	ByteArrayOutputStream catOperation(String repoUrl, String path, String commitId) throws Exception;
	ByteArrayOutputStream catOperation(String repoUrl, SzzFileRevision szzFileRevision) throws Exception;
	ByteArrayOutputStream diffOperation(String repoUrl, SzzFileRevision currentSzzFileRevision, SzzFileRevision nextSzzFileRevision) throws Exception;
	LinkedList<SzzFileRevision> convertFileRevisions(LinkedList<?> fileRevisions);

	
}
