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
 *	<p>Set implementation that uses any given map for internal storage.  The
 *	set will take on all the properties of the given map.  For example, 
 *	if given an	{@link IdentityMap}, the set will allow two elements 
 *	that are the same according to their {@link Object#equals} method but have 
 *	different JVM identities.  Or if a {@link LinkedHashMap} is used, elements 
 *	will be	kept in insertion order.  The map used must be able to take 
 *	null values.</p>
 *
 *	@author		Abe White
 */
public class MapSet
	extends AbstractSet
{
	Map	_map = null;


	/**
	 *	Equivalent to <code>MapSet (new HashMap ())</code>.
	 *
	 *	@see	#MapSet(Map)
	 */
	public MapSet ()
	{
		this (new HashMap ());
	}


	/**
	 *	Construct a MapSet with the given interal map.  The internal
	 *	map will be cleared.  It should not be accessed in any way after being
 	 *	given to this constructor; this set will 'inherit' its behavior, 
	 *	however.  For example, if the given map is a {@link LinkedHashMap}, 
	 *	the	{@link #iterator} of this set will return values in
 	 *	insertion order.
	 */
	public MapSet (Map map)
	{
		_map = map;
		_map.clear ();
	}


	public int size ()
	{
		return _map.size ();
	}

	
	public boolean add (Object obj)
	{
		if (_map.containsKey (obj))
			return false;

		_map.put (obj, null);
		return true;
	}


	public boolean remove (Object obj)
	{
		boolean contained = _map.containsKey (obj);
		_map.remove (obj);
		return contained;
	}


	public boolean contains (Object obj)
	{
		return _map.containsKey (obj);
	}


	public Iterator iterator ()
	{
		return _map.keySet ().iterator ();
	}


	boolean isIdentity ()
	{
		return _map instanceof IdentityMap;
	}
}
