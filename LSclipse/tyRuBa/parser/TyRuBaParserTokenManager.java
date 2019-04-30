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

import java.io.IOException;
import java.io.PrintStream;

public class TyRuBaParserTokenManager
  implements TyRuBaParserConstants
{
  int nestedBraces = 0;
  public PrintStream debugStream = System.out;
  
  public void setDebugStream(PrintStream ds)
  {
    this.debugStream = ds;
  }
  
  private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
  {
    switch (pos)
    {
    case 0: 
      if ((active0 & 0x20000000000000) != 0L) {
        return 65;
      }
      if ((active0 & 0x390004000000000) != 0L) {
        return 28;
      }
      if ((active0 & 0x3FFFFF0000) != 0L)
      {
        this.jjmatchedKind = 60;
        return 30;
      }
      if ((active0 & 0x400000000E000) != 0L) {
        return 36;
      }
      if ((active0 & 0x8000000000) != 0L)
      {
        this.jjmatchedKind = 58;
        return 28;
      }
      if ((active0 & 0x2000000000000) != 0L) {
        return 5;
      }
      return -1;
    case 1: 
      if ((active0 & 0xC000000000) != 0L) {
        return 28;
      }
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 1;
        return 101;
      }
      if ((active0 & 0x2FF6FF0000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 1;
        return 30;
      }
      if ((active0 & 0x1009000000) != 0L) {
        return 30;
      }
      return -1;
    case 2: 
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 2;
        return 101;
      }
      if ((active0 & 0x2FC6FE0000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 2;
        return 30;
      }
      if ((active0 & 0x30010000) != 0L) {
        return 30;
      }
      return -1;
    case 3: 
      if ((active0 & 0x27C2BE0000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 3;
        return 30;
      }
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 3;
        return 101;
      }
      if ((active0 & 0x804400000) != 0L) {
        return 30;
      }
      return -1;
    case 4: 
      if ((active0 & 0x25423A0000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 4;
        return 30;
      }
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 4;
        return 101;
      }
      if ((active0 & 0x280840000) != 0L) {
        return 30;
      }
      return -1;
    case 5: 
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 5;
        return 101;
      }
      if ((active0 & 0x2440180000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 5;
        return 30;
      }
      if ((active0 & 0x102220000) != 0L) {
        return 30;
      }
      return -1;
    case 6: 
      if ((active0 & 0x2000100000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 6;
        return 30;
      }
      if ((active0 & 0xE000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 6;
        return 101;
      }
      if ((active0 & 0x440080000) != 0L) {
        return 30;
      }
      return -1;
    case 7: 
      if ((active0 & 0x2000000000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 7;
        return 30;
      }
      if ((active0 & 0x8000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 7;
        return 101;
      }
      if ((active0 & 0x6000) != 0L) {
        return 101;
      }
      if ((active0 & 0x100000) != 0L) {
        return 30;
      }
      return -1;
    case 8: 
      if ((active0 & 0x8000) != 0L) {
        return 101;
      }
      if ((active0 & 0x2000000000) != 0L)
      {
        this.jjmatchedKind = 60;
        this.jjmatchedPos = 8;
        return 30;
      }
      return -1;
    }
    return -1;
  }
  
  private final int jjStartNfa_0(int pos, long active0, long active1)
  {
    return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
  }
  
  private final int jjStopAtPos(int pos, int kind)
  {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    return pos + 1;
  }
  
  private final int jjStartNfaWithStates_0(int pos, int kind, int state)
  {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      return pos + 1;
    }
    return jjMoveNfa_0(state, pos + 1);
  }
  
  private final int jjMoveStringLiteralDfa0_0()
  {
    switch (this.curChar)
    {
    case '#': 
      this.jjmatchedKind = 50;
      return jjMoveStringLiteralDfa1_0(57344L);
    case '&': 
      return jjMoveStringLiteralDfa1_0(549755813888L);
    case '(': 
      return jjStopAtPos(0, 41);
    case ')': 
      return jjStopAtPos(0, 42);
    case '*': 
      return jjStartNfaWithStates_0(0, 56, 28);
    case '+': 
      return jjStartNfaWithStates_0(0, 55, 28);
    case ',': 
      return jjStopAtPos(0, 48);
    case '.': 
      return jjStartNfaWithStates_0(0, 49, 5);
    case '/': 
      return jjStartNfaWithStates_0(0, 53, 65);
    case ':': 
      this.jjmatchedKind = 74;
      return jjMoveStringLiteralDfa1_0(18015498021109760L);
    case ';': 
      return jjStopAtPos(0, 47);
    case '<': 
      return jjStopAtPos(0, 45);
    case '=': 
      return jjStartNfaWithStates_0(0, 57, 28);
    case '>': 
      return jjStopAtPos(0, 46);
    case '@': 
      return jjStopAtPos(0, 51);
    case 'A': 
      return jjMoveStringLiteralDfa1_0(134217728L);
    case 'B': 
      return jjMoveStringLiteralDfa1_0(8589934592L);
    case 'C': 
      return jjMoveStringLiteralDfa1_0(1048576L);
    case 'D': 
      return jjMoveStringLiteralDfa1_0(17716740096L);
    case 'E': 
      return jjMoveStringLiteralDfa1_0(268566528L);
    case 'F': 
      return jjMoveStringLiteralDfa1_0(34360262656L);
    case 'I': 
      return jjMoveStringLiteralDfa1_0(16777216L);
    case 'M': 
      return jjMoveStringLiteralDfa1_0(2155872256L);
    case 'N': 
      return jjMoveStringLiteralDfa1_0(4295294976L);
    case 'O': 
      return jjMoveStringLiteralDfa1_0(68719476736L);
    case 'P': 
      return jjMoveStringLiteralDfa1_0(137438953472L);
    case 'R': 
      return jjMoveStringLiteralDfa1_0(33554432L);
    case 'S': 
      return jjMoveStringLiteralDfa1_0(1073741824L);
    case 'T': 
      return jjMoveStringLiteralDfa1_0(71303168L);
    case 'U': 
      return jjMoveStringLiteralDfa1_0(2097152L);
    case '[': 
      return jjStopAtPos(0, 43);
    case ']': 
      return jjStopAtPos(0, 44);
    case '{': 
      return jjStopAtPos(0, 69);
    case '|': 
      this.jjmatchedKind = 52;
      return jjMoveStringLiteralDfa1_0(274877906944L);
    }
    return jjMoveNfa_0(0, 0);
  }
  
  private final int jjMoveStringLiteralDfa1_0(long active0)
  {
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(0, active0, 0L);
      return 1;
    }
    switch (this.curChar)
    {
    case '&': 
      if ((active0 & 0x8000000000) != 0L) {
        return jjStartNfaWithStates_0(1, 39, 28);
      }
      break;
    case '-': 
      if ((active0 & 0x40000000000000) != 0L) {
        return jjStopAtPos(1, 54);
      }
      break;
    case ':': 
      if ((active0 & 0x10000000000) != 0L) {
        return jjStopAtPos(1, 40);
      }
      break;
    case 'A': 
      return jjMoveStringLiteralDfa2_0(active0, 34359738368L);
    case 'E': 
      return jjMoveStringLiteralDfa2_0(active0, 156267184128L);
    case 'F': 
      if ((active0 & 0x1000000000) != 0L) {
        return jjStartNfaWithStates_0(1, 36, 30);
      }
      break;
    case 'I': 
      return jjMoveStringLiteralDfa2_0(active0, 524288L);
    case 'N': 
      return jjMoveStringLiteralDfa2_0(active0, 270532608L);
    case 'O': 
      return jjMoveStringLiteralDfa2_0(active0, 12894666752L);
    case 'S': 
      if ((active0 & 0x1000000) != 0L) {
        return jjStartNfaWithStates_0(1, 24, 30);
      }
      if ((active0 & 0x8000000) != 0L) {
        return jjStartNfaWithStates_0(1, 27, 30);
      }
      break;
    case 'U': 
      return jjMoveStringLiteralDfa2_0(active0, 2147483648L);
    case 'X': 
      return jjMoveStringLiteralDfa2_0(active0, 131072L);
    case 'Y': 
      return jjMoveStringLiteralDfa2_0(active0, 67108864L);
    case 'g': 
      return jjMoveStringLiteralDfa2_0(active0, 32768L);
    case 'i': 
      return jjMoveStringLiteralDfa2_0(active0, 8192L);
    case 'l': 
      return jjMoveStringLiteralDfa2_0(active0, 16384L);
    case '|': 
      if ((active0 & 0x4000000000) != 0L) {
        return jjStartNfaWithStates_0(1, 38, 28);
      }
      break;
    }
    return jjStartNfa_0(0, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(0, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(1, active0, 0L);
      return 2;
    }
    switch (this.curChar)
    {
    case 'A': 
      return jjMoveStringLiteralDfa3_0(active0, 33554432L);
    case 'C': 
      return jjMoveStringLiteralDfa3_0(active0, 34359738368L);
    case 'D': 
      if ((active0 & 0x10000000) != 0L) {
        return jjStartNfaWithStates_0(2, 28, 30);
      }
      return jjMoveStringLiteralDfa3_0(active0, 8650752L);
    case 'F': 
      return jjMoveStringLiteralDfa3_0(active0, 17179869184L);
    case 'I': 
      return jjMoveStringLiteralDfa3_0(active0, 2228224L);
    case 'L': 
      return jjMoveStringLiteralDfa3_0(active0, 2147483648L);
    case 'M': 
      return jjMoveStringLiteralDfa3_0(active0, 1073741824L);
    case 'N': 
      return jjMoveStringLiteralDfa3_0(active0, 4295491584L);
    case 'P': 
      return jjMoveStringLiteralDfa3_0(active0, 67108864L);
    case 'R': 
      return jjMoveStringLiteralDfa3_0(active0, 137438953472L);
    case 'S': 
      return jjMoveStringLiteralDfa3_0(active0, 4194304L);
    case 'T': 
      if ((active0 & 0x10000) != 0L) {
        return jjStartNfaWithStates_0(2, 16, 30);
      }
      if ((active0 & 0x20000000) != 0L) {
        return jjStartNfaWithStates_0(2, 29, 30);
      }
      break;
    case 'U': 
      return jjMoveStringLiteralDfa3_0(active0, 8590983168L);
    case 'e': 
      return jjMoveStringLiteralDfa3_0(active0, 32768L);
    case 'i': 
      return jjMoveStringLiteralDfa3_0(active0, 16384L);
    case 'n': 
      return jjMoveStringLiteralDfa3_0(active0, 8192L);
    }
    return jjStartNfa_0(1, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(1, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(2, active0, 0L);
      return 3;
    }
    switch (this.curChar)
    {
    case 'A': 
      return jjMoveStringLiteralDfa4_0(active0, 17179869184L);
    case 'D': 
      return jjMoveStringLiteralDfa4_0(active0, 4295491584L);
    case 'E': 
      if ((active0 & 0x4000000) != 0L) {
        return jjStartNfaWithStates_0(3, 26, 30);
      }
      return jjMoveStringLiteralDfa4_0(active0, 8388608L);
    case 'I': 
      return jjMoveStringLiteralDfa4_0(active0, 1073741824L);
    case 'L': 
      return jjMoveStringLiteralDfa4_0(active0, 33554432L);
    case 'N': 
      return jjMoveStringLiteralDfa4_0(active0, 8590983168L);
    case 'Q': 
      return jjMoveStringLiteralDfa4_0(active0, 2097152L);
    case 'S': 
      return jjMoveStringLiteralDfa4_0(active0, 137439084544L);
    case 'T': 
      if ((active0 & 0x400000) != 0L) {
        return jjStartNfaWithStates_0(3, 22, 30);
      }
      if ((active0 & 0x800000000) != 0L) {
        return jjStartNfaWithStates_0(3, 35, 30);
      }
      return jjMoveStringLiteralDfa4_0(active0, 2147483648L);
    case 'U': 
      return jjMoveStringLiteralDfa4_0(active0, 262144L);
    case 'b': 
      return jjMoveStringLiteralDfa4_0(active0, 16384L);
    case 'c': 
      return jjMoveStringLiteralDfa4_0(active0, 8192L);
    case 'n': 
      return jjMoveStringLiteralDfa4_0(active0, 32768L);
    }
    return jjStartNfa_0(2, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(2, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(3, active0, 0L);
      return 4;
    }
    switch (this.curChar)
    {
    case 'A': 
      return jjMoveStringLiteralDfa5_0(active0, 524288L);
    case 'D': 
      if ((active0 & 0x200000000) != 0L) {
        return jjStartNfaWithStates_0(4, 33, 30);
      }
      return jjMoveStringLiteralDfa5_0(active0, 1073741824L);
    case 'E': 
      return jjMoveStringLiteralDfa5_0(active0, 4294967296L);
    case 'I': 
      if ((active0 & 0x80000000) != 0L) {
        return jjStartNfaWithStates_0(4, 31, 30);
      }
      return jjMoveStringLiteralDfa5_0(active0, 137438953472L);
    case 'L': 
      return jjMoveStringLiteralDfa5_0(active0, 33554432L);
    case 'P': 
      if ((active0 & 0x40000) != 0L) {
        return jjStartNfaWithStates_0(4, 18, 30);
      }
      break;
    case 'S': 
      if ((active0 & 0x800000) != 0L) {
        return jjStartNfaWithStates_0(4, 23, 30);
      }
      break;
    case 'T': 
      return jjMoveStringLiteralDfa5_0(active0, 1179648L);
    case 'U': 
      return jjMoveStringLiteralDfa5_0(active0, 17181966336L);
    case 'e': 
      return jjMoveStringLiteralDfa5_0(active0, 32768L);
    case 'l': 
      return jjMoveStringLiteralDfa5_0(active0, 8192L);
    case 'r': 
      return jjMoveStringLiteralDfa5_0(active0, 16384L);
    }
    return jjStartNfa_0(3, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(3, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(4, active0, 0L);
      return 5;
    }
    switch (this.curChar)
    {
    case 'A': 
      return jjMoveStringLiteralDfa6_0(active0, 1048576L);
    case 'E': 
      if ((active0 & 0x200000) != 0L) {
        return jjStartNfaWithStates_0(5, 21, 30);
      }
      return jjMoveStringLiteralDfa6_0(active0, 1073741824L);
    case 'L': 
      return jjMoveStringLiteralDfa6_0(active0, 17180393472L);
    case 'S': 
      if ((active0 & 0x20000) != 0L) {
        return jjStartNfaWithStates_0(5, 17, 30);
      }
      return jjMoveStringLiteralDfa6_0(active0, 137438953472L);
    case 'T': 
      if ((active0 & 0x100000000) != 0L) {
        return jjStartNfaWithStates_0(5, 32, 30);
      }
      break;
    case 'Y': 
      if ((active0 & 0x2000000) != 0L) {
        return jjStartNfaWithStates_0(5, 25, 30);
      }
      break;
    case 'a': 
      return jjMoveStringLiteralDfa6_0(active0, 16384L);
    case 'r': 
      return jjMoveStringLiteralDfa6_0(active0, 32768L);
    case 'u': 
      return jjMoveStringLiteralDfa6_0(active0, 8192L);
    }
    return jjStartNfa_0(4, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(4, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(5, active0, 0L);
      return 6;
    }
    switch (this.curChar)
    {
    case 'L': 
      if ((active0 & 0x80000) != 0L) {
        return jjStartNfaWithStates_0(6, 19, 30);
      }
      return jjMoveStringLiteralDfa7_0(active0, 1048576L);
    case 'T': 
      if ((active0 & 0x40000000) != 0L) {
        return jjStartNfaWithStates_0(6, 30, 30);
      }
      if ((active0 & 0x400000000) != 0L) {
        return jjStartNfaWithStates_0(6, 34, 30);
      }
      return jjMoveStringLiteralDfa7_0(active0, 137438953472L);
    case 'a': 
      return jjMoveStringLiteralDfa7_0(active0, 32768L);
    case 'd': 
      return jjMoveStringLiteralDfa7_0(active0, 8192L);
    case 'r': 
      return jjMoveStringLiteralDfa7_0(active0, 16384L);
    }
    return jjStartNfa_0(5, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(5, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(6, active0, 0L);
      return 7;
    }
    switch (this.curChar)
    {
    case 'E': 
      return jjMoveStringLiteralDfa8_0(active0, 137438953472L);
    case 'L': 
      if ((active0 & 0x100000) != 0L) {
        return jjStartNfaWithStates_0(7, 20, 30);
      }
      break;
    case 'e': 
      if ((active0 & 0x2000) != 0L) {
        return jjStartNfaWithStates_0(7, 13, 101);
      }
      break;
    case 't': 
      return jjMoveStringLiteralDfa8_0(active0, 32768L);
    case 'y': 
      if ((active0 & 0x4000) != 0L) {
        return jjStartNfaWithStates_0(7, 14, 101);
      }
      break;
    }
    return jjStartNfa_0(6, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(6, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(7, active0, 0L);
      return 8;
    }
    switch (this.curChar)
    {
    case 'N': 
      return jjMoveStringLiteralDfa9_0(active0, 137438953472L);
    case 'e': 
      if ((active0 & 0x8000) != 0L) {
        return jjStartNfaWithStates_0(8, 15, 101);
      }
      break;
    }
    return jjStartNfa_0(7, active0, 0L);
  }
  
  private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
  {
    if ((active0 &= old0) == 0L) {
      return jjStartNfa_0(7, old0, 0L);
    }
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      jjStopStringLiteralDfa_0(8, active0, 0L);
      return 9;
    }
    switch (this.curChar)
    {
    case 'T': 
      if ((active0 & 0x2000000000) != 0L) {
        return jjStartNfaWithStates_0(9, 37, 30);
      }
      break;
    }
    return jjStartNfa_0(8, active0, 0L);
  }
  
  private final void jjCheckNAdd(int state)
  {
    if (this.jjrounds[state] != this.jjround)
    {
      this.jjstateSet[(this.jjnewStateCnt++)] = state;
      this.jjrounds[state] = this.jjround;
    }
  }
  
  private final void jjAddStates(int start, int end)
  {
    do
    {
      this.jjstateSet[(this.jjnewStateCnt++)] = jjnextStates[start];
    } while (start++ != end);
  }
  
  private final void jjCheckNAddTwoStates(int state1, int state2)
  {
    jjCheckNAdd(state1);
    jjCheckNAdd(state2);
  }
  
  private final void jjCheckNAddStates(int start, int end)
  {
    do
    {
      jjCheckNAdd(jjnextStates[start]);
    } while (start++ != end);
  }
  
  private final void jjCheckNAddStates(int start)
  {
    jjCheckNAdd(jjnextStates[start]);
    jjCheckNAdd(jjnextStates[(start + 1)]);
  }
  
  static final long[] jjbitVec0 = {
    -2L, -1L, -1L, -1L };
  static final long[] jjbitVec2 = {
    0, 0, -1L, -1L };
  static final long[] jjbitVec3 = {
    2301339413881290750L, -16384L, 4294967295L, 432345564227567616L };
  static final long[] jjbitVec4 = {
    000-36028797027352577L };
  static final long[] jjbitVec5 = {
    0, -1L, -1L, -1L };
  static final long[] jjbitVec6 = {
    -1L, -1L, 65535L };
  static final long[] jjbitVec7 = {
    -1L, -1L };
  static final long[] jjbitVec8 = {
    70368744177663L };
  
  private final int jjMoveNfa_0(int startState, int curPos)
  {
    int startsAt = 0;
    this.jjnewStateCnt = 101;
    int i = 1;
    this.jjstateSet[0] = startState;
    int kind = Integer.MAX_VALUE;
    for (;;)
    {
      if (++this.jjround == Integer.MAX_VALUE) {
        ReInitRounds();
      }
      if (this.curChar < '@')
      {
        long l = 1L << this.curChar;
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 36: 
            if ((0x3FF4C1800000000 & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            if ((0x20002C7A00000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            if ((0x1800000000 & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 101: 
            if ((0x3FF4C1800000000 & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            if ((0x3FF4C1800000000 & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 65: 
            if ((0xFFFF7FFFFFFFFFFF & l) != 0L) {
              jjCheckNAddStates(0, 2);
            } else if (this.curChar == '/') {
              if (kind > 64) {
                kind = 64;
              }
            }
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(95, 96);
            } else if (this.curChar == '/') {
              jjCheckNAddStates(3, 5);
            }
            if (this.curChar == '*') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 87;
            } else if (this.curChar == '/') {
              jjCheckNAddStates(6, 8);
            }
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(78, 79);
            }
            if (this.curChar == '*') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 70;
            }
            break;
          case 0: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              jjCheckNAddStates(9, 15);
            }
            else if ((0x20002C7A00000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            else if ((0x100003600 & l) != 0L)
            {
              if (kind > 1) {
                kind = 1;
              }
            }
            else if (this.curChar == '/')
            {
              jjAddStates(16, 21);
            }
            else if (this.curChar == '\'')
            {
              jjCheckNAddStates(22, 24);
            }
            else if (this.curChar == '"')
            {
              jjCheckNAddStates(25, 27);
            }
            else if (this.curChar == '.')
            {
              jjCheckNAdd(5);
            }
            else if (this.curChar == '?')
            {
              if (kind > 61) {
                kind = 61;
              }
              jjCheckNAdd(32);
            }
            if ((0x3FE000000000000 & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(2, 3);
            }
            else if ((0x1800000000 & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            else if (this.curChar == '-')
            {
              jjAddStates(28, 30);
            }
            else if (this.curChar == '0')
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddStates(31, 33);
            }
            else if (this.curChar == '/')
            {
              jjCheckNAddStates(0, 2);
            }
            else if (this.curChar == '!')
            {
              if (kind > 62) {
                kind = 62;
              }
              jjCheckNAdd(34);
            }
            if (this.curChar == '#') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 36;
            }
            break;
          case 1: 
            if ((0x3FE000000000000 & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(2, 3);
            }
            break;
          case 2: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(2, 3);
            }
            break;
          case 4: 
            if (this.curChar == '.') {
              jjCheckNAdd(5);
            }
            break;
          case 5: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddStates(34, 36);
            }
            break;
          case 7: 
            if ((0x280000000000 & l) != 0L) {
              jjCheckNAdd(8);
            }
            break;
          case 8: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddTwoStates(8, 9);
            }
            break;
          case 10: 
            if (this.curChar == '"') {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 11: 
            if ((0xFFFFFFFBFFFFDBFF & l) != 0L) {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 13: 
            if ((0x8400000000 & l) != 0L) {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 14: 
            if ((this.curChar == '"') && (kind > 12)) {
              kind = 12;
            }
            break;
          case 15: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAddStates(37, 40);
            }
            break;
          case 16: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 17: 
            if ((0xF000000000000 & l) != 0L) {
              this.jjstateSet[(this.jjnewStateCnt++)] = 18;
            }
            break;
          case 18: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAdd(16);
            }
            break;
          case 19: 
            if (this.curChar == '\'') {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 20: 
            if ((0xFFFFFF7FFFFFDBFF & l) != 0L) {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 22: 
            if ((0x8400000000 & l) != 0L) {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 23: 
            if ((this.curChar == '\'') && (kind > 12)) {
              kind = 12;
            }
            break;
          case 24: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAddStates(41, 44);
            }
            break;
          case 25: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 26: 
            if ((0xF000000000000 & l) != 0L) {
              this.jjstateSet[(this.jjnewStateCnt++)] = 27;
            }
            break;
          case 27: 
            if ((0xFF000000000000 & l) != 0L) {
              jjCheckNAdd(25);
            }
            break;
          case 28: 
            if ((0x20002C7A00000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            break;
          case 29: 
            if ((0x1800000000 & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 30: 
            if ((0x3FF4C1800000000 & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 31: 
            if (this.curChar == '?')
            {
              if (kind > 61) {
                kind = 61;
              }
              jjCheckNAdd(32);
            }
            break;
          case 32: 
            if ((0x3FF001000000000 & l) != 0L)
            {
              if (kind > 61) {
                kind = 61;
              }
              jjCheckNAdd(32);
            }
            break;
          case 33: 
            if (this.curChar == '!')
            {
              if (kind > 62) {
                kind = 62;
              }
              jjCheckNAdd(34);
            }
            break;
          case 34: 
            if ((0x3FF001000000000 & l) != 0L)
            {
              if (kind > 62) {
                kind = 62;
              }
              jjCheckNAdd(34);
            }
            break;
          case 35: 
            if (this.curChar == '#') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 36;
            }
            break;
          case 37: 
            if ((0x3FF4C1800000000 & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 40: 
          case 42: 
            if (this.curChar == '/') {
              jjCheckNAddStates(0, 2);
            }
            break;
          case 41: 
            if ((0xFFFF7FFFFFFFFFFF & l) != 0L) {
              jjCheckNAddStates(0, 2);
            }
            break;
          case 44: 
            if ((this.curChar == '/') && (kind > 64)) {
              kind = 64;
            }
            break;
          case 45: 
            if ((0x3FF000000000000 & l) != 0L) {
              jjCheckNAddStates(9, 15);
            }
            break;
          case 46: 
            if ((0x3FF000000000000 & l) != 0L) {
              jjCheckNAddTwoStates(46, 47);
            }
            break;
          case 47: 
            if (this.curChar == '.')
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddStates(45, 47);
            }
            break;
          case 48: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddStates(45, 47);
            }
            break;
          case 50: 
            if ((0x280000000000 & l) != 0L) {
              jjCheckNAdd(51);
            }
            break;
          case 51: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddTwoStates(51, 9);
            }
            break;
          case 52: 
            if ((0x3FF000000000000 & l) != 0L) {
              jjCheckNAddTwoStates(52, 53);
            }
            break;
          case 54: 
            if ((0x280000000000 & l) != 0L) {
              jjCheckNAdd(55);
            }
            break;
          case 55: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 10) {
                kind = 10;
              }
              jjCheckNAddTwoStates(55, 9);
            }
            break;
          case 56: 
            if ((0x3FF000000000000 & l) != 0L) {
              jjCheckNAddStates(48, 50);
            }
            break;
          case 58: 
            if ((0x280000000000 & l) != 0L) {
              jjCheckNAdd(59);
            }
            break;
          case 59: 
            if ((0x3FF000000000000 & l) != 0L) {
              jjCheckNAddTwoStates(59, 9);
            }
            break;
          case 60: 
            if (this.curChar == '0')
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddStates(31, 33);
            }
            break;
          case 62: 
            if ((0x3FF000000000000 & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(62, 3);
            }
            break;
          case 63: 
            if ((0xFF000000000000 & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(63, 3);
            }
            break;
          case 64: 
            if (this.curChar == '/') {
              jjAddStates(16, 21);
            }
            break;
          case 66: 
            if ((0xFFFFFFFFFFFFDBFF & l) != 0L) {
              jjCheckNAddStates(6, 8);
            }
            break;
          case 67: 
            if (((0x2400 & l) != 0L) && (kind > 2)) {
              kind = 2;
            }
            break;
          case 68: 
            if ((this.curChar == '\n') && (kind > 2)) {
              kind = 2;
            }
            break;
          case 69: 
            if (this.curChar == '\r') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 68;
            }
            break;
          case 70: 
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(71, 72);
            }
            break;
          case 71: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(71, 72);
            }
            break;
          case 72: 
            if (this.curChar == '*') {
              jjCheckNAddStates(51, 53);
            }
            break;
          case 73: 
            if ((0xFFFF7BFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(74, 72);
            }
            break;
          case 74: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(74, 72);
            }
            break;
          case 75: 
            if ((this.curChar == '/') && (kind > 2)) {
              kind = 2;
            }
            break;
          case 76: 
            if (this.curChar == '*') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 70;
            }
            break;
          case 77: 
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(78, 79);
            }
            break;
          case 78: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(78, 79);
            }
            break;
          case 79: 
            if (this.curChar == '*') {
              jjCheckNAddStates(54, 56);
            }
            break;
          case 80: 
            if ((0xFFFF7BFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(81, 79);
            }
            break;
          case 81: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(81, 79);
            }
            break;
          case 82: 
            if (this.curChar == '/') {
              jjCheckNAddStates(3, 5);
            }
            break;
          case 83: 
            if ((0xFFFFFFFFFFFFDBFF & l) != 0L) {
              jjCheckNAddStates(3, 5);
            }
            break;
          case 84: 
            if (((0x2400 & l) != 0L) && (kind > 3)) {
              kind = 3;
            }
            break;
          case 85: 
            if ((this.curChar == '\n') && (kind > 3)) {
              kind = 3;
            }
            break;
          case 86: 
            if (this.curChar == '\r') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 85;
            }
            break;
          case 87: 
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(88, 89);
            }
            break;
          case 88: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(88, 89);
            }
            break;
          case 89: 
            if (this.curChar == '*') {
              jjCheckNAddStates(57, 59);
            }
            break;
          case 90: 
            if ((0xFFFF7BFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(91, 89);
            }
            break;
          case 91: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(91, 89);
            }
            break;
          case 92: 
            if ((this.curChar == '/') && (kind > 4)) {
              kind = 4;
            }
            break;
          case 93: 
            if (this.curChar == '*') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 87;
            }
            break;
          case 94: 
            if (this.curChar == '*') {
              jjCheckNAddTwoStates(95, 96);
            }
            break;
          case 95: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(95, 96);
            }
            break;
          case 96: 
            if (this.curChar == '*') {
              jjCheckNAddStates(60, 62);
            }
            break;
          case 97: 
            if ((0xFFFF7BFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(98, 96);
            }
            break;
          case 98: 
            if ((0xFFFFFBFFFFFFFFFF & l) != 0L) {
              jjCheckNAddTwoStates(98, 96);
            }
            break;
          case 99: 
            if ((this.curChar == '/') && (kind > 5)) {
              kind = 5;
            }
            break;
          case 100: 
            if (this.curChar == '-') {
              jjAddStates(28, 30);
            }
            break;
          }
        } while (i != startsAt);
      }
      else if (this.curChar < '')
      {
        long l = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 36: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            else if ((0x1000000040000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 101: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            else if (this.curChar == '[')
            {
              this.jjstateSet[(this.jjnewStateCnt++)] = 38;
            }
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 65: 
            jjCheckNAddStates(0, 2);
            if (this.curChar == '\\') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 42;
            }
            break;
          case 0: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            else if ((0x1000000040000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            break;
          case 3: 
            if (((0x100000001000 & l) != 0L) && (kind > 6)) {
              kind = 6;
            }
            break;
          case 6: 
            if ((0x2000000020 & l) != 0L) {
              jjAddStates(63, 64);
            }
            break;
          case 9: 
            if (((0x5000000050 & l) != 0L) && (kind > 10)) {
              kind = 10;
            }
            break;
          case 11: 
            if ((0xFFFFFFFFEFFFFFFF & l) != 0L) {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 12: 
            if (this.curChar == '\\') {
              jjAddStates(65, 67);
            }
            break;
          case 13: 
            if ((0x14404410000000 & l) != 0L) {
              jjCheckNAddStates(25, 27);
            }
            break;
          case 20: 
            if ((0xFFFFFFFFEFFFFFFF & l) != 0L) {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 21: 
            if (this.curChar == '\\') {
              jjAddStates(68, 70);
            }
            break;
          case 22: 
            if ((0x14404410000000 & l) != 0L) {
              jjCheckNAddStates(22, 24);
            }
            break;
          case 28: 
            if ((0x1000000040000000 & l) != 0L)
            {
              if (kind > 58) {
                kind = 58;
              }
              jjCheckNAdd(28);
            }
            break;
          case 29: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 30: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 32: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 61) {
                kind = 61;
              }
              this.jjstateSet[(this.jjnewStateCnt++)] = 32;
            }
            break;
          case 34: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 62) {
                kind = 62;
              }
              this.jjstateSet[(this.jjnewStateCnt++)] = 34;
            }
            break;
          case 37: 
            if ((0x7FFFFFE87FFFFFE & l) != 0L)
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 38: 
            if ((this.curChar == ']') && (kind > 63)) {
              kind = 63;
            }
            break;
          case 39: 
            if (this.curChar == '[') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 38;
            }
            break;
          case 41: 
            jjCheckNAddStates(0, 2);
            break;
          case 43: 
            if (this.curChar == '\\') {
              this.jjstateSet[(this.jjnewStateCnt++)] = 42;
            }
            break;
          case 49: 
            if ((0x2000000020 & l) != 0L) {
              jjAddStates(71, 72);
            }
            break;
          case 53: 
            if ((0x2000000020 & l) != 0L) {
              jjAddStates(73, 74);
            }
            break;
          case 57: 
            if ((0x2000000020 & l) != 0L) {
              jjAddStates(75, 76);
            }
            break;
          case 61: 
            if ((0x100000001000000 & l) != 0L) {
              jjCheckNAdd(62);
            }
            break;
          case 62: 
            if ((0x7E0000007E & l) != 0L)
            {
              if (kind > 6) {
                kind = 6;
              }
              jjCheckNAddTwoStates(62, 3);
            }
            break;
          case 66: 
            jjAddStates(6, 8);
            break;
          case 71: 
            jjCheckNAddTwoStates(71, 72);
            break;
          case 73: 
          case 74: 
            jjCheckNAddTwoStates(74, 72);
            break;
          case 78: 
            jjCheckNAddTwoStates(78, 79);
            break;
          case 80: 
          case 81: 
            jjCheckNAddTwoStates(81, 79);
            break;
          case 83: 
            jjAddStates(3, 5);
            break;
          case 88: 
            jjCheckNAddTwoStates(88, 89);
            break;
          case 90: 
          case 91: 
            jjCheckNAddTwoStates(91, 89);
            break;
          case 95: 
            jjCheckNAddTwoStates(95, 96);
            break;
          case 97: 
          case 98: 
            jjCheckNAddTwoStates(98, 96);
          }
        } while (i != startsAt);
      }
      else
      {
        int hiByte = this.curChar >> '\b';
        int i1 = hiByte >> 6;
        long l1 = 1L << (hiByte & 0x3F);
        int i2 = (this.curChar & 0xFF) >> '\006';
        long l2 = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 36: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 101: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 41: 
          case 65: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddStates(0, 2);
            }
            break;
          case 0: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 11: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjAddStates(25, 27);
            }
            break;
          case 20: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjAddStates(22, 24);
            }
            break;
          case 30: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 60) {
                kind = 60;
              }
              jjCheckNAdd(30);
            }
            break;
          case 32: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 61) {
                kind = 61;
              }
              this.jjstateSet[(this.jjnewStateCnt++)] = 32;
            }
            break;
          case 34: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 62) {
                kind = 62;
              }
              this.jjstateSet[(this.jjnewStateCnt++)] = 34;
            }
            break;
          case 37: 
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 63) {
                kind = 63;
              }
              jjCheckNAddTwoStates(37, 39);
            }
            break;
          case 66: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjAddStates(6, 8);
            }
            break;
          case 71: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(71, 72);
            }
            break;
          case 73: 
          case 74: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(74, 72);
            }
            break;
          case 78: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(78, 79);
            }
            break;
          case 80: 
          case 81: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(81, 79);
            }
            break;
          case 83: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjAddStates(3, 5);
            }
            break;
          case 88: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(88, 89);
            }
            break;
          case 90: 
          case 91: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(91, 89);
            }
            break;
          case 95: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(95, 96);
            }
            break;
          case 97: 
          case 98: 
            if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
              jjCheckNAddTwoStates(98, 96);
            }
            break;
          }
        } while (i != startsAt);
      }
      if (kind != Integer.MAX_VALUE)
      {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = curPos;
        kind = Integer.MAX_VALUE;
      }
      curPos++;
      if ((i = this.jjnewStateCnt) == (startsAt = 101 - (this.jjnewStateCnt = startsAt))) {
        return curPos;
      }
      try
      {
        this.curChar = this.input_stream.readChar();
      }
      catch (IOException localIOException) {}
    }
    return curPos;
  }
  
  private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1)
  {
    return -1;
  }
  
  private final int jjStartNfa_1(int pos, long active0, long active1)
  {
    return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
  }
  
  private final int jjStartNfaWithStates_1(int pos, int kind, int state)
  {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      return pos + 1;
    }
    return jjMoveNfa_1(state, pos + 1);
  }
  
  private final int jjMoveStringLiteralDfa0_1()
  {
    switch (this.curChar)
    {
    case '{': 
      return jjStopAtPos(0, 71);
    case '}': 
      return jjStopAtPos(0, 73);
    }
    return jjMoveNfa_1(0, 0);
  }
  
  private final int jjMoveNfa_1(int startState, int curPos)
  {
    int startsAt = 0;
    this.jjnewStateCnt = 1;
    int i = 1;
    this.jjstateSet[0] = startState;
    int kind = Integer.MAX_VALUE;
    for (;;)
    {
      if (++this.jjround == Integer.MAX_VALUE) {
        ReInitRounds();
      }
      if (this.curChar < '@')
      {
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            kind = 70;
          }
        } while (i != startsAt);
      }
      else if (this.curChar < '')
      {
        long l = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            if ((0xD7FFFFFFFFFFFFFF & l) != 0L) {
              kind = 70;
            }
            break;
          }
        } while (i != startsAt);
      }
      else
      {
        int hiByte = this.curChar >> '\b';
        int i1 = hiByte >> 6;
        long l1 = 1L << (hiByte & 0x3F);
        int i2 = (this.curChar & 0xFF) >> '\006';
        long l2 = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            if ((jjCanMove_0(hiByte, i1, i2, l1, l2)) && (kind > 70)) {
              kind = 70;
            }
            break;
          }
        } while (i != startsAt);
      }
      if (kind != Integer.MAX_VALUE)
      {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = curPos;
        kind = Integer.MAX_VALUE;
      }
      curPos++;
      if ((i = this.jjnewStateCnt) == (startsAt = 1 - (this.jjnewStateCnt = startsAt))) {
        return curPos;
      }
      try
      {
        this.curChar = this.input_stream.readChar();
      }
      catch (IOException localIOException) {}
    }
    return curPos;
  }
  
  private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1)
  {
    return -1;
  }
  
  private final int jjStartNfa_2(int pos, long active0, long active1)
  {
    return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
  }
  
  private final int jjStartNfaWithStates_2(int pos, int kind, int state)
  {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    try
    {
      this.curChar = this.input_stream.readChar();
    }
    catch (IOException localIOException)
    {
      return pos + 1;
    }
    return jjMoveNfa_2(state, pos + 1);
  }
  
  private final int jjMoveStringLiteralDfa0_2()
  {
    switch (this.curChar)
    {
    case '{': 
      return jjStopAtPos(0, 71);
    case '}': 
      return jjStopAtPos(0, 72);
    }
    return jjMoveNfa_2(0, 0);
  }
  
  private final int jjMoveNfa_2(int startState, int curPos)
  {
    int startsAt = 0;
    this.jjnewStateCnt = 1;
    int i = 1;
    this.jjstateSet[0] = startState;
    int kind = Integer.MAX_VALUE;
    for (;;)
    {
      if (++this.jjround == Integer.MAX_VALUE) {
        ReInitRounds();
      }
      if (this.curChar < '@')
      {
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            kind = 70;
          }
        } while (i != startsAt);
      }
      else if (this.curChar < '')
      {
        long l = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            if ((0xD7FFFFFFFFFFFFFF & l) != 0L) {
              kind = 70;
            }
            break;
          }
        } while (i != startsAt);
      }
      else
      {
        int hiByte = this.curChar >> '\b';
        int i1 = hiByte >> 6;
        long l1 = 1L << (hiByte & 0x3F);
        int i2 = (this.curChar & 0xFF) >> '\006';
        long l2 = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0: 
            if ((jjCanMove_0(hiByte, i1, i2, l1, l2)) && (kind > 70)) {
              kind = 70;
            }
            break;
          }
        } while (i != startsAt);
      }
      if (kind != Integer.MAX_VALUE)
      {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = curPos;
        kind = Integer.MAX_VALUE;
      }
      curPos++;
      if ((i = this.jjnewStateCnt) == (startsAt = 1 - (this.jjnewStateCnt = startsAt))) {
        return curPos;
      }
      try
      {
        this.curChar = this.input_stream.readChar();
      }
      catch (IOException localIOException) {}
    }
    return curPos;
  }
  
  static final int[] jjnextStates = {
    41, 43, 44, 83, 84, 86, 66, 67, 69, 46, 47, 52, 53, 56, 57, 9, 
    65, 76, 77, 82, 93, 94, 20, 21, 23, 11, 12, 14, 1, 4, 45, 61, 
    63, 3, 5, 6, 9, 11, 12, 16, 14, 20, 21, 25, 23, 48, 49, 9, 
    56, 57, 9, 72, 73, 75, 79, 80, 75, 89, 90, 92, 96, 97, 99, 7, 
    8, 13, 15, 17, 22, 24, 26, 50, 51, 54, 55, 58, 59 };
  
  private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
  {
    switch (hiByte)
    {
    case 0: 
      return (jjbitVec2[i2] & l2) != 0L;
    }
    if ((jjbitVec0[i1] & l1) != 0L) {
      return true;
    }
    return false;
  }
  
  private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
  {
    switch (hiByte)
    {
    case 0: 
      return (jjbitVec4[i2] & l2) != 0L;
    case 48: 
      return (jjbitVec5[i2] & l2) != 0L;
    case 49: 
      return (jjbitVec6[i2] & l2) != 0L;
    case 51: 
      return (jjbitVec7[i2] & l2) != 0L;
    case 61: 
      return (jjbitVec8[i2] & l2) != 0L;
    }
    if ((jjbitVec3[i1] & l1) != 0L) {
      return true;
    }
    return false;
  }
  
  public static final String[] jjstrLiteralImages = {
    "", 
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "#include", "#library", "#generate", 
    "NOT", "EXISTS", "NODUP", 
    "FINDALL", "COUNTALL", "UNIQUE", 
    "TEST", "MODES", "IS", "REALLY", 
    "TYPE", "AS", "END", "DET", "SEMIDET", 
    "MULTI", "NONDET", "BOUND", 
    "DEFAULT", "FACT", "OF", "PERSISTENT", 
    "||", "&&", "::", "(", ")", "[", "]", "<", ">", ";", ",", 
    ".", "#", "@", "|", "/", ":-", "+", "*", "=", 
    0000000000000000":" };
  public static final String[] lexStateNames = {
    "DEFAULT", 
    "inBraces", 
    "inNestedBraces" };
  public static final int[] jjnewLexState = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 2, -1, 0-1 };
  static final long[] jjtoToken = {
    -576460752303426495L, 1537L };
  static final long[] jjtoSkip = {
    62L };
  static final long[] jjtoMore = {
    0480L };
  protected JavaCharStream input_stream;
  private final int[] jjrounds = new int[101];
  private final int[] jjstateSet = new int['Ê'];
  StringBuffer image;
  int jjimageLen;
  int lengthOfMatch;
  protected char curChar;
  
  public TyRuBaParserTokenManager(JavaCharStream stream)
  {
    this.input_stream = stream;
  }
  
  public TyRuBaParserTokenManager(JavaCharStream stream, int lexState)
  {
    this(stream);
    SwitchTo(lexState);
  }
  
  public void ReInit(JavaCharStream stream)
  {
    this.jjmatchedPos = (this.jjnewStateCnt = 0);
    this.curLexState = this.defaultLexState;
    this.input_stream = stream;
    ReInitRounds();
  }
  
  private final void ReInitRounds()
  {
    this.jjround = -2147483647;
    for (int i = 101; i-- > 0;) {
      this.jjrounds[i] = Integer.MIN_VALUE;
    }
  }
  
  public void ReInit(JavaCharStream stream, int lexState)
  {
    ReInit(stream);
    SwitchTo(lexState);
  }
  
  public void SwitchTo(int lexState)
  {
    if ((lexState >= 3) || (lexState < 0)) {
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
    }
    this.curLexState = lexState;
  }
  
  protected Token jjFillToken()
  {
    Token t = Token.newToken(this.jjmatchedKind);
    t.kind = this.jjmatchedKind;
    String im = jjstrLiteralImages[this.jjmatchedKind];
    t.image = (im == null ? this.input_stream.GetImage() : im);
    t.beginLine = this.input_stream.getBeginLine();
    t.beginColumn = this.input_stream.getBeginColumn();
    t.endLine = this.input_stream.getEndLine();
    t.endColumn = this.input_stream.getEndColumn();
    return t;
  }
  
  int curLexState = 0;
  int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;
  
  public Token getNextToken()
  {
    int curPos = 0;
    try
    {
      this.curChar = this.input_stream.BeginToken();
    }
    catch (IOException localIOException1)
    {
      this.jjmatchedKind = 0;
      return jjFillToken();
    }
    this.image = null;
    this.jjimageLen = 0;
    for (;;)
    {
      switch (this.curLexState)
      {
      case 0: 
        this.jjmatchedKind = Integer.MAX_VALUE;
        this.jjmatchedPos = 0;
        curPos = jjMoveStringLiteralDfa0_0();
        break;
      case 1: 
        this.jjmatchedKind = Integer.MAX_VALUE;
        this.jjmatchedPos = 0;
        curPos = jjMoveStringLiteralDfa0_1();
        break;
      case 2: 
        this.jjmatchedKind = Integer.MAX_VALUE;
        this.jjmatchedPos = 0;
        curPos = jjMoveStringLiteralDfa0_2();
      }
      if (this.jjmatchedKind != Integer.MAX_VALUE)
      {
        if (this.jjmatchedPos + 1 < curPos) {
          this.input_stream.backup(curPos - this.jjmatchedPos - 1);
        }
        if ((jjtoToken[(this.jjmatchedKind >> 6)] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
        {
          Token matchedToken = jjFillToken();
          if (jjnewLexState[this.jjmatchedKind] != -1) {
            this.curLexState = jjnewLexState[this.jjmatchedKind];
          }
          return matchedToken;
        }
        if ((jjtoSkip[(this.jjmatchedKind >> 6)] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
        {
          if (jjnewLexState[this.jjmatchedKind] == -1) {
            break;
          }
          this.curLexState = jjnewLexState[this.jjmatchedKind];
          break;
        }
        MoreLexicalActions();
        if (jjnewLexState[this.jjmatchedKind] != -1) {
          this.curLexState = jjnewLexState[this.jjmatchedKind];
        }
        curPos = 0;
        this.jjmatchedKind = Integer.MAX_VALUE;
        try
        {
          this.curChar = this.input_stream.readChar();
        }
        catch (IOException localIOException2) {}
      }
    }
    int error_line = this.input_stream.getEndLine();
    int error_column = this.input_stream.getEndColumn();
    String error_after = null;
    boolean EOFSeen = false;
    try
    {
      this.input_stream.readChar();this.input_stream.backup(1);
    }
    catch (IOException localIOException3)
    {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
      if ((this.curChar == '\n') || (this.curChar == '\r'))
      {
        error_line++;
        error_column = 0;
      }
      else
      {
        error_column++;
      }
    }
    if (!EOFSeen)
    {
      this.input_stream.backup(1);
      error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
    }
    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
  }
  
  void MoreLexicalActions()
  {
    this.jjimageLen += (this.lengthOfMatch = this.jjmatchedPos + 1);
    switch (this.jjmatchedKind)
    {
    case 71: 
      if (this.image == null) {
        this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
      } else {
        this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
      }
      this.jjimageLen = 0;
      this.nestedBraces += 1;
      break;
    case 72: 
      if (this.image == null) {
        this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
      } else {
        this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
      }
      this.jjimageLen = 0;
      this.nestedBraces -= 1;
      if (this.nestedBraces == 0) {
        SwitchTo(1);
      }
      break;
    }
  }
}
