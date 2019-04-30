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
 * Created on Aug 19, 2004
 */
package tyRuBa.modes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBJavaObjectCompoundTerm;
import tyRuBa.engine.RBTerm;

/**
 * @author riecken
 */
public class JavaConstructorType extends ConstructorType {
	
	Class javaClass;
	
	public JavaConstructorType(Class javaClass) {
		this.javaClass = javaClass;
	}
	
	public RBTerm apply(ArrayList terms) {
		throw new Error("Java Constructors can only be applied a single term");
	}
	
	public RBTerm apply(RBTerm term) {
		if (term instanceof RBJavaObjectCompoundTerm) {
			RBJavaObjectCompoundTerm java_term = (RBJavaObjectCompoundTerm) term;
			if (this.getTypeConst().isSuperTypeOf(java_term.getTypeConstructor()))
				return java_term;
			else {
				Object obj = java_term.getObject();
				try {
					Constructor ctor = javaClass.getConstructor(new Class[] {obj.getClass()});
					return RBCompoundTerm.makeJava(ctor.newInstance(new Object[] {obj}));
				} catch (Exception e) {
					throw new Error("Illegal TyRuBa to Java Type Cast: "+java_term+"::"+this);
					// TODO: This is really a TypeModeError 
				}
			}
		}
		return RBCompoundTerm.make(this,term);
	}
	
	public Type apply(Type argType) throws TypeModeError {
		Type iresult = getType();
		argType.checkEqualTypes(iresult);
		return iresult;
	}
	
	public Type getType() {
		return Factory.makeSubAtomicType(this.getTypeConst());
	}
	
	public boolean equals(Object other) {
		if (this.getClass()!=other.getClass())
			return false;
		else
			return this.javaClass.equals(((JavaConstructorType)other).javaClass);
	}
	
	public int hashCode() {
		return javaClass.hashCode();
	}
	
	public int getArity() {
		return 1;
	}
	
	public FunctorIdentifier getFunctorId() {
		return new FunctorIdentifier(javaClass.getName(),1);
	}
	public TypeConstructor getTypeConst() {
		return Factory.makeTypeConstructor(javaClass);
	}
	
	public String toString() {
		return "JavaConstructorType("+javaClass+")";
	}
}
