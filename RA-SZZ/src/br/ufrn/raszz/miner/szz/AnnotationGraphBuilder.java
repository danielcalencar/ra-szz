package br.ufrn.raszz.miner.szz;

import static br.ufrn.raszz.util.FileOperationsUtil.getDiffHunks;
import static br.ufrn.raszz.util.FileOperationsUtil.getHeaders;
import static br.ufrn.raszz.util.FileOperationsUtil.isPropertyChangeOnly;
import static br.ufrn.raszz.util.FileOperationsUtil.joinUnfinishedLines;
import static br.ufrn.raszz.util.FileOperationsUtil.joinUnfinishedLinesWhenCloning;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.raszz.model.szz.AnnotationGraphModel;
import br.ufrn.raszz.model.szz.DiffHunk;
import br.ufrn.raszz.model.szz.Line;
import br.ufrn.raszz.model.szz.LineType;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import br.ufrn.razszz.connectoradapter.SzzRepository;

public class AnnotationGraphBuilder {
	//private static final Logger log = Logger.getLogger(AnnotationGraphBuilder.class);

	public AnnotationGraphModel buildLinesModel(SzzRepository repository,
			LinkedList<SzzFileRevision> szzFileRevisions, 
			String repoUrl, String project) throws Exception {
		
		AnnotationGraphModel model = new AnnotationGraphModel();
		for (final SzzFileRevision currentSzzFileRev : szzFileRevisions) {			
			//#debug
			//if(!currentSzzFileRev.getRevision().equals("57533"))//1450050) //369828) //1460310
				//continue;

			final ByteArrayOutputStream currentContent = repository.catOperation(repoUrl, currentSzzFileRev);
			//if (szzFileRevisions.size() == 1) {
			if (szzFileRevisions.indexOf(currentSzzFileRev) == (szzFileRevisions.size() - 1)) {
				cloneLinesToPreviousRevision(model, currentContent, currentSzzFileRev.getRevision(),
						"", currentSzzFileRev.getPath(), currentSzzFileRev, project);
				break;
			}
			
			/*if (szzFileRevisions.indexOf(currentSzzFileRev) == (szzFileRevisions.size() - 1)) 
				break;*/
				
			final SzzFileRevision nextSzzFileRev = szzFileRevisions.get(szzFileRevisions.indexOf(currentSzzFileRev) + 1);
			final ByteArrayOutputStream diffContent = repository.diffOperation(repoUrl, currentSzzFileRev, nextSzzFileRev);
			final ByteArrayOutputStream nextContent = repository.catOperation(repoUrl, nextSzzFileRev);
			try{
				buildLinesModel(model, diffContent, currentContent, nextContent, currentSzzFileRev.getRevision(), nextSzzFileRev.getRevision(), 
						currentSzzFileRev.getPath(), nextSzzFileRev.getPath(), currentSzzFileRev, nextSzzFileRev, project);
			} finally {
				diffContent.close();
				currentContent.close();
				nextContent.close();
			}			
		}
		return model;
	}

	public void buildLinesModel(AnnotationGraphModel model, ByteArrayOutputStream diff, ByteArrayOutputStream frContent,
			ByteArrayOutputStream nextFrContent, String revision, String nextRevision, String previousPath, String nextPath,
			SzzFileRevision fr, SzzFileRevision nextFr, String project)
			throws Exception {
		List<String> headers = getHeaders(diff);
		List<DiffHunk> hunks = getHunks(model, diff, headers, previousPath, nextPath, fr, nextFr, revision, nextRevision);
		//c.readLine("printing hunks...");
		//for(DiffHunk hunk : hunks){
		//	c.readLine(hunk.toString());
		//}
		if (!hunks.isEmpty()) {
			//c.readLine("is property Change only?!");
			if(!isPropertyChangeOnly(diff)){
				buildLinesBeforeAfterBetweeHunks(model, hunks, frContent, nextFrContent, revision, 
						nextRevision, previousPath, nextPath, fr, nextFr, project);
			} else {
				//c.readLine("yes! only property changes!");
				cloneLinesToPreviousRevision(model, frContent, revision,
						nextRevision, previousPath, fr, project);
			}
		} else {
			cloneLinesToPreviousRevision(model,frContent, revision, 
					nextRevision, previousPath, fr, project);
		}
		
		
		//#debug
		//List<Line> lines = model.get(fr);
		//c.readLine("revision: " + fr.getRevision());
		//for(Line line : lines){
		//	c.readLine("content: " + line.getContent());
		//	c.readLine("number: " + line.getNumber());
		//	c.readLine("previousNumber: " +  line.getPreviousNumber());
		//}
		headers.clear();
		hunks.clear();
	}

