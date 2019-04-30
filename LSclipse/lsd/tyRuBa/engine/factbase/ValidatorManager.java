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
 * Created on Jun 18, 2004
 */
package tyRuBa.engine.factbase;

import tyRuBa.engine.Validator;

/**
 * A ValidatorManager persists the Validators that indicate whether facts for a
 * bucket are valid or not. In a FactBase the validators are referenced by their
 * handles, so mappings are implemented in the ValidatorManager that allow
 * retrieval of a validator by giving its handle.
 * @category FactBase
 * @author riecken
 */
public interface ValidatorManager {

    /**
     * Adds a validator to this manager.
     * @param v the Validator
     * @param identifier an identifier for this validator
     */
    public void add(Validator v, String identifier);

    /**
     * Updates the specified validator.
     * @param validatorHandle the validator's handle
     */
    public void update(long validatorHandle, Boolean outdated, Boolean hasFacts);

    /**
     * Removes the validator identified by the specified handle.
     * @param validatorHandle the validator's handle
     */
    public void remove(long validatorHandle);

    /**
     * Removes a validator.
     * @param identifier identifier for the validator to remove.
     */
    public void remove(String identifier);

    /**
     * Retrieves a validator by its handle.
     * @param validatorHandle validator to retrieve.
     */
    public Validator get(long validatorHandle);

    /**
     * Retrieves a validator by its identifier.
     * @param identifier Identifier of validator to retrieve.
     */
    public Validator get(String identifier);

    /**
     * Retrieves the identifer for a given validator.
     * @param validatorHandle handle for the validator whose identifier you
     * want.
     */
    public String getIdentifier(long validatorHandle);

    /**
     * Prints out all the validators.
     */
    public void printOutValidators();

    /**
     * Persists the validators.
     */
    public void backup();

    /**
     * Returns the last time a validator was invalidated.
     */
    public long getLastInvalidatedTime();
}