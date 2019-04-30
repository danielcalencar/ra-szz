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
 * Created on Jul 26, 2004
 */
package tyRuBa.util.pager;

import java.net.MalformedURLException;
import java.net.URL;

import tyRuBa.util.pager.Pager.ResourceId;

/**
 * Represents a location on by a URL
 * @category FactBase
 * @author riecken
 */
public class URLLocation extends Location {

    /** Base location. */
    URL base = null;

    /** Creates a new URLLocation. */
    public URLLocation(String theBaseURL) throws MalformedURLException {
        this(new URL(theBaseURL));
    }

    /** Creates a new URLLocation. */
    public URLLocation(URL theBaseURL) {
        base = theBaseURL;
    }

    /** Gets the base location. */
    public URL getBase() {
        return base;
    }

    /**
     * Creates a resourceId for the given path relative to the base.
     */
    public ResourceId getResourceID(String relativeID) {
        return new URLResourceID(this, relativeID);
    }

}