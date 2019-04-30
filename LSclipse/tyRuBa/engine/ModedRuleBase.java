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

import java.util.Vector;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.factbase.FactBase;
import tyRuBa.engine.factbase.FactLibraryManager;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeModeError;

public class ModedRuleBase
  extends RuleBase
{
  private RBComponentVector rules = null;
  private FactBase facts;
  private Mode currMode = null;
  private Vector[] currTypes = null;
  private PredicateIdentifier predId;
  private FactLibraryManager libraryManager;
  
  public ModedRuleBase(QueryEngine engine, PredicateMode predMode, FactBase allTheFacts, FactLibraryManager libraryManager, PredicateIdentifier predId)
  {
    super(engine, predMode, allTheFacts.isPersistent());
    this.facts = allTheFacts;
    this.predId = predId;
    this.libraryManager = libraryManager;
    ensureRuleBase();
    this.currTypes = new Vector[predMode.getParamModes().getNumBound()];
    for (int i = 0; i < this.currTypes.length; i++) {
      this.currTypes[i] = new Vector();
    }
  }
  
  public void insert(RBComponent r, ModedRuleBaseIndex insertedFrom, TupleType inferredTypes)
    throws TypeModeError
  {
    try
    {
      PredicateMode thisRBMode = getPredMode();
      RBComponent converted = r.convertToMode(thisRBMode, 
        Factory.makeModeCheckContext(insertedFrom));
      if (getPredMode().toBeCheck())
      {
        BindingList bindings = thisRBMode.getParamModes();
        TupleType boundTypes = Factory.makeTupleType();
        for (int i = 0; i < bindings.size(); i++) {
          if (bindings.get(i).isBound()) {
            boundTypes.add(inferredTypes.get(i));
          }
        }
        if (this.currMode == null)
        {
          this.currMode = converted.getMode();
          for (int i = 0; i < this.currTypes.length; i++) {
            this.currTypes[i].add(boundTypes.get(i));
          }
        }
        else if (this.currTypes.length == 0)
        {
          this.currMode = this.currMode.add(converted.getMode());
        }
        else
        {
          boolean hasOverlap = true;
          for (int i = 0; i < this.currTypes.length; i++) {
            hasOverlap = 
              boundTypes.get(i).hasOverlapWith(this.currTypes[i], hasOverlap);
          }
          if ((hasOverlap) && (this.currTypes.length > 0)) {
            this.currMode = this.currMode.add(converted.getMode());
          } else {
            this.currMode = this.currMode.noOverlapWith(converted.getMode());
          }
        }
        if (this.currMode.compatibleWith(getMode())) {
          privateInsert(converted, insertedFrom);
        } else {
          throw new TypeModeError(
            "Inferred mode exceeds declared mode in " + 
            converted.getPredName() + "\n" + 
            "inferred mode: " + this.currMode + "\tdeclared mode: " + getMode());
        }
      }
      else
      {
        privateInsert(converted, insertedFrom);
      }
    }
    catch (TypeModeError e)
    {
      throw new TypeModeError("while converting " + r + " to mode: " + 
        getPredMode() + "\n" + e.getMessage());
    }
  }
  
  private void privateInsert(RBComponent converted, ModedRuleBaseIndex insertedFrom)
    throws TypeModeError
  {
    ensureRuleBase();
    this.rules.insert(converted);
  }
  
  private void ensureRuleBase()
  {
    if (this.rules == null) {
      this.rules = new RBComponentVector();
    }
  }
  
  public String toString()
  {
    return 
    
      "/******** BEGIN ModedRuleBase ***********************/\nPredicate mode: " + getPredMode() + "\n" + "Inferred mode: " + this.currMode + "\n" + this.rules + "\n" + "/******** END ModedRuleBase *************************/";
  }
  
  public int hashCode()
  {
    throw new Error("That's strange... who wants to know my hashcode??");
  }
  
  protected Compiled compile(CompilationContext context)
  {
    if (this.rules != null)
    {
      if (isPersistent()) {
        return this.facts.compile(getPredMode(), context).disjoin(this.libraryManager.compile(getPredMode(), this.predId, context)).disjoin(this.rules.compile(context));
      }
      return this.facts.compile(getPredMode(), context).disjoin(this.rules.compile(context));
    }
    if (isPersistent()) {
      return this.facts.compile(getPredMode(), context).disjoin(this.libraryManager.compile(getPredMode(), this.predId, context));
    }
    return this.facts.compile(getPredMode(), context);
  }
}
