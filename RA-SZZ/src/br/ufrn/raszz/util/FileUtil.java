package br.ufrn.raszz.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.raszz.model.Line;

public class FileUtil {

	private static List<Match> commentMatches = new ArrayList<Match>();
	private static BufferedReader br;
	private static Map<String, List<Line>> files = new HashMap<String, List<Line>>();

	private static List<Line> readFile(String filename) throws IOException {
		FileInputStream stream = new FileInputStream(filename);
		br = new BufferedReader(new InputStreamReader(stream));
		List<Line> lines = new ArrayList<>();
		String content = br.readLine();
		int number = 1;
		while (content != null) {
			Line line = new Line();
			line.setContent(content);
			line.setNumber(number++);
			line.setPreviousNumber(number);
			lines.add(line);
			content = br.readLine();
		}
		return lines;
	}
	
	private static List<Line> readFile(String folder, String commitId, String pathfile) throws IOException {
		FileInputStream stream = new FileInputStream(folder + commitId + pathfile);
		br = new BufferedReader(new InputStreamReader(stream));
		List<Line> lines = new ArrayList<>();
		String content = br.readLine();
		int number = 1;
		while (content != null) {
			Line line = new Line();
			line.setContent(content);
			line.setNumber(number++);
			line.setPreviousNumber(number);
			lines.add(line);
			content = br.readLine();
		}
		return lines;
	}
	
