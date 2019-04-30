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

public class ParseException
  extends Exception
{
  protected boolean specialConstructor;
  public Token currentToken;
  public int[][] expectedTokenSequences;
  public String[] tokenImage;
  
  public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal)
  {
    super("");
    this.specialConstructor = true;
    this.currentToken = currentTokenVal;
    this.expectedTokenSequences = expectedTokenSequencesVal;
    this.tokenImage = tokenImageVal;
  }
  
  public ParseException()
  {
    this.specialConstructor = false;
  }
  
  public ParseException(String message)
  {
    super(message);
    this.specialConstructor = false;
  }
  
  public String getMessage()
  {
    if (!this.specialConstructor) {
      return super.getMessage();
    }
    String expected = "";
    int maxSize = 0;
    for (int i = 0; i < this.expectedTokenSequences.length; i++)
    {
      if (maxSize < this.expectedTokenSequences[i].length) {
        maxSize = this.expectedTokenSequences[i].length;
      }
      for (int j = 0; j < this.expectedTokenSequences[i].length; j++) {
        expected = expected + this.tokenImage[this.expectedTokenSequences[i][j]] + " ";
      }
      if (this.expectedTokenSequences[i][(this.expectedTokenSequences[i].length - 1)] != 0) {
        expected = expected + "...";
      }
      expected = expected + this.eol + "    ";
    }
    String retval = "Encountered \"";
    Token tok = this.currentToken.next;
    for (int i = 0; i < maxSize; i++)
    {
      if (i != 0) {
        retval = retval + " ";
      }
      if (tok.kind == 0)
      {
        retval = retval + this.tokenImage[0];
        break;
      }
      retval = retval + add_escapes(tok.image);
      tok = tok.next;
    }
    retval = retval + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn;
    retval = retval + "." + this.eol;
    if (this.expectedTokenSequences.length == 1) {
      retval = retval + "Was expecting:" + this.eol + "    ";
    } else {
      retval = retval + "Was expecting one of:" + this.eol + "    ";
    }
    retval = retval + expected;
    return retval;
  }
  
  protected String eol = System.getProperty("line.separator", "\n");
  
  protected String add_escapes(String str)
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
}
