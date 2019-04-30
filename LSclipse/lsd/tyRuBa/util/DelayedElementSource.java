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
/*
 * Created on Mar 29, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tyRuBa.util;

/**
 * @author kdvolder
 */
public abstract class DelayedElementSource extends ElementSource {

	private ElementSource delayed = null;

	private ElementSource delayed() {
		if (delayed==null) {
		    delayed = produce();
		}
		return delayed;
	}

	public int status() {
		return delayed().status();
	}

	/** Overide this method to provide the ElementSource the first time it
	 * is needed.
	 */
	protected abstract ElementSource produce();
	
	public Object nextElement() {
		return delayed().nextElement();
	}

	public void print(PrintingState p) {
		p.print("Delayed("+produceString());
		if (delayed!=null) {
			p.indent();p.newline();
				delayed.print(p);
			p.outdent();
		}
		p.print(")");
	}

	protected abstract String produceString() ;
	
	

    /**
     * @see tyRuBa.util.ElementSource#release()
     */
    public void release() {
        if (delayed != null) {
//            if (delayed instanceof ResultSetElementSource) {
//                System.err.println("[INFO] - release - releasing DelayedElementSource (connected to a ResultSetElementSource)");
//            } else {
//                System.err.println("[INFO] - release - releasing DelayedElementSource");
//            }
            delayed.release();
            delayed = null;
        }
    }
}
