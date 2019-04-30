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
package changetypes;

public abstract interface IAtomicChange
{
  public static enum ChangeTypes
  {
    ADD_PACKAGE,  ADD_TYPE,  ADD_METHOD,  ADD_FIELD,  ADD_RETURN,  ADD_FIELDOFTYPE,  ADD_ACCESSES,  ADD_CALLS,  ADD_SUBTYPE,  ADD_INHERITEDFIELD,  ADD_INHERITEDMETHOD,  ADD_PARAMETER,  DEL_PACKAGE,  DEL_TYPE,  DEL_METHOD,  DEL_FIELD,  DEL_RETURN,  DEL_FIELDOFTYPE,  DEL_ACCESSES,  DEL_CALLS,  DEL_SUBTYPE,  DEL_INHERITEDFIELD,  DEL_INHERITEDMETHOD,  CHANGE_METHODBODY,  CHANGE_METHODSIGNATURE,  ADD_CONDITIONAL,  DEL_CONDITIONAL,  DEL_PARAMETER;
  }
}
