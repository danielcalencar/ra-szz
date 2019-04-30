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
 * Created on May 27, 2004
 */
package tyRuBa.engine;

import java.io.Serializable;


/**
 * Subclasses of Identifier stores a name and an arity. These objects are used to identify 
 * functors, predicates, etc.
 */
public abstract class Identifier implements Serializable {

    protected String name;
    protected int arity;

    public Identifier(String name,int arity) {
        this.name = name;
        this.arity = arity;
    }

    public boolean equals(Object arg) {
    	if (arg.getClass().equals(this.getClass())) {
    	    Identifier other = (Identifier) arg;
    		return name.equals(other.name) && arity == other.arity;
    	} else {
    		return false;
    	}
    }

    public int hashCode() {
    	return getClass().hashCode() * arity + name.hashCode();
    }

    public String toString() {
    	return name + "/" + arity;
    }

    public int getArity() {
    	return arity;
    }

    public String getName() {
    	return name;
    }

}
