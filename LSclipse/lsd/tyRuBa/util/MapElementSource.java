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

import java.util.NoSuchElementException;

/** A map ElementSource is an elementSource that walks through another elementSource and
 * computes elements for another elementSource through an action.
 * The resulting elementSource consists of the non null results of the action aplied to the
 * input elementSource */
final class MapElementSource extends ElementSource {
  
  private Object next = null;
  private ElementSource remaining;
  private Action action;

  public MapElementSource(ElementSource on, Action what) {
    action = what;
    remaining = on;
  }

  public int status() {
//System.err.println("before advance");
    if (next==null)
      advance();
//System.err.println("after advance");
    if (next==null) {
		if (remaining==null)
			return NO_MORE_ELEMENTS;
		else {
			int result = remaining.status();
			return result;
		}
    }
    else
      return ELEMENT_READY;
  }

  public Object nextElement() {
    if (status()==ELEMENT_READY) {
      Object theNext = next;
      next = null;
      return theNext;
    }
    else
      throw new NoSuchElementException("MapElementSource");
  }

	public void print(PrintingState p) {
		p.print("Map(");
		p.indent();
			if (next!=null) {
				p.print("ready="+next+" ");
			}
			p.print("Action= "+action.toString());p.newline();
			p.print("on =");			
			p.indent();
				if (remaining==null)
					p.print("null");
				else
					remaining.print(p);
			p.outdent();
		p.outdent();
		p.print(")");
	}

  private int advance() {
    int result = ELEMENT_READY; //if the while below is not executed.
    next = null; //Clear next element and try to get a new one
//System.err.println("MapElementSource::advance():1");
    while ( (next==null) && (result=remaining.status())==ELEMENT_READY) {
//System.err.println("MapElementSource::advance():2");
      next = action.compute(remaining.nextElement());
    }
//System.err.println("MapElementSource::advance():3");
	if (result==NO_MORE_ELEMENTS) {
		remaining = ElementSource.theEmpty;
	}
    return result;
  }
  
  public boolean isEmpty() {
  	return (next == null) && (remaining.isEmpty());	
  }
  
  public ElementSource first() {
  	if (next!=null) {
  		return ElementSource.singleton(next);
  	}
  	else
  		return remaining.first().map(action);
  }

	public void release() {
		super.release();
		next = null;
		action = null;
		if (remaining!=null) {
			remaining.release();
			remaining = null;
		}
	}

}