	private void buildLinesBeforeAfterBetweeHunks(AnnotationGraphModel model, List<DiffHunk> hunks, ByteArrayOutputStream frContent,
			ByteArrayOutputStream nextFrContent, String previousRevision, String revision, String previousPath, 
			String nextPath, SzzFileRevision fr, SzzFileRevision nextFr, String project) throws IOException {

		//c.readLine("not propertychange... getting lines not involved in hunks!!!");
		//String fname = getFileName(previousPath);

		ByteArrayInputStream baous1 = new ByteArrayInputStream(frContent.toByteArray());
		InputStreamReader isr1 = new InputStreamReader(baous1);
		BufferedReader br = new BufferedReader(isr1);
		LinkedList<Line> linesNotInHunk = model.get(fr);

		if(linesNotInHunk == null){
			linesNotInHunk = new LinkedList<Line>();
		}

		int lineNumber = 1;
		//getting the lines that are out of hunks.
		try{
			while (br.ready()) {

				String lineContent = br.readLine();
				Line line = null;
				for (DiffHunk hunk : hunks) {
					//because of this, we are going to put every +addition with line -1
					//which is useful in the traceBack method createLinesInPreviousRevisions
					line = hunk.isLinePreviousRevisionInvolved(lineNumber);
					if (line != null) break;
				}

				if (line == null) {
					Line lineobj = new Line();
					lineobj.setContent(lineContent);
					lineobj.setRevision(revision);
					lineobj.setPreviousRevision(previousRevision);
					lineobj.setPreviousPath(previousPath);
					lineobj.setNextPath(nextPath);
					lineobj.setPreviousNumber(lineNumber);
					linesNotInHunk.add(lineobj);
				}
				lineNumber++;
			}
		} finally {
			br.close();
			br = null;
			isr1 = null;
			baous1 = null;
		}
	
		Collections.sort(linesNotInHunk, new Comparator<Line>() {
			@Override
			public int compare(Line o1, Line o2) {				
                return o1.getPreviousNumber() < o2.getPreviousNumber() ? -1 : (o1.getPreviousNumber() > o2.getPreviousNumber() ? +1 : 0);
			}
		});
		

		for(Line currentLine : linesNotInHunk){ 
			if(currentLine.getFoundInDiffHunks())
				continue;
			Line lastLine = hunks.get(hunks.size()-1).getLastLine();
			Line firstLine = hunks.get(0).getFirstLine();
			int lastLineNumber = lastLine.getPreviousNumber();
			if(lastLineNumber == -1){ // in case the last line of the hunk was an addition
				lastLineNumber = lastLine.getNumber();
			}
			for (DiffHunk hunk : hunks) {
				int firstLineNextHunk = -1;
				if ((hunks.indexOf(hunk) + 1) != hunks.size()) { // if this is not the last hunk
					firstLineNextHunk = hunks.get(hunks.indexOf(hunk) + 1).getFirstLine().getPreviousNumber();
					if (currentLine.getPreviousNumber() > hunk.getLastLine().getPreviousNumber()
							&& currentLine.getPreviousNumber() < firstLineNextHunk) {
						// in case the current line is in between the hunk and the next hunk
						//c.readLine("this line is in between hunks");
						int diffPosition = hunk.getLastLine().getNumber() - hunk.getLastLine().getPreviousNumber();
						currentLine.setNumber(currentLine.getPreviousNumber() + diffPosition);
					}
				}
			}
			//in case the Line is after all hunks
			if (currentLine.getPreviousNumber() > lastLineNumber) {
				//c.readLine("this line is after all hunks");
				if(lastLine.getPreviousNumber() != -1){
					int diffPosition = lastLine.getNumber() - lastLine.getPreviousNumber();
					currentLine.setNumber(currentLine.getPreviousNumber() + diffPosition);
				} else {
					//c.readLine("ops! should not be happening!!!");
					//c.readLine(currentLine.getContent());
					//c.readLine("path: " + currentLine.getPreviousPath());
					//c.readLine("revision " + revision + " previous_revision: " + previousRevision);
					//c.readLine("previousNumber = " + currentLine.getPreviousNumber());
					//c.readLine("context diff: " + lastLine.getContext_difference());
					//c.readLine("additions so far:" + lastLine.getAdditions() );
					//c.readLine("deletions so far:" + lastLine.getDeletions() + " (last)");
					currentLine.setNumber(currentLine.getPreviousNumber() + 
							lastLine.getContext_difference() + 
							(lastLine.getAdditions() - (lastLine.getDeletions()-1))
							);
					//c.readLine("new number! " + currentLine.getNumber());
				}

      			// modifications before the hunks, which means that they didnt change
			} else if (currentLine.getPreviousNumber() < firstLine.getPreviousNumber()) {
				//c.readLine("this line is before all hunks");
				// in case the Line is before all hunks
				currentLine.setNumber(currentLine.getPreviousNumber());
			}
			lineNumber++;
		}

		//because of this, we are going to put every +addition with line -1
		//which is useful in the traceBack method createLinesInPreviousRevisions
		for(Line isAddition : linesNotInHunk){
			if(isAddition.getType() == LineType.ADDITION){
				isAddition.setNumber(-1);
				isAddition.setPreviousNumber(-1);//don't think it is necessary but let's enforce it
			}
		}

		// true = exclude removed lines - thus lines with getnumber == -1 are not "prepared"
		joinUnfinishedLinesWhenCloning(linesNotInHunk, true);
		model.put(fr,linesNotInHunk);
	}

