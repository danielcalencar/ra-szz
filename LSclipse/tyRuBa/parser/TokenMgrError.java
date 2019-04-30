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

public class TokenMgrError
  extends Error
{
  static final int LEXICAL_ERROR = 0;
  static final int STATIC_LEXER_ERROR = 1;
  static final int INVALID_LEXICAL_STATE = 2;
  static final int LOOP_DETECTED = 3;
  int errorCode;
  
  protected static final String addEscapes(String str)
  {
    StringBuffer retval = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      switch (str.charAt(i))
      {
      case '\000': 
        break;
      case '\b': 
        retval.append("\\b");
        break;
      case '\t': 
        retval.append("\\t");
        break;
      case '\n': 
        retval.append("\\n");
        break;
      case '\f': 
        retval.append("\\f");
        break;
      case '\r': 
        retval.append("\\r");
        break;
      case '"': 
        retval.append("\\\"");
        break;
      case '\'': 
        retval.append("\\'");
        break;
      case '\\': 
        retval.append("\\\\");
        break;
      default: 
        char ch;
        if (((ch = str.charAt(i)) < ' ') || (ch > '~'))
        {
          String s = "0000" + Integer.toString(ch, 16);
          retval.append("\\u" + s.substring(s.length() - 4, s.length()));
        }
        else
        {
          retval.append(ch);
        }
        break;
      }
    }
    return retval.toString();
  }
  
  protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar)
  {
    return 
    
      "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: " + (EOFSeen ? "<EOF> " : new StringBuilder("\"").append(addEscapes(String.valueOf(curChar))).append("\"").append(" (").append(curChar).append("), ").toString()) + "after : \"" + addEscapes(errorAfter) + "\"";
  }
  
  public String getMessage()
  {
    return super.getMessage();
  }
  
  public TokenMgrError() {}
  
  public TokenMgrError(String message, int reason)
  {
    super(message);
    this.errorCode = reason;
  }
  
  public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason)
  {
    this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
  }
}
