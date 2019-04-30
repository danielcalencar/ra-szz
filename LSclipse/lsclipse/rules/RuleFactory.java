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
package lsclipse.rules;

import java.util.HashSet;
import java.util.Set;

public class RuleFactory
{
  Set<Rule> rules = new HashSet();
  
  public RuleFactory()
  {
    this.rules.add(new ChangeBidirectionalAssociationToUni());
    this.rules.add(new ChangeUnidirectionalAssociationToBi());
    this.rules.add(new DecomposeConditional());
    this.rules.add(new EncapsulateCollection());
    this.rules.add(new ExtractMethod());
    this.rules.add(new InlineMethod());
    this.rules.add(new InlineTemp());
    this.rules.add(new IntroduceAssertion());
    this.rules.add(new IntroduceExplainingVariable());
    this.rules.add(new IntroduceNullObject());
    this.rules.add(new MoveMethod());
    this.rules.add(new ParameterizeMethod());
    this.rules.add(new PreserveWholeObject());
    this.rules.add(new RemoveAssignmentToParameters());
    this.rules.add(new RemoveControlFlag());
    this.rules.add(new RenameMethod());
    this.rules.add(new ReplaceArrayWithObject());
    this.rules.add(new ReplaceConditionalWithPolymorphism());
    this.rules.add(new ReplaceDataValueWithObject());
    this.rules.add(new ReplaceExceptionWithTest());
    this.rules.add(new ReplaceMethodWithMethodObject());
    this.rules.add(new ReplaceNestedCondWithGuardClauses());
    this.rules.add(new ReplaceParameterWithExplicitMethods());
    this.rules.add(new ReplaceSubclassWithField());
    this.rules.add(new SeparateQueryFromModifier());
    this.rules.add(new ConsolidateConditionalExpression());
    this.rules.add(new ConsolidateDuplicateConditionalFragment());
    this.rules.add(new ReplaceTypeCodeWithSubclasses());
    this.rules.add(new IntroduceParamObject());
    this.rules.add(new ReplaceTypeCodeWithState());
    this.rules.add(new FormTemplateMethod());
  }
  
  public Rule returnRuleByName(String name)
  {
    for (Rule r : this.rules) {
      if (r.getName().equals(name)) {
        return r;
      }
    }
    return null;
  }
}
