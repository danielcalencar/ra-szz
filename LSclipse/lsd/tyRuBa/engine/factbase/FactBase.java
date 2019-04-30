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
 * Created on May 4, 2004
 */
package tyRuBa.engine.factbase;

import tyRuBa.engine.RBComponent;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.PredicateMode;

/**
 * A Fact Database.
 * @category FactBase
 * @author riecken
 */
public abstract class FactBase {

    /**
     * Returns true if this factbase is persisted to disk somehow.
     */
    public abstract boolean isPersistent();

    /**
     * Persists the factbase to disk (if persistent, otherwise does nothing).
     */
    public abstract void backup();

    /**
     * Adds a fact to the database.
     * @param f the fact to add to the factbase.
     */
    public abstract void insert(RBComponent f);

    /**
     * Returns true if this factbase is empty.
     */
    public abstract boolean isEmpty();

    /**
     * Compiles this FactBase. Wraps the basicCompile method to fail if this
     * FactBase is empty.
     * @param mode the mode that this factbase is being compiled for.
     * @param context compilation context.
     */
    public final Compiled compile(PredicateMode mode, CompilationContext context) {
        if (this.isEmpty()) {
            return Compiled.fail;
        } else
            return this.basicCompile(mode, context);
    }

    /**
     * Actually compiles this FactBase.
     * @param mode the mode that this factbase is being compiled for.
     * @param context compilation context.
     */
    public abstract Compiled basicCompile(PredicateMode mode, CompilationContext context);
}