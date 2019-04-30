package br.ufrn.raszz.model.szz;

import java.util.ArrayList;
import java.util.List;

public class DiffHunk {
	private String header;
	private List<Line> content;

	public DiffHunk() {
		content = new ArrayList<Line>();
	}

	public List<Line> getContent() {
		return content;
	}

	public void setContent(List<Line> content) {
		this.content = content;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Line isLinePreviousRevisionInvolved(int lineNumber) {
		//Line lineInvolved = null;
		for (Line line : content) {
			//int number = -1; // just a weird number that can not be matched
			/*if (line.getType() == LineType.DELETION) {
				number = line.getPreviousNumber();
			} else if (line.getType() == LineType.CONTEXT) {
				number = line.getPreviousNumber();
			}
			if (linenNumber == number)
				lineInvolved = line;
			*/
			if (line.getType() != LineType.ADDITION) {
				if (lineNumber == line.getPreviousNumber())
					return line;
				
			}

		}
		return null;//lineInvolved;
	}

	public Line isLineNextRevisionInvolved(int lineNumber) {
		Line lineInvolved = null;
		for (Line line : content) {
			int number = -1; // just a weird number that can not be matched
			if (line.getType() == LineType.ADDITION) {
				number = line.getNumber();
			} else if (line.getType() == LineType.CONTEXT) {
				number = line.getNumber();
			} 
			
			if (lineNumber == number) {
				lineInvolved = line;
			}

		}
		return lineInvolved;
	}

	public Line getLastLine() {
		if (!content.isEmpty()) {
			return content.get(content.size() - 1);
		}
		return null;
	}

	public Line getFirstLine() {
		if (!content.isEmpty()) {
			return content.get(0);
		}
		return null;
	}

	public String toString(){
		String result = "";
		for(Line line : content){
			result = result + line.getContent() + "\n";
		}
		return result;
	}
}
