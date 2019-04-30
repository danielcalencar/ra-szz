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
 * Created on Oct 3, 2003
 */
package tyRuBa.util;

import tyRuBa.engine.RuleBase;
import junit.framework.Assert;

public final class SynchPolicy {
	
	SynchResource resource;

	public SynchPolicy(SynchResource res) {
		resource = res;
	}

	//TODO: This is not static and not public
	int stopSources = 0;
	int busySources = 0;

	public void sourceDone() {
		synchronized(resource) {
			busySources--;
			debug_message("--");
			Assert.assertTrue(busySources>=0);
			//Assert.assertTrue(busySources>0 ||
			//        ResultSetElementSource.active.size()==0);
			if (busySources==0)
				resource.notifyAll();
		}
	}

	public void newSource() {
		while (stopSources>0)
			try {
				resource.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		busySources++;
		debug_message("++");
	}
	
	public void stopSources() {
		final long initWaitTime = 100;
		long waitTime = initWaitTime;
		if (Aurelizer.debug_sounds!=null)
			Aurelizer.debug_sounds.enter_loop("temporizing");
		try {
			synchronized (resource) {
				stopSources++;
				debug_message("stop");
				while (busySources>0) {
					try {
						resource.wait(waitTime);
						if (waitTime>initWaitTime) {
							System.gc();
						}
						waitTime *= 2;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (busySources>0 && waitTime>33000) {
						stopSources --;
						if (Aurelizer.debug_sounds!=null)
							Aurelizer.debug_sounds.enter("error");
						throw new Error("I've lost my patience waiting for all queries to be released");
					}
				}
			}
		}
		finally {
			if (Aurelizer.debug_sounds!=null)
				Aurelizer.debug_sounds.exit("temporizing");
		}
	}

	private void debug_message(String msg) {
		if (RuleBase.debug_tracing)
			System.err.println("SynchPolicy "+msg+": "+this);
	}

	public String toString() {
		return "SynchPolicy(busy="+busySources+",stop="+stopSources+")";
	}

	public void allowSources() {
		synchronized (resource) {
			Assert.assertTrue(stopSources>0);
			stopSources--;
			debug_message("allow");
			resource.notifyAll();
		}
	}

}
