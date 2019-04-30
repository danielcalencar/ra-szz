package refdiff.core;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import refdiff.core.api.RefactoringHandler;
import refdiff.core.api.SvnRefactoringDetector;
import refdiff.core.rm2.analysis.RefDiffConfig;
import refdiff.core.rm2.analysis.RefDiffConfigImpl;
import refdiff.core.rm2.analysis.StructuralDiffHandler;
import refdiff.core.rm2.analysis.SvnHistoryStructuralDiffAnalyzer;
import refdiff.core.rm2.model.SDModel;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

public class RefDiffSVN implements SvnRefactoringDetector {

    public List<SDRefactoring> detectAtCommit(SVNRepository repository, String commitId, String folder) {
        List<SDRefactoring> result = new ArrayList<>();
        SvnHistoryStructuralDiffAnalyzer sda = new SvnHistoryStructuralDiffAnalyzer(config);
        sda.detectAtCommit(repository, folder, commitId, new StructuralDiffHandler() {
            @Override
            public void svnHandle(SVNLogEntry commitData, SDModel sdModel) {
                result.addAll(sdModel.getRefactorings());
            }
        });
        return result;
    }

    private RefDiffConfig config;

    public RefDiffSVN() {
        this(new RefDiffConfigImpl());
    }

    public RefDiffSVN(RefDiffConfig config) {
        this.config = config;
    }

    private final class HandlerAdpater extends StructuralDiffHandler {
        private final RefactoringHandler handler;

        private HandlerAdpater(RefactoringHandler handler) {
            this.handler = handler;
        }

        @Override
        public void svnHandle(SVNLogEntry commitData, SDModel sdModel) {
            handler.svnHandle(commitData, sdModel.getRefactorings());
        }

        @Override
        public void handleException(String commitId, Exception e) {
            handler.handleException(commitId, e);
        }
        
    }
    /*
    @Override
    public void detectAtCommit(SVNRepository repository, String commitId, RefactoringHandler handler) {
        SvnHistoryStructuralDiffAnalyzer sda = new SvnHistoryStructuralDiffAnalyzer(config);
        sda.detectAtCommit(repository, commitId, new HandlerAdpater(handler));
    }*/

    @Override
    public String getConfigId() {
        return config.getId();
    }
}
