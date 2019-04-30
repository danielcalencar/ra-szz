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

public class Mutex
{
  private int waiting = -1;
  
  public synchronized void obtain()
  {
    this.waiting += 1;
    if (this.waiting == 0) {
      return;
    }
    try
    {
      wait();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new Error("This should not happen!");
    }
  }
  
  public synchronized void release()
  {
    if (this.waiting > 0) {
      notify();
    }
    this.waiting -= 1;
  }
}
