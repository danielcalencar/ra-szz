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
package tyRuBa.engine;

/** Form key is an adapter to provide the right equals and hashCode to
    store something in a hashTable key such that it matches when it has
    the same form */
class FormKey {

  RBTerm theKey;

  FormKey(RBTerm t) { theKey = t;}

  public boolean equals(Object other) {
    if (other instanceof FormKey) {
      return theKey.sameForm(((FormKey)other).theKey);
    }
    else
      return false;
  }

  public int hashCode() {
    return theKey.formHashCode();
  }

}
