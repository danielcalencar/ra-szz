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
 *	<p>Interface implemented by collections that support weak or soft 
 *	references for their values.</p>
 *
 *	@author		Abe White
 */
public interface RefCollection
	extends Collection
{
	/**
	 *	Harden the reference for the given value.  This will ensure that the
	 *	value is not garbage collected.  Note that this is a mutator method 
	 *	and can result in {@link ConcurrentModificationException}s being 
	 *	thrown by any iterator in use while this method is called.
	 *
	 *	@return		true if the reference to the value is now hard; false if the
	 *				value does not exist in the collection (or has already 
	 *				expired)
	 */
	public boolean makeHard (Object obj);


	/**
	 *	Soften the reference for the given value.  This will allow the value
	 *	to be expired from the collection and garbage collected.
	 *	This is the default for all new values added to the collection.
	 *	Note that this is a mutator method and can result in 
	 *	{@link ConcurrentModificationException}s being thrown by any
	 *	iterator in use while this method is called.
	 *
	 *	@return		true if the reference to the value is now soft; false
	 *				if the value does not exist in the collection or cannot
	 *				be maintained in a reference (as for nuill values)
	 */
	public boolean makeReference (Object obj);
}