	private void cloneLinesToPreviousRevision(AnnotationGraphModel model, ByteArrayOutputStream frContent,
			String previousRevision, String revision, String previousPath,
			SzzFileRevision fr, String project) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(frContent.toByteArray())));
		LinkedList<Line> lines = model.get(fr);
		//log.info("cloning revision: " + revision + " to " + previousRevision);
		String temp = "cloning revision: " + revision + " to " + previousRevision;
		if (temp.equals("cloning revision: 226700 to 178816"))
			System.out.println("LOOP?");
		if(lines == null){
			lines = new LinkedList<Line>();
		}
		int lineNumber = 1;
		try{
			while (br.ready()) {
				String lineContent = br.readLine();
				final Line lineNotInHunk = new Line();
				lineNotInHunk.setContent(lineContent);
				lineNotInHunk.setPreviousNumber(lineNumber);
				lineNotInHunk.setPreviousRevision(previousRevision);
				lineNotInHunk.setRevision(revision);
				lineNotInHunk.setNumber(lineNumber);
				lines.add(lineNotInHunk);
				lineNumber++;
			}
		} finally {
			br.close();
		}
	
		// true = exclude removed lines - thus lines with getnumber == -1 are not "prepared"
		joinUnfinishedLinesWhenCloning(lines, true);
		model.put(fr,lines);
		//log.info("no diff between " + previousPath + " and " + nextPath);
	}
	
	private List<DiffHunk> getHunks(AnnotationGraphModel model, ByteArrayOutputStream diff, List<String> headers, 
			String previousPath, String nextPath, SzzFileRevision fr, SzzFileRevision nextFr,
			String revision, String nextRevision) throws IOException{
		List<DiffHunk> hunks = getDiffHunks(diff, headers, previousPath, nextPath, revision, nextRevision);
		joinUnfinishedLines(hunks);

		//updating my model map
		for(DiffHunk hunk : hunks){
			//#debug
			//String response = "";
			//response = c.readLine("header: " + hunk.getHeader() + " skip?");
			//if(response.length() == 0){
			//	continue;
			//}

			for(Line line : hunk.getContent()){
				//#debug
				//c.readLine("type: " + line.getType());
				//c.readLine("content: " + line.getContent());
				LinkedList<Line> lines = model.get(fr);
				if(lines == null){
					lines = new LinkedList<Line>();
				}
				lines.add(line);
				model.put(fr, lines);
			}
		}
		return hunks;
	}

}
