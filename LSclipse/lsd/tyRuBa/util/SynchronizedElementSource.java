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

public class SynchronizedElementSource extends ElementSource {

	//private Throwable createdWhere;

	/** The resource which needs protecting (used as the lock object) */
	private SynchResource resource;

	/** The elementsource that digs up element from the resource */
	private ElementSource src;

	public SynchronizedElementSource(SynchResource resource, ElementSource src) {
//		try {
//			throw new Throwable("Creation of SynchronizedElementSource");
//		}
//		catch (Throwable e) {
//			createdWhere = e;
//		}
		synchronized(resource) {
			resource.getSynchPolicy().newSource();
			this.resource = resource;
			this.src = src;			
		}
	}

	public void print(PrintingState p) {
		p.print("Synchronized(");
		src.print(p);
		p.print(")");
	}

	public int status() {
		if (resource==null)
			return NO_MORE_ELEMENTS;
		else 
		synchronized (resource) {
			int result = src.status();
			if (result==NO_MORE_ELEMENTS) {
			    //System.err.println("[INFO] - status - no more elements");
				release();				
			}
			return result;
		}
	}

	public void release() {
		if (resource!=null) {
		    //TODO: TESTING SOMETHING HERE
			if (src!=null) {
			    //System.err.println("[INFO] - release - releasing the element source protected by this SynchronizedElementSource");
				src.release();
			}
			src = null;
			resource.getSynchPolicy().sourceDone();
			resource = null;
		    
		    //old code
//			resource.getSynchPolicy().sourceDone();
//			resource = null;
//			if (src!=null)
//				src.release();
//			src = null;
		}		
	}

	public Object nextElement() {
		synchronized (resource) {
			return src.nextElement();
		}
	}

	protected void finalize() throws Throwable {
	    //System.err.println("[INFO] - Synchronized Element source finalizer called.");
		try {
			if (resource!=null) {
//				System.err.println("SynchElement was created at...");
//				createdWhere.printStackTrace();
				if (Aurelizer.debug_sounds!=null)
					Aurelizer.debug_sounds.enter("ok");
			}
			release();
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
			if (Aurelizer.debug_sounds!=null)
				Aurelizer.debug_sounds.enter("error");
		}
	}

}
