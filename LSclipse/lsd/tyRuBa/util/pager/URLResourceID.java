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
 * Created on Jul 27, 2004
 */
package tyRuBa.util.pager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import tyRuBa.util.pager.Pager.ResourceId;

/**
 * An Id for a resource that is stored on disk. Accessed using URLs.
 * @category FactBase
 * @author riecken
 */
public class URLResourceID extends ResourceId {

    /** actual location to the base location. */
    private URL actualLocation;

    /** Creates a new URLResourceID. */
    public URLResourceID(URLLocation location, String relativeID) {
        try {
            actualLocation = new URL(location.getBase(), relativeID);
        } catch (MalformedURLException e) {
            throw new Error("Malformed URL for URLResource: " + location + " ::: " + relativeID);
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other instanceof URLResourceID) {
            URLResourceID id_other = (URLResourceID) other;
            return id_other.actualLocation.equals(this.actualLocation);
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return 29 * actualLocation.hashCode();
    }

    /** Opens an InputStream to the resource. */
    public InputStream readResource() throws IOException {
        return actualLocation.openStream();
    }

    /** Opens an OutputStream to the resource. */
    public OutputStream writeResource() throws IOException {
        //read only, so return null;
        return null;
    }

    /** Deletes the resource */
    public void removeResource() {
        //read only, so do nothing
    }

    /** Checks whether the resource exists. */
    public boolean resourceExists() {
        try {
            actualLocation.openStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}