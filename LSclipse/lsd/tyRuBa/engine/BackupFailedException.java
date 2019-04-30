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
 * Created on Oct 12, 2003
 */
package tyRuBa.engine;

import java.io.IOException;

/**
 * Because JVM does not guarnatee it will run finalizers, infortunatrly,
 * TyRuBa cannot guarantee it can make a backup of your factbase. 
 * 
 * Tyruba depends on some finalizers to release locks on the objects that
 * need to be stored to disk. If they have not been released tyruba
 * will refuse to store them to disk and then a backupFailedException
 * will result.
 * 
 * If you don;t like this, complain to sun and ask them to provide
 * as part of the JVM spec some better guarantees that finalizers
 * will do anything at all.
 * 
 * In a better future version of TyRuBa, TyRuBa will try to implement
 * a backup routine that saves the factbase anyway.
 * 
 * In the mean time, you can call ElementSource.release whenever you
 * stop reading the source before it reaches the end. This will hasten
 * release of locks and thus depend much less on running finalizers.
 * 
 * However, remaining in the spirit of Sun JVM spec. Calling this method
 * is not *guarnateed* to do anything at all :-)
 */
public class BackupFailedException extends IOException {

	public BackupFailedException(String s) {
		super(s);
	}

}
