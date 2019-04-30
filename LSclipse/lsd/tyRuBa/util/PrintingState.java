/* 
*    Ref-Finder
*    Copyright (C) <2015>  <PLSE_UCLA>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tyRuBa.util;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/*
 * This is a helper class for printing a representation of
 * ElementSources and ElementCollectors for the purpose of debugging.
 */
public class PrintingState {
	
	/** 
	 * Since Collectors can be recursive we must avoid running in circles. Thus
	 * the PrintingState keeps track of where the printing process has visited.
	 */
	Set visited = new HashSet();
	
	int indentationLevel = 0;
	int column = 0;
	
	PrintStream out;

	public PrintingState(PrintStream s) {
		out = s;
	}

	public void print(String o) {
		String s = o.toString();
		column += s.length();
		out.print(s);
	}
	
	void println(String o) {
		print(o);
		newline();
	}

	void newline() {
		out.println();
		for (column = 0; column < indentationLevel; column++) {
			out.print(" ");
		}
	}

	void indent() {
		indentationLevel += 2;
	}

	void outdent() {
		indentationLevel -= 2;
	}

	protected void printObj(Object object) {
		if (object instanceof ElementSource) {
			((ElementSource)object).print(this);
		}
		else
			print(object.toString());
	}

}
