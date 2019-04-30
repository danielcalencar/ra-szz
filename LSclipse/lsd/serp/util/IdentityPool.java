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
package serp.util;


import java.util.*;


/**
 *	<p>Pool implementation using object identity to maintain unique
 *	instances.  Taken instances are held weakly, so there is not an
 *	absolute requirement that all taken instances be manually returned to the 
 *	pool.</p>
 *	
 *	@author		Abe White
 */
public class IdentityPool
	extends AbstractPool
{
	private Set _free	= new MapSet (new IdentityMap ());
	private Map _taken	= new WeakKeyMap (new IdentityMap ());


	/**
	 *	@see	AbstractPool#AbstractPool()
	 */
	public IdentityPool ()
	{
		super ();
	}


	/**
	 *	@see	AbstractPool#AbstractPool(int,int,int,int)
	 */
	public IdentityPool (int min, int max, int wait, int autoReturn)
	{
		super (min, max, wait, autoReturn);
	}


	/**
	 *	@see	AbstractPool#AbstractPool(Collection)
	 */
	public IdentityPool (Collection c)
	{
		super (c);
	}

	
	protected Set freeSet ()
	{
		return _free;
	}


	protected Map takenMap ()
	{
		return _taken;
	}
}
