package br.ufrn.raszz.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.incava.analysis.DetailedReport;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiff.Type;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.diffj.app.DiffJ;
import org.incava.diffj.app.Options;

import br.ufrn.raszz.model.szz.Line;
import br.ufrn.raszz.model.szz.LineType;
import br.ufrn.raszz.model.szz.SzzFileRevision;
import br.ufrn.razszz.connectoradapter.SzzRepository;

public class DiffJOperationsUtil {	
	
	public static void saveFile(SzzRepository repository, String repoUrl, SzzFileRevision fromRev, SzzFileRevision toRev, String fname) {
		try {
			ByteArrayOutputStream baousFr = repository.catOperation(repoUrl, fromRev.getPath(), fromRev.getRevision());
			OutputStream outputStream = new FileOutputStream("resources\\source\\from\\"+ fromRev.getRevision() + "-" + fname);
			baousFr.writeTo(outputStream);
			
			ByteArrayOutputStream baousNextFr = repository.catOperation(repoUrl, toRev.getPath(), toRev.getRevision());
			outputStream = new FileOutputStream("resources\\source\\to\\"+ toRev.getRevision() + "-" + fname);
			baousNextFr.writeTo(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static Report diffJOperation(SzzRepository repository, String repoUrl, SzzFileRevision fromRev, SzzFileRevision toRev, String fname) {
		
		saveFile(repository, repoUrl, fromRev, toRev, fname);
		
		Options opts = new Options();
		String[] args = {"resources\\source\\from\\"+ fromRev.getRevision() + "-" + fname, "resources\\source\\to\\"+ toRev.getRevision() + "-" + fname};
        List<String> names = opts.process(Arrays.asList(args));

        if (opts.showVersion()) {
            System.out.println("diffj, version " + Options.VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        DiffJ diffj = new DiffJ(opts.showBriefOutput(), opts.showContextOutput(), opts.highlightOutput(),
                                opts.recurse(),
                                opts.getFirstFileName(), opts.getFromSource(),
                                opts.getSecondFileName(), opts.getToSource());
        diffj.processNames(names);
        return diffj.getReport();
	}
	
	public static FileDiff hasDiffJType(Report diffJReport, Line line) {
		FileDiffs hunks = diffJReport.getDifferences();
		//System.out.println(line.getPreviousNumber());
		for (FileDiff fileDiff : hunks) {
			if ((fileDiff.getFirstLocation().getStart().line <= line.getPreviousNumber()) 
					&& (fileDiff.getFirstLocation().getEnd().line >= line.getPreviousNumber())){
				//System.out.println(fileDiff.getType().toString());
				if (!fileDiff.getType().toString().equals("a")) { //ignore when addtion code - szz not suport 
					List<String> contents = ((DetailedReport) diffJReport).getFromContent();
					List<Line> lines = convertListStringToListLine(contents, line.getPreviousRevision(), line.getRevision());
					try {
						// true = exclude removed lines - thus lines with getnumber == -1 are not "prepared"
						FileOperationsUtil.joinUnfinishedLinesWhenCloning(lines, true);
					} catch (Exception ex) {
						System.out.println(ex.getStackTrace());
					}
					Line fromLine = lines.get(line.getPreviousNumber()-1);
					//System.out.println(fromLine.getContent());
					if (fromLine.getContent().equals(line.getContent())) {
						//&& ((DetailedReport) diffJReport).getFromContent().get(line.getPreviousNumber()-1).equals(line.getContent())) 
						//compareType(line.getType(), fileDiff.getType())
						return fileDiff;
					}
				}				
			}
		}
		return null;
	}
	
	private static List<Line> convertListStringToListLine(List<String> contents, String previousRevision, String revision){
		LinkedList<Line> lines = new LinkedList<Line>();
		int linenumber = 0;
		for (String lineContent : contents) {	
			final Line line = new Line();
			line.setContent(lineContent);
			line.setPreviousNumber(++linenumber);
			line.setPreviousRevision(previousRevision);
			line.setRevision(revision);
			line.setNumber(linenumber);
			lines.add(line);
		}
		return lines;
	}
		
	
	@SuppressWarnings("unused")
	private static boolean compareType(final LineType revType, final Type diffjType) {
		if (revType.toString().equals("addition") && diffjType.toString().equals("a"))
			return true;
		if (revType.toString().equals("deletion") && diffjType.toString().equals("d"))
			return true;
		if (revType.toString().equals("context") && diffjType.toString().equals("c"))
			return true;	
		return false;
	}
}
