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
/* RuleFactory.java
 * 
 * This class is used to construct and return a Rule when
 * given the name of the Rule.
 * 
 * author:   Kyle Prete
 * created:  7/20/2010
 */
package lsclipse.rules;

import java.util.HashSet;
import java.util.Set;

public class RuleFactory {
	Set<Rule> rules = new HashSet<Rule>();

	public RuleFactory() {
		rules.add(new ChangeBidirectionalAssociationToUni());
		rules.add(new ChangeUnidirectionalAssociationToBi());
		rules.add(new DecomposeConditional());
		rules.add(new EncapsulateCollection());
		rules.add(new ExtractMethod());
		rules.add(new InlineMethod());
		rules.add(new InlineTemp());
		rules.add(new IntroduceAssertion());
		rules.add(new IntroduceExplainingVariable());
		rules.add(new IntroduceNullObject());
		rules.add(new MoveMethod());
		rules.add(new ParameterizeMethod());
		rules.add(new PreserveWholeObject());
		rules.add(new RemoveAssignmentToParameters());
		rules.add(new RemoveControlFlag());
		rules.add(new RenameMethod());
		rules.add(new ReplaceArrayWithObject());
		rules.add(new ReplaceConditionalWithPolymorphism());
		rules.add(new ReplaceDataValueWithObject());
		rules.add(new ReplaceExceptionWithTest());
		rules.add(new ReplaceMethodWithMethodObject());
		rules.add(new ReplaceNestedCondWithGuardClauses());
		rules.add(new ReplaceParameterWithExplicitMethods());
		rules.add(new ReplaceSubclassWithField());
		rules.add(new SeparateQueryFromModifier());
		rules.add(new ConsolidateConditionalExpression());
		rules.add(new ConsolidateDuplicateConditionalFragment());
		rules.add(new ReplaceTypeCodeWithSubclasses());
		rules.add(new IntroduceParamObject());
		rules.add(new ReplaceTypeCodeWithState());
		rules.add(new FormTemplateMethod());
	}

	public Rule returnRuleByName(String name) {
		for (Rule r : rules) {
			if (r.getName().equals(name))
				return r;
		}
		// Our Factory does not handle this refactoring type.
		return null;
	}
}
