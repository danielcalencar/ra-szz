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
 * Created on Aug 11, 2004
 */
package tyRuBa.modes;

import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.MetaBase;

/**
 * @author riecken
 */
public abstract class TypeConstructor {
    
    public static TypeConstructor theAny = Factory.makeTypeConstructor(Object.class);
    
    public Type apply(TupleType args, boolean growable) {
        if (growable) { 
            return new GrowableType(new CompositeType(this, false, args));
        } else {
            return new CompositeType(this, false, args);
        }
    }
    
    public Type applyStrict(TupleType args, boolean growable) {
        if (growable) { 
            return new GrowableType(new CompositeType(this, true, args));
        } else {
            return new CompositeType(this, true, args);
        }
    }

    public boolean isSuperTypeOf(TypeConstructor other) {
        if (this.equals(other)) {
            return true;
        } else {
            TypeConstructor superTypeConst = other.getSuperTypeConstructor();
            return superTypeConst != null
                && this.isSuperTypeOf(superTypeConst);
        }
    }

    public abstract TypeConstructor getSuperTypeConstructor();

    public TypeConstructor getSuperestTypeConstructor() {
        TypeConstructor superConst = getSuperTypeConstructor();
        if (superConst == null) {
            return this;
        } else {
            return superConst.getSuperestTypeConstructor();
        }
    }

    public abstract String getName();

    public abstract int getTypeArity();

    public int getTermArity() {
        try {
            if (!hasRepresentation())
                throw new TypeModeError("The type constructor " + this + "is abstract and cannot be used as a term constructor");
            Type representedBy = getRepresentation();
            if (representedBy instanceof TupleType)
                return ((TupleType)representedBy).size();
            if (representedBy instanceof ListType)
                return 1; // constructor to make one of these needs one representation 
                          // argument of some list type.
            if (representedBy instanceof CompositeType)
                return 1;
            else 
                throw new Error("This should not happen");
        }
        catch (TypeModeError e) {
            throw new Error("This should not happen, unless the type system is broken");
        }
    }


    public abstract String getParameterName(int i);

    public TypeConstructor lowerBound(TypeConstructor otherTypeConst) {
        if (this.equals(otherTypeConst)) {
            return this;
        } else if (this.isSuperTypeOf(otherTypeConst)) {
            return this;
        } else if (otherTypeConst.isSuperTypeOf(this)) {
            return otherTypeConst;
        } else {
            return this.getSuperTypeConstructor().lowerBound(otherTypeConst);
        }
    }

    public Type getRepresentation() {
        throw new Error("This is not a user defined type: "+this);
    }

    public boolean hasRepresentation() {
        return false;
    }

    public abstract ConstructorType getConstructorType();

    public FunctorIdentifier getFunctorId() {
        return new FunctorIdentifier(getName(),getTermArity());
    }

    public abstract boolean isInitialized();

    public void setParameter(TupleType args) {
        throw new Error("This is not a user defined type: "+this);
    }

    public void setConstructorType(ConstructorType constrType) {
        throw new Error("This is not a user defined type: "+this);
    }

    public void addSubTypeConst(TypeConstructor typeConstructor) throws TypeModeError {
        throw new TypeModeError("This is not a user defined type: "+this);
    }

    public void addSuperTypeConst(TypeConstructor superConst) throws TypeModeError {
        throw new TypeModeError("This is not a user defined type: "+this);
    }

    public void setRepresentationType(Type repBy) {
        throw new Error("This is not a user defined type: "+this);
    }

    public boolean isJavaTypeConstructor() {
        return false;
    }

    /**
     * @codegroup metadata
     */
	public void setMetaBase(MetaBase base) {
		//Default implementation doesn't care about the metaBase.
		//only the userdefinedTC cares because it needs to be able to add
		//its own metaData to the metaBase.
	}

	public abstract Class javaEquivalent();

}