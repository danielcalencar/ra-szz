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

public abstract class DelayedElementSource
  extends ElementSource
{
  private ElementSource delayed = null;
  
  private ElementSource delayed()
  {
    if (this.delayed == null) {
      this.delayed = produce();
    }
    return this.delayed;
  }
  
  public int status()
  {
    return delayed().status();
  }
  
  protected abstract ElementSource produce();
  
  public Object nextElement()
  {
    return delayed().nextElement();
  }
  
  public void print(PrintingState p)
  {
    p.print("Delayed(" + produceString());
    if (this.delayed != null)
    {
      p.indent();p.newline();
      this.delayed.print(p);
      p.outdent();
    }
    p.print(")");
  }
  
  protected abstract String produceString();
  
  public void release()
  {
    if (this.delayed != null)
    {
      this.delayed.release();
      this.delayed = null;
    }
  }
}
