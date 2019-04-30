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

/**
 * @author cburns
 *
 * This simple class implements a mutex that can be used much more flexibly than synchronized
 * methods and synchronized blocks.
 */
public class Mutex {
	private int waiting = -1;
	
	public synchronized void obtain() {
		
		waiting++;
		if(waiting == 0) {
			//We're here first, can proceed to the protected code
			return;
		} else {
			try {
				this.wait();
			} catch(InterruptedException e) {
				throw new Error("This should not happen!");
			}
		}
	}
	
	public synchronized void release() {
		if(waiting > 0)
			this.notify(); //wake up one thread that's waiting on the mutex
			
		waiting--;
	}
}
