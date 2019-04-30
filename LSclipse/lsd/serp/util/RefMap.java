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
 *	<p>Interface implemented by maps that support weak or soft references for
 *	their keys or values.</p>
 *
 *	@author		Abe White
 */
public interface RefMap
	extends Map
{
	/**
	 *	Harden the reference for the given key.  This will ensure that the
	 *	key and the value it corresponds to are not garbage collected.
	 *	Note that this is a mutator method and can result in 
	 *	{@link ConcurrentModificationException}s being thrown by any
	 *	iterator in use while this method is called.
	 *
	 *	@return		true if the reference to the key is now hard; false if the
	 *				key does not exist in the map (or has already expired)
	 */
	public boolean makeHard (Object key);	


	/**
	 *	Soften the reference for the given key.  This will allow the key and
	 *	the value it corresponds to can be expired from the map, and the
	 *	key/value garbage collected.  This is the default for all new key/
	 *	value pairs added to the map.
	 *	Note that this is a mutator method and can result in 
	 *	{@link ConcurrentModificationException}s being thrown by any
	 *	iterator in use while this method is called.
	 *
	 *	@return		true if the reference to the key/value is now soft; false
	 *				if the key does not exist or the key/value cannot be
	 *				maintained in a reference (as for nuill values)
	 */
	public boolean makeReference (Object key);
}
