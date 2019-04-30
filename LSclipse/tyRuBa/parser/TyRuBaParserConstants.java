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
package tyRuBa.parser;

public abstract interface TyRuBaParserConstants
{
  public static final int EOF = 0;
  public static final int WHITECHAR = 1;
  public static final int COMMENT = 2;
  public static final int SINGLE_LINE_COMMENT = 3;
  public static final int FORMAL_COMMENT = 4;
  public static final int MULTI_LINE_COMMENT = 5;
  public static final int INTEGER_LITERAL = 6;
  public static final int DECIMAL_LITERAL = 7;
  public static final int HEX_LITERAL = 8;
  public static final int OCTAL_LITERAL = 9;
  public static final int FLOATING_POINT_LITERAL = 10;
  public static final int EXPONENT = 11;
  public static final int STRING_LITERAL = 12;
  public static final int INCLUDE = 13;
  public static final int LIBRARY = 14;
  public static final int GENERATE = 15;
  public static final int NOT = 16;
  public static final int EXISTS = 17;
  public static final int NODUP = 18;
  public static final int FINDALL = 19;
  public static final int COUNTALL = 20;
  public static final int UNIQUE = 21;
  public static final int TEST = 22;
  public static final int MODES = 23;
  public static final int IS = 24;
  public static final int REALLY = 25;
  public static final int TYPEDEF = 26;
  public static final int REPRESENTED_AS = 27;
  public static final int MODEEND = 28;
  public static final int DET = 29;
  public static final int SEMIDET = 30;
  public static final int MULTI = 31;
  public static final int NONDET = 32;
  public static final int BOUND = 33;
  public static final int DEF = 34;
  public static final int FACT = 35;
  public static final int OF = 36;
  public static final int PERSISTENT = 37;
  public static final int OR = 38;
  public static final int AND = 39;
  public static final int TYPE = 40;
  public static final int LPAREN = 41;
  public static final int RPAREN = 42;
  public static final int LBRACKET = 43;
  public static final int RBRACKET = 44;
  public static final int LANGLE = 45;
  public static final int RANGLE = 46;
  public static final int SEMICOLON = 47;
  public static final int COMMA = 48;
  public static final int DOT = 49;
  public static final int HASH = 50;
  public static final int UNQUOTE = 51;
  public static final int VERTSLASH = 52;
  public static final int SLASH = 53;
  public static final int WHEN = 54;
  public static final int PLUS = 55;
  public static final int STAR = 56;
  public static final int STRICT = 57;
  public static final int SPECIAL = 58;
  public static final int SPECIAL_CHAR = 59;
  public static final int IDENTIFIER = 60;
  public static final int VARIABLE = 61;
  public static final int TEMPLATE_VAR = 62;
  public static final int JAVA_CLASS = 63;
  public static final int REGEXP = 64;
  public static final int LOWCASE = 65;
  public static final int UPCASE = 66;
  public static final int LETTER = 67;
  public static final int DIGIT = 68;
  public static final int QUOTEDCODE = 73;
  public static final int DEFAULT = 0;
  public static final int inBraces = 1;
  public static final int inNestedBraces = 2;
  public static final String[] tokenImage = {
    "<EOF>", 
    "<WHITECHAR>", 
    "<COMMENT>", 
    "<SINGLE_LINE_COMMENT>", 
    "<FORMAL_COMMENT>", 
    "<MULTI_LINE_COMMENT>", 
    "<INTEGER_LITERAL>", 
    "<DECIMAL_LITERAL>", 
    "<HEX_LITERAL>", 
    "<OCTAL_LITERAL>", 
    "<FLOATING_POINT_LITERAL>", 
    "<EXPONENT>", 
    "<STRING_LITERAL>", 
    "\"#include\"", 
    "\"#library\"", 
    "\"#generate\"", 
    "\"NOT\"", 
    "\"EXISTS\"", 
    "\"NODUP\"", 
    "\"FINDALL\"", 
    "\"COUNTALL\"", 
    "\"UNIQUE\"", 
    "\"TEST\"", 
    "\"MODES\"", 
    "\"IS\"", 
    "\"REALLY\"", 
    "\"TYPE\"", 
    "\"AS\"", 
    "\"END\"", 
    "\"DET\"", 
    "\"SEMIDET\"", 
    "\"MULTI\"", 
    "\"NONDET\"", 
    "\"BOUND\"", 
    "\"DEFAULT\"", 
    "\"FACT\"", 
    "\"OF\"", 
    "\"PERSISTENT\"", 
    "\"||\"", 
    "\"&&\"", 
    "\"::\"", 
    "\"(\"", 
    "\")\"", 
    "\"[\"", 
    "\"]\"", 
    "\"<\"", 
    "\">\"", 
    "\";\"", 
    "\",\"", 
    "\".\"", 
    "\"#\"", 
    "\"@\"", 
    "\"|\"", 
    "\"/\"", 
    "\":-\"", 
    "\"+\"", 
    "\"*\"", 
    "\"=\"", 
    "<SPECIAL>", 
    "<SPECIAL_CHAR>", 
    "<IDENTIFIER>", 
    "<VARIABLE>", 
    "<TEMPLATE_VAR>", 
    "<JAVA_CLASS>", 
    "<REGEXP>", 
    "<LOWCASE>", 
    "<UPCASE>", 
    "<LETTER>", 
    "<DIGIT>", 
    "\"{\"", 
    "<token of kind 70>", 
    "\"{\"", 
    "\"}\"", 
    "\"}\"", 
    "\":\"" };
}