	public static long matchLinenumber(String folder, long commitId, String pathfile, String element, long starline, Long endline)
			throws IOException {
		int interval = -1;
		try {
			String filename = folder + commitId + pathfile;
			List<Line> lines = null;
			if (files.containsKey(filename))
				lines = files.get(filename);
			else {
				lines = readFile(filename);
				files.put(filename, lines);
			}
			joinUnfinishedLinesWhenCloning(lines);

			for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
				Line line = iterator.next();
				String content = line.toString();
				if (content == null)
					continue;
				if (FileUtil.isCommentOrBlankLine(content))
					continue;
				String modifiedLine = prepareMethod(content);
				if (modifiedLine.equals(shortenLine(element)) || (element.trim().startsWith("package ")
						&& modifiedLine.equals(shortenLine(element.replace("package ", ""))))) {
					interval = line.getPreviousNumber() - 1;
				}
			}
		} catch (IOException e) {
			interval = -1;
		}
		return interval;

	}

	public static long matchLinenumber(String folder, String commitId, String pathfile, String element)
			throws IOException {
		int interval = -1;
		try {
			List<Line> lines = readFile(folder, commitId, pathfile);
			joinUnfinishedLinesWhenCloning(lines);

			for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
				Line line = iterator.next();
				String content = line.toString();
				if (content == null)
					continue;
				if (FileUtil.isCommentOrBlankLine(content))
					continue;
				String modifiedLine = prepareMethod(content);
				if (modifiedLine.equals(shortenLine(element)) || (element.trim().startsWith("package ")
						&& modifiedLine.equals(shortenLine(element.replace("package ", ""))))) {
					interval = line.getPreviousNumber() - 1;
				}
			}
		} catch (IOException e) {
			interval = -1;
		}
		return interval;

	}

	public static long[] matchCodeIntervalNumber(String folder, String commitId, String pathfile, String element)
			throws IOException {
		long[] interval = new long[2];
		try {
			List<Line> lines = readFile(folder, commitId, pathfile);
			joinUnfinishedLinesWhenCloning(lines);

			int countOpeningCurlyBraces = 0;
			int countClosingCurlyBraces = 0;

			for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
				Line line = iterator.next();
				String content = line.toString();
				if (content == null)
					continue;
				if (FileUtil.isCommentOrBlankLine(content))
					continue;
				String modifiedLine = prepareMethod(content);
				if (modifiedLine.equals(shortenLine(element)) || (element.trim().startsWith("package ")
						&& modifiedLine.equals(shortenLine(element.replace("package ", ""))))) {
					interval[0] = line.getNumber();
					do {
						if (content != null) {
							countOpeningCurlyBraces += calculateOpeningCurlyBraces(content);
							countClosingCurlyBraces += calculateClosingCurlyBraces(content);
						}
						if (countOpeningCurlyBraces == countClosingCurlyBraces) {
							interval[1] = line.getPreviousNumber() - 1;
							return interval;
						} else if (iterator.hasNext()) {
							line = iterator.next();
							content = line.toString();
						} else
							break;
					} while (countOpeningCurlyBraces != countClosingCurlyBraces);
				}
			}
		} catch (IOException e) {
			interval[0] = -1;
			interval[1] = -1;
		}
		return interval;
	}

	public static void joinUnfinishedLinesWhenCloning(List<Line> lines) {
		boolean isOpenBlockComment = false;

		for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
			Line line = iterator.next();
			if (line.toString() == null)
				continue;
			String content = line.toString();
			content = removeComments(content);
			//content = removeFalseComments(content);
			int j = content.indexOf("*/");
			
			if (!isOpenBlockComment) {
				//isOpenBlockComment = (content.contains("/*") && !content.contains("*/"));
				int index = content.indexOf("/*");
				if (index != -1) {
					String tokenAfterComment = content.substring(0, index);
					boolean isEndedWithQuotationMark = tokenAfterComment.endsWith("\"");
					isOpenBlockComment = ((index != -1 && !isEndedWithQuotationMark) && j == -1);
				}
			}
			else if (content.contains("*/")) //&& !(content.contains("\"*/\"")))
				isOpenBlockComment = false;
			if (isCommentOrBlankLine(content) || isOpenBlockComment) {
				line.setContent(content);
				continue;
			}

			// content = removeComments(content);
			content = prepareContent(content);

			while (unfinished(content)) {
				if (!iterator.hasNext())
					break;
				Line nextline = iterator.next();
				String nextcontent = nextline.getContent();
				nextcontent = removeComments(nextcontent);
				nextcontent = prepareContent(nextcontent);
				content = content + nextcontent;
				line.setContent(content);
				line.setContentAdjusted(true);
				nextline.setContent(null);
			}
			line.setContent(content);
		}
	}

	public static boolean unfinished(String content) {
		boolean result = false;
		content = shortenLine(content);
		// if (!content.endsWith(";") & !content.endsWith("{") &
		// !content.endsWith(";}") & !content.endsWith(":")) {
		if (!content.endsWith(";") & !content.endsWith("{") & !content.endsWith(";}") & !content.endsWith(":")) {
			if (!isAnnotation(content)) {
				result = true;
			}
		}
		return result;
	}

	private static boolean isAnnotation(String content) {
		Pattern p = Pattern.compile("^\\@.*$");
		Matcher m = p.matcher(content);
		if (m.find()) {
			return true;
		}
		return false;
	}

	private static String prepareContent(String line) {
		if (line.contains(" class ")) {
			int index = line.indexOf(" class ");
			line = line.substring(index);
		} else {
			line = " " + line;
			line = line.replaceAll(" static ", " ");
			line = " " + line;
			line = line.replaceAll(" final ", " ");
			line = " " + line;
			line = line.replaceAll(" transient ", " ");
			line = " " + line;
			line = line.replaceAll(" volatile ", " ");
			line = " " + line;
			line = line.replaceAll(" native ", " ");
			line = " " + line;
			line = line.replaceAll(" const ", " ");
			line = line.replaceAll("\\<\\?\\>", "");
			line = line.replaceAll("\\<T\\>", "");
			line = line.replaceAll("\\(final ", " ( ");
			line = line.replaceAll("\\,final ", " , ");
			line = line.replaceAll(" final\\,", " , ");
			line = line.replaceAll(" synchronized ", "");
		}
		line = shortenLine(line);
		return line;
	}

	private static String prepareMethod(String line) {
		if (!(line.trim().equals(";") || line.trim().equals("\\{") || line.trim().equals("throws")
				|| line.trim().equals("=")))
			line = line.split("\\{")[0].split("throws")[0].split("=")[0].split(";")[0];
		return line;
	}

	private static String shortenLine(String line) {
		return line.trim().replaceAll(" ", "");
	}

	private static int calculateOpeningCurlyBraces(String line) {
		int count = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '{')
				count++;
		}
		return count;
	}

	private static int calculateClosingCurlyBraces(String line) {
		int count = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '}')
				count++;
		}
		return count;
	}
	
	private static int calculateQuotationMarks(String line) {
		int count = 0;
		if ((line.length() > 0) && (line.charAt(0) == '"')) count++;
		for (int i = 1; i < line.length(); i++) {
			if ((line.charAt(i) == '"') && (line.charAt(i-0) != '\\'))
				count++;
		}
		return count;
	}

	public static boolean isCommentOrBlankLine(String line) {
		line = line.trim().replace(" ", "");
		if (line.length() == 0)
			return true;

		if (line.equals("\\Nonewlineatendoffile")) {
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

	public static String removeComments(String content) {
		Pattern commentsPattern = Pattern.compile("(//.*?$)|(/\\*.*?\\*/)", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern stringsPattern = Pattern.compile("(\".*?(?<!\\\\)\")");

		Matcher commentsMatcher = commentsPattern.matcher(content);
		while (commentsMatcher.find()) {
			Match match = new Match();
			match.start = commentsMatcher.start();
			match.text = commentsMatcher.group();
			commentMatches.add(match);
		}

		List<Match> commentsToRemove = new ArrayList<Match>();

		Matcher stringsMatcher = stringsPattern.matcher(content);
		while (stringsMatcher.find()) {
			for (Match comment : commentMatches) {
				if (comment.start > stringsMatcher.start() && comment.start < stringsMatcher.end())
					commentsToRemove.add(comment);
			}
		}
		for (Match comment : commentsToRemove)
			commentMatches.remove(comment);

		for (Match comment : commentMatches)
			content = content.replace(comment.text, " ");
		
		return content;
	}

	private static class Match {
		int start;
		String text;
	}
/*
	public static String removeFalseComments(String content) {
		
		/*qtd content.indexOf("/*");
		if (content.indexOf("/*") != -1)
		
		String[] tokens = content.split("/*");
		if (tokens.length % 2 == 0) {
			for (int i = 0; i < tokens.length; i = i+2) {
				int afterQuoMarkCount = calculateQuotationMarks(tokens[i]);
				int beforeQuoMarkCount = calculateQuotationMarks(tokens[i+1]);
				if ((afterQuoMarkCount % 2 == 0) && (beforeQuoMarkCount % 2 == 0))
					
			}
		}*/
				
		//String regex = "/\\*(?:.|[\\n\\r])*?\\*/";
	/*	Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if (m.find()) {
			String comment = m.group();
			String temp = content.replace(comment, "");

			int quoMarkCount = 0;
			for (int i = 0; i < temp.length(); i++) {
				if (temp.charAt(i) == '"')
					quoMarkCount++;
			}

			if (quoMarkCount % 2 == 0) {
				content = content.replace(comment, "");
			}
		}
		return content;
	}
*/
}
