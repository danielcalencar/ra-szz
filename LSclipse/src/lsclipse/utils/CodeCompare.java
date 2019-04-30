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
package lsclipse.utils;

import lsclipse.LCS;

public class CodeCompare {
	public static final double SIMILARITY_THRESHOLD = 0.85;
	public static final double DIFFERNCE_THRESHOLD = 0.75;

	// Make sure the longest common string is at least SIMILARITY_THRESHOLD of
	// the shorter code fragment.
	public static boolean compare(String left, String right) {
		String shorter = getShorterString(left, right);
		String lcs = LCS.getLCS(left, right);
		double similarity = (double) lcs.length() / (double) shorter.length();
		if (similarity >= SIMILARITY_THRESHOLD)
			return true;

		return false;
	}

	// Make sure the longest common string is at most SIMILARITY_THRESHOLD of
	// the longer code fragment.
	public static boolean contrast(String left, String right) {
		String longer = getLongerString(left, right);
		String lcs = LCS.getLCS(left, right);
		double similarity = (double) lcs.length() / (double) longer.length();
		if (similarity <= DIFFERNCE_THRESHOLD)
			return true;

		return false;
	}

	private static String getShorterString(String left, String right) {
		if (left.length() < right.length())
			return left;
		return right;
	}

	private static String getLongerString(String left, String right) {
		if (left.length() > right.length())
			return left;
		return right;
	}

}
