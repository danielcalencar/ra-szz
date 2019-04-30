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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tyRuBa.util.pager.Pager.ResourceId;

/**
 * An Id for a resource that is stored on disk. Accessed using files.
 * @category FactBase
 * @author riecken
 */
public class FileResourceID extends ResourceId {

    /** Relative location to the base location. */
    private String relativeId;

    /** Base location. */
    private FileLocation base;

    /** File pointing to the resource. Only created if necessary. */
    private File lazyActualFile = null;

    /** Creates a new FileResourceID. */
    public FileResourceID(FileLocation location, String relativeID) {
        base = location;
        relativeId = relativeID;
    }

    public String toString() {
    		return "FileResourceID(" + base + "/" + relativeId +")";
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other instanceof FileResourceID) {
            FileResourceID id_other = (FileResourceID) other;
            return (id_other.relativeId.equals(relativeId) && id_other.base.equals(base));
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return 23 * base.hashCode() + 47 * relativeId.hashCode();
    }

    /** Opens an InputStream to the resource. */
    public InputStream readResource() throws IOException {
        if (lazyActualFile == null) {
            lazyActualFile = new File(base.getBase(), relativeId);
        }
        return new FileInputStream(lazyActualFile);
    }

    /** Opens an OutputStream to the resource. */
    public OutputStream writeResource() throws IOException {
        File baseFile = base.getBase();
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        if (lazyActualFile == null) {
            lazyActualFile = new File(base.getBase(), relativeId);
        }
        return new FileOutputStream(lazyActualFile);
    }

    /** Deletes the resource */
    public void removeResource() {
        if (lazyActualFile == null) {
            lazyActualFile = new File(base.getBase(), relativeId);
        }
        lazyActualFile.delete();
    }

    /** Checks whether the resource exists. */
    public boolean resourceExists() {
        if (lazyActualFile == null) {
            lazyActualFile = new File(base.getBase(), relativeId);
        }
        return lazyActualFile.exists();
    }

}