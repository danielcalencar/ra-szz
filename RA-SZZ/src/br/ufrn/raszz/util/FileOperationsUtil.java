package br.ufrn.raszz.util;

import java.util.*;
import java.io.*;
/*
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.wc2.*;/**/

import br.ufrn.raszz.model.szz.DiffHunk;
import br.ufrn.raszz.model.szz.Line;
import br.ufrn.raszz.model.szz.LineType;
import br.ufrn.raszz.model.szz.SzzFileRevision;

import java.text.*;
import java.util.regex.*;

import org.apache.log4j.*;

public abstract class FileOperationsUtil {

	private static final Logger log = Logger.getLogger(FileOperationsUtil.class); 
	private static Console c = System.console();
	public static boolean isPropertyChange = false;
	//private static String regex = "(//.*)|(/\\*.*)|(\\*.*)";
	private static String regex = "/\\*(?:.|[\\n\\r])*?\\*/";

	public static boolean isPropertyChangeOnly(ByteArrayOutputStream diff) throws Exception{
		boolean yes = false;

		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(diff.toByteArray())));
		try{
			int count = 0;
			while(br.ready()){

				String line = br.readLine().trim();
				if(count <= 5){//the first lines of the diff don't matter
					//log.info("line " + line);
					count++;
					continue;
				}

				if(line.contains("Property changes on:")){
					yes = true;
					break;
				}

				//c.readLine("analyzing line " + line);

				//only searching for modified parts
				if(isAddition(line) || isDeletion(line)){
					line = line.replace("-","");
					line = line.replace("+","");
					//c.readLine("now Im inside if");
					if(!FileOperationsUtil.isCommentOrBlankLine(line)){
						//c.readLine("not a comment nor a blank line");
						yes = false;
						break;
					}
				}
			}
		} finally {
			br.close();
		}
		//c.readLine("result: " + yes);
		return yes;
	}
	/*
	public static void convertFileRevisions(LinkedList<SVNFileRevision> fileRevisions, 
			LinkedList<SzzFileRevision> szzFileRevisions){
		for(SVNFileRevision svnfr : fileRevisions){
			SzzFileRevision szzfr = new SzzFileRevision(svnfr);
			szzFileRevisions.add(szzfr);
		}
	}*/

	public static Date getRevisionDate(String dateToParse) {
		SimpleDateFormat sdf = DateUtils.
			getSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", null);
		Date date = null;
		try{
			date = sdf.parse(dateToParse);
		} catch (Exception e){
			e.printStackTrace();
		}
		return date;
	}

	public static boolean isAddition(String line) {

		boolean result1;
		Pattern pattern = Pattern.compile("^(\\+)");
		Matcher matcher = pattern.matcher(line.trim());
		result1 = matcher.find();

		return result1;
	}

	public static boolean isDeletion(String line) {
		boolean result1;
		Pattern pattern = Pattern.compile("^(\\-)");
		Matcher matcher = pattern.matcher(line.trim());
		result1 = matcher.find();

		return result1;
	}

    public static boolean isTestFile(ByteArrayOutputStream baous) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baous.toByteArray())));
		Pattern testCase = Pattern.compile(SzzRegex.EXTENDS_TESTCASE.getValue());
		Pattern testAnnotation = Pattern.compile(SzzRegex.TEST_ANNOTATION.getValue());
		boolean result = false;
		try{
			while(br.ready()){
				String line = br.readLine();
				if(!isCommentOrBlankLine(line)){
					Matcher m1 = testCase.matcher(line);
					Matcher m2 = testAnnotation.matcher(line);
					if(m1.find() || m2.find()){
						result = true;
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			br.close();
		}
		return result;
	}

	public static String prepareLineContent(Line line){
		String content = line.getContent().trim();
		content = removeComment(content);
		if(content.length() > 0){
			if(content.charAt(0) == '+'){
				content = content.replaceFirst("\\+","");
				content = content.trim();
			} else if (content.charAt(0) == '-'){
				content = content.replaceFirst("-","");
				content = content.trim();
			}
		}
		content = content.replace(" ","");
		if(!content.endsWith("}")){
			content = content + "}";
		}
		if(!content.startsWith("}")){
			content = "}" + content;
		}
		return content;
	}

	public static String prepareContent(String line){
		String content = line.trim();
		if(content.length() > 0){
			if(content.charAt(0) == '+'){
				content = content.replaceFirst("\\+","");
				content = content.trim();
			} else if (content.charAt(0) == '-'){
				content = content.replaceFirst("-","");
				content = content.trim();
			}
		}
		content = content.replaceAll(" ","");
		return content;
	}

	public static List<String> getHeaders(ByteArrayOutputStream diff) throws IOException {
		List<String> headers = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(diff.toByteArray())));

		try {
			while (br.ready()) {
				String line = br.readLine();
				line = line.trim();
				if (isHunkHeader(line)) {
					headers.add(line);
				}
				if(line.contains("Property changes on")){
					isPropertyChange = true;
				}
			}
		} finally {
			br.close();
		}
		return headers;
	}


	public static void joinUnfinishedLines(List<DiffHunk> hunks){
		for(DiffHunk hunk : hunks){
			List<Line> lines = hunk.getContent();
			//c.readLine("printing hunk...");
			//c.readLine(hunk.toString());
			
			joinUnfinishedLinesWhenCloning(lines, false);
			/*
			int index = 0;
			for(Line line : lines){			
				
				//#debug
				//if (line.getContent().trim().equals("URI tcpURI = new URI(")) {
				//	System.out.println(line.getContent());
				//	System.out.println("DEBUG AQUI!");
				//}
									
				//c.readLine("getting content...");
				String content = line.getContent();
				content = removeComment(content);
				content = prepareContent(content);
				if(isCommentOrBlankLine(content)){
					index++;
					//c.readLine("comment or blank line!");
					continue;
				}
				//c.readLine("content: " + content);
				int count = 1;
				while(unfinished(content)){
					line.setContentAdjusted(true);
					//c.readLine(" it is not finished! ");
					//getting next line
					int final_index = index+count;
					if(final_index >= lines.size()){
						break;
					}
					Line nextline = lines.get(final_index);
					String nextcontent = nextline.getContent();
					nextcontent = removeComment(nextcontent);
					nextcontent = prepareContent(nextcontent);
					//c.readLine(" joining with " + nextcontent);
					content = content + nextcontent;
					//c.readLine(" final content " + content);
					line.setContent(content);
					nextline.setContent(" ");
					count++;
				}
				index++;
			}
			*/
		}
	}

	public static void joinUnfinishedLinesWhenCloning(List<Line> lines, boolean excludeRemovedLine){
		int index = 0;		
		for(Line line : lines){
			//c.readLine("getting content...");
			
			//if(line.getContent().contains("-    static Collection<IdentityHashSet<Tuple>> GetEquivalenceClasses(LOLoad op,"))				
			//if(line.getContent().contains("-public class Store implements HConstants {")) 
			//if(index==4)
				//log.info(line.getContent());
			
			int linenumber = (excludeRemovedLine)?line.getNumber():line.getPreviousNumber();
			if (linenumber == -1) {
				index++;
				continue;
			}			
			
			String content = line.getContent();
			content = removeComment(content);
			content = prepareContent(content);
			if(isCommentOrBlankLine(content)){
				index++;
				//c.readLine("comment or blank line!");
				line.setContent(content);
				continue;
			}
			//c.readLine("content: " + content);
			int count = 1;
			while(unfinished(content)){
				line.setContentAdjusted(true);
				//c.readLine(" it is not finished! ");
				//getting next line
				int final_index = index+count;
				if(final_index >= lines.size()){
					break;
				}
				Line nextline = lines.get(final_index);
				linenumber = (excludeRemovedLine)?nextline.getNumber():nextline.getPreviousNumber();
				if (linenumber == -1) {
					count++;
					continue;
				}
				
				String nextcontent = nextline.getContent();
				nextcontent = removeComment(nextcontent);
				nextcontent = prepareContent(nextcontent);
				//c.readLine(" joining with " + nextcontent);
				content = content + nextcontent;
				//c.readLine(" final content " + content);
				line.setContent(content);
				nextline.setContent(" ");
				count++;
			}
			index++;
			//if(line.getContent().equals("staticCollection<IdentityHashSet<Tuple>>GetEquivalenceClasses(LOLoadop,staticCollection<IdentityHashSet<Tuple>>getEquivalenceClasses(LOLoadop,")) 
			//if(line.getContent().trim().equals("staticCollection<IdentityHashSet<Tuple>>GetEquivalenceClasses(staticCollection<IdentityHashSet<Tuple>>getEquivalenceClasses(LOForEachop,Map<LogicalOperator,DataBag>derivedData){"))
				//log.info("achei!");
		}
	}

	public static boolean unfinished(String content){
		boolean result = false;
		content = content.trim().replace(" ","");
		//content = removeComment(content);
		if(!content.endsWith(";") & !content.endsWith("{") &
				!content.endsWith("}") &
				!content.endsWith(":")){
			if(!isAnnotation(content)){
				result = true;
			}
		}
		return result;
	}

	public static boolean isAnnotation(String content){
		Pattern p = Pattern.compile("^\\@.*$");
		Matcher m = p.matcher(content);
		if(m.find()){
			return true;
		}
		return false;
	}

	public static boolean runRegex(String content, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if(m.find()){
			return true;
		}
		return false;
	}

	public static boolean isCommentOrBlankLine(String line){
		line = prepareContent(line);
		if (line.length() == 0)
			return true;

		if(line.equals("\\Nonewlineatendoffile")){
			//c.readLine("here");
			return true;
		}

		boolean result = false;
		Pattern pattern = Pattern.compile("^(//)(.*)$");
		Matcher matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if (result)
			return true;

		pattern = Pattern.compile("^(/\\*)(.*)$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if (result)
			return true;

		pattern = Pattern.compile("^(\\*)(.*)$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if (result)
			return true;

		pattern = Pattern.compile("^(})(\\s*)$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if (result)
			return true;

		pattern = Pattern.compile("^(\\{)(\\s*)$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if (result)
			return true;

		return false;
	}

	public static List<DiffHunk> getDiffHunks(ByteArrayOutputStream diff, List<String> headers, 
			String previousPath, String nextPath, String revision, String nextRevision)
			throws IOException {
		List<DiffHunk> hunks = new ArrayList<DiffHunk>();
		for (String header : headers) {
			List<Line> deletionsBuffer = new ArrayList<Line>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(diff.toByteArray())));
			try {
				//used to build the evolution relationship when a modification occurs
				boolean linkageFlag = false;
				LineType previousType = LineType.CONTEXT;
				boolean headerFound = false;

				//@@ -[9],10 +9,10 @@
				int prevRevContextStartNumber = getPrevContextStartingLineNumber(header);
				//@@ -9,10 +[9],10 @@
				int nextRevContextStartNumber = getNextContextStartingLineNumber(header);

				//@@ -9,[10] +9,10 @@
				//int prevRevContextLineRange = getPrevContextLineRange(header);
				//@@ -9,10 +9,[10] @@
				//int nextRevContextLineRange = getNextContextLineRange(header);


				//we will need this when we adjust the content of a line to infer the number of the following revision
				int context_difference = nextRevContextStartNumber - prevRevContextStartNumber; 

				int prevSync =  prevRevContextStartNumber; 
				int nextSync = nextRevContextStartNumber;
				int deletions = 0;
				int additions = 0;

				boolean firstOccurrenceFound = false; //track when we reach the first context line
				boolean startDelCount = false;
				boolean startAddCount = false;

				DiffHunk hunk = new DiffHunk();
				hunk.setHeader(header);

				while (br.ready()) {
					String line = br.readLine();

					//#debug
					//c.readLine("line analyzed: " + line);
					//String response = "";
					//response = c.readLine("trace it?");
					//if(response.length() !=0){
					//	c.readLine("prevSync: " + prevSync);
					//	c.readLine("nextSync: " + nextSync);
					//	c.readLine("startDelCount " + startDelCount);
					//	c.readLine("startAddCount " + startAddCount);
					//}

					if (!headerFound && line.trim().equals(header)) {
						headerFound = true;
						continue;
					}

					if (headerFound) {
						Line lineobj = new Line();
						lineobj.setPreviousPath(previousPath);
						lineobj.setNextPath(nextPath);
						lineobj.setContent(line);
						if (!isHunkHeader(line)) {

							if (isDeletion(line)) {
								lineobj.setAdditions(additions);
								lineobj.setDeletions(deletions);
								deletions++;
								// comming from an addition
								if (linkageFlag) {
									deletionsBuffer.clear();
								}
								lineobj.setType(LineType.DELETION);
								if(!firstOccurrenceFound){
									firstOccurrenceFound = true;     
									startDelCount = true;
								} else if (!startDelCount){  //i think it doesnt happen in practice
									startDelCount = true;
								} else {
									prevSync++;
								}

								lineobj.setPreviousNumber(prevSync);
								lineobj.setPreviousRevision(revision);
								lineobj.setContext_difference(context_difference);
								deletionsBuffer.add(lineobj);
								
								//#debug
								//if(response.length() !=0)
								//	c.readLine("added to deletion buffer!");

								linkageFlag = false;
								previousType = LineType.DELETION;

							} else if (isAddition(line)) {
								lineobj.setDeletions(deletions);
								lineobj.setAdditions(additions);
								additions++;
								if (previousType == LineType.DELETION) {
									linkageFlag = true;
								}
								lineobj.setType(LineType.ADDITION);
								if(!firstOccurrenceFound){
									firstOccurrenceFound = true;     
									startAddCount = true;
								} else if (!startAddCount){ //very rare case
									startAddCount = true;
								} else {
									nextSync++;
								}
								lineobj.setNumber(nextSync);
								lineobj.setRevision(nextRevision);
								lineobj.setContext_difference(context_difference);
								previousType = LineType.ADDITION;
								if (linkageFlag) {
									//#debug
									//if(response.length() !=0)
									//	c.readLine("linkage flag! let's link!");
									lineobj.getOrigins().addAll(deletionsBuffer);

									for (Line deletion : deletionsBuffer) {
										//#debug
										//if(response.length() !=0)
										//	c.readLine("linking " + deletion.getContent() + " to " + lineobj.getContent());
										deletion.getEvolutions().add(lineobj);
									}
								}
							} else {
								//#debug
								//if(response.length() != 0){
								//	c.readLine("it is a context line!");
								//}
								if(!firstOccurrenceFound){ 
									firstOccurrenceFound = true;     
									startDelCount = true;
									startAddCount = true;
								} else { 
									if(!startDelCount){
										startDelCount = true;
									} else {
										prevSync++;
									}	

									if(!startAddCount){
										startAddCount = true;
									} else {
										nextSync++;
									}
								}
								lineobj.setType(LineType.CONTEXT);
								lineobj.setPreviousRevision(revision);
								lineobj.setRevision(nextRevision);
								//#debug
								//if(response.length() != 0){
								//	c.readLine("setting previous number: " + prevSync);
								//}
								lineobj.setPreviousNumber(prevSync);

								//#debug
								//if(response.length() != 0){
								//	c.readLine("setting next number: " + nextSync);
								//}
								lineobj.setNumber(nextSync);
								previousType = LineType.CONTEXT;
								linkageFlag = false;
								deletionsBuffer.clear();
							}
							//these lines don't have to be bothered anymore
							lineobj.setFoundInDiffHunks(true);
							hunk.getContent().add(lineobj);
						} else {
							break;
						}
					}
				}
				hunks.add(hunk);
			} finally {
				br.close();
			}
		}
		
		return hunks;
	}

	public static int getPrevContextStartingLineNumber(String header) {
		String[] tokens = header.split(" ");
		String toAnalyze = tokens[1];
		String[] tokens2 = toAnalyze.split(",");
		String lineNumberStr = tokens2[0].replace("-", "");
		int lineNumber = Integer.valueOf(lineNumberStr);
		return lineNumber;
	}

	public static int getPrevContextLineRange(String header) {
		String[] tokens = header.split(" ");
		String toAnalyze = tokens[1];
		String[] tokens2 = toAnalyze.split(",");
		String lineNumberStr = tokens2[1];
		int lineNumber = Integer.valueOf(lineNumberStr);
		return lineNumber;
	}

	public static int getNextContextStartingLineNumber(String header) {
		String[] tokens = header.split(" ");
		String toAnalyze = tokens[2];
		String[] tokens2 = toAnalyze.split(",");
		String lineNumberStr = tokens2[0].replace("+", "");
		int lineNumber = Integer.valueOf(lineNumberStr);
		return lineNumber;
	}

	public static int getNextContextLineRange(String header) {
		String[] tokens = header.split(" ");
		String toAnalyze = tokens[2];
		String[] tokens2 = toAnalyze.split(",");
		String lineNumberStr = tokens2[1];
		int lineNumber = Integer.valueOf(lineNumberStr);
		return lineNumber;
	}

	public static boolean isHunkHeader(String line) {
		Pattern pattern = Pattern.compile("@@\\s-\\d+,\\d+\\s\\+\\d+,\\d+\\s@@");
		Matcher matcher = pattern.matcher(line);
		return matcher.find();
	}

	public static boolean isImport(String content) {
		boolean result = false;
		content = prepareContent(content);
		Pattern pattern = Pattern.compile("^(import)(\\s*)(.*)$");
		Matcher matcher = pattern.matcher(content);
		result = matcher.find();
		if(result){
			log.info("import statement found, skipping it!");
		}
		return result;
	}	

	public static SzzFileRevision getPrevRev(SzzFileRevision fileRevision, List<SzzFileRevision> revisions){
		SzzFileRevision prev = null;
		int index = revisions.indexOf(fileRevision);
		//log.info("index " + index);
		//in case the file is not  the first of the collection
		if(index > 0){
			//log.info("prev index " + (index-1));
			prev = revisions.get(index-1);
		}
		return prev;
	}

        public static String getFileName(String path){
		if(path == null)
			return null;
		String[] tokens = path.split("/");
		if(tokens.length == 0)
			return null;
		String lastPart = tokens[tokens.length - 1];
		return lastPart;
	}
/*
	public static SVNLogEntryPath getSvnLogEntryPath(SVNRepository svn, long revision, String path) throws SVNException {
		//setting variables
		Collection<SVNLogEntry> entries = new ArrayList<SVNLogEntry>();
		SVNLogEntryPath entryPath = null;
		SvnOperationFactory fac = new SvnOperationFactory(); 

		SVNRevision rev = SVNRevision.create(revision);
		try{                            
			BackhoeLogEntryHandler handler = new BackhoeLogEntryHandler();
			log.info("trying to get revision: " + revision + " and path: " + path);
			// (paths, startrev, endrev, changedpaths, strictnode, limit, mergecommits, null, handler);
			svn.log(new String[]{""}, revision, revision, true, false, 0, true, null, handler);
			entries.addAll(handler.getEntries());
			if(!entries.isEmpty()){
				SVNLogEntry entry = (SVNLogEntry) ((List)entries).get(0);
				for(SVNLogEntryPath slep : entry.getChangedPaths().values()){
					String pathToInvestigate = slep.getPath();
					String fname = getFileName(pathToInvestigate);
					if(fname == null || !fname.contains(".java")){
						continue;
					}
					if(path.equals(pathToInvestigate)){
						entryPath = slep;
					}
				}
			} else {
				log.info("not found for revision: " + revision + " and path: " + path);
			}

		} catch (SVNException svne){
			log.error(svne.getMessage());
		}
		return entryPath;
	}*/
/*
	public static SVNLogEntryPath getSvnLogEntryPathBasedOnName(SVNRepository svn, long revision, String path) throws SVNException {
		//setting variables
		Collection<SVNLogEntry> entries = new ArrayList<SVNLogEntry>();
		SVNLogEntryPath entryPath = null;
		SvnOperationFactory fac = new SvnOperationFactory(); 

		SVNRevision rev = SVNRevision.create(revision);
		try{                            
			BackhoeLogEntryHandler handler = new BackhoeLogEntryHandler();
			log.info("trying to get revision: " + revision + " and path: " + path);
			// (paths, startrev, endrev, changedpaths, strictnode, limit, mergecommits, null, handler);
			svn.log(new String[]{""}, revision, revision, true, false, 0, true, null, handler);
			entries.addAll(handler.getEntries());
			if(!entries.isEmpty()){
				SVNLogEntry entry = (SVNLogEntry) ((List)entries).get(0);
				for(SVNLogEntryPath slep : entry.getChangedPaths().values()){
					String pathToInvestigate = slep.getPath();
					String fname_to_find = getFileName(pathToInvestigate);
					if(fname_to_find == null || !fname_to_find.contains(".java")){
						continue;
					}
					String fname = getFileName(path);
					if(fname.equals(fname_to_find)){
						entryPath = slep;
					}
				}
			} else {
				log.info("not found for revision: " + revision + " and path: " + path);
			}

		} catch (SVNException svne){
			log.error(svne.getMessage());
		}
		return entryPath;
	}*/

	private static String removeComment(String content){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if(m.find()){
			String comment = m.group();
			String temp = content.replace(comment,"");
			
			int quoMarkCount = 0;
			for (int i = 0; i < temp.length(); i++) {
				if (temp.charAt(i) == '"') quoMarkCount++;
			}
			
			if (quoMarkCount % 2 == 0) {
				content = content.replace(comment,"");
			}
				
			/*
			int qmFirstNoComment = temp.indexOf("\"");
			int qmLastNoComment = temp.lastIndexOf("\"");
			
			int qmFirstComment = comment.indexOf("\"");
			int qmLastComment = comment.lastIndexOf("\"");
			
			if (qmFirstNoComment == qmLastComment
			*/
		}
		//remove inline comment in the end of the line
		int offset = content.indexOf("//");
		if (-1 != offset) {
			content = content.substring(0, offset);
		}
		return content;
	}

	public static List<Integer> getAdditionsInHunk(Line line){
		List<Integer> additions = new ArrayList<Integer>();
		String content = prepareLineContent(line);
		for(Line evol : line.getEvolutions()){
			String evolcontent = prepareLineContent(evol);
			//#debug
			//c.readLine("evol content: " + evolcontent);
			if(content.equals(evolcontent)){
				additions.add(evol.getAdditions());
				//#debug
				//c.readLine("found additions: " + additions);
			}
		}
		return additions;
	}
}
