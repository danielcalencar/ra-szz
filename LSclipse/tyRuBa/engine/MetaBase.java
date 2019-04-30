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

import tyRuBa.modes.CompositeType;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeMapping;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.tdbc.PreparedInsert;
import tyRuBa.tdbc.TyrubaException;

public class MetaBase
{
  private QueryEngine engine;
  private PreparedInsert typeConstructorFact = null;
  private PreparedInsert nameFact = null;
  private PreparedInsert subtypeFact = null;
  private PreparedInsert representationFact = null;
  private PreparedInsert arityFact = null;
  public static String declarations = "TYPE meta.Type \t= meta.TupleType \t| meta.ListType \t| meta.CompositeType TYPE meta.TupleType AS [meta.Type] TYPE meta.ListType AS meta.Type TYPE meta.CompositeType AS <tyRuBa.modes.TypeConstructor,meta.Type> meta.typeConstructor :: tyRuBa.modes.TypeConstructor MODES (F) IS NONDET END meta.name :: Object, String MODES (F,F) IS NONDET END meta.arity :: tyRuBa.modes.TypeConstructor, Integer MODES (B,F) IS DET       (F,F) IS NONDET END meta.subtype :: tyRuBa.modes.TypeConstructor, tyRuBa.modes.TypeConstructor MODES (F,F) IS NONDET       (B,F) IS NONDET      (F,B) IS SEMIDET END meta.representation :: tyRuBa.modes.TypeConstructor, meta.Type MODES (B,F) IS SEMIDET       (F,F) IS NONDET END ";
  
  MetaBase(QueryEngine engine)
  {
    this.engine = engine;
  }
  
  private void lazyInitialize()
  {
    if (this.typeConstructorFact == null) {
      try
      {
        this.typeConstructorFact = this.engine.prepareForInsertion(
          "meta.typeConstructor(!t)");
        this.arityFact = this.engine.prepareForInsertion(
          "meta.arity(!t,!n)");
        this.nameFact = this.engine.prepareForInsertion(
          "meta.name(!t,!n)");
        this.subtypeFact = this.engine.prepareForInsertion(
          "meta.subtype(!super,!sub)");
        this.representationFact = this.engine.prepareForInsertion(
          "meta.representation(!type,!repType)");
      }
      catch (ParseException e)
      {
        e.printStackTrace();
        throw new Error(e);
      }
      catch (TypeModeError e)
      {
        e.printStackTrace();
        throw new Error(e);
      }
    }
  }
  
  public void assertTypeConstructor(TypeConstructor type)
  {
    lazyInitialize();
    try
    {
      this.typeConstructorFact.put("!t", type);
      this.typeConstructorFact.executeInsert();
      
      this.nameFact.put("!t", type);
      this.nameFact.put("!n", type.getName());
      this.nameFact.executeInsert();
      
      this.arityFact.put("!t", type);
      this.arityFact.put("!n", type.getTypeArity());
      this.arityFact.executeInsert();
      
      type.setMetaBase(this);
    }
    catch (TyrubaException e)
    {
      throw new Error(e);
    }
  }
  
  public void assertSubtype(TypeConstructor superConst, TypeConstructor subConst)
  {
    lazyInitialize();
    try
    {
      this.subtypeFact.put("!super", superConst);
      this.subtypeFact.put("!sub", subConst);
      this.subtypeFact.executeInsert();
    }
    catch (TyrubaException e)
    {
      throw new Error(e);
    }
  }
  
  public void assertRepresentation(TypeConstructor constructor, Type repType)
  {
    lazyInitialize();
    try
    {
      this.representationFact.put("!type", constructor);
      this.representationFact.put("!repType", repType);
      this.representationFact.executeInsert();
    }
    catch (TyrubaException e)
    {
      throw new Error(e);
    }
  }
  
  public static void addTypeMappings(FrontEnd frontend)
    throws TypeModeError
  {
    frontend.addTypeMapping(
      new FunctorIdentifier("meta.Type", 0), 
      new TypeMapping()
      {
        public Class getMappedClass()
        {
          return Type.class;
        }
        
        public Object toTyRuBa(Object obj)
        {
          throw new Error("This method cannot be caled because the class Type is abstract");
        }
        
        public Object toJava(Object parts)
        {
          throw new Error("This method cannot be called because meta.Type is abstract");
        }
      });
    frontend.addTypeMapping(
      new FunctorIdentifier("meta.CompositeType", 0), 
      new TypeMapping()
      {
        public Class getMappedClass()
        {
          return CompositeType.class;
        }
        
        public Object toTyRuBa(Object obj)
        {
          CompositeType compType = (CompositeType)obj;
          return new Object[] {
            compType.getTypeConstructor(), 
            compType.getArgs() };
        }
        
        public Object toJava(Object _parts)
        {
          Object[] parts = (Object[])_parts;
          TypeConstructor constructor = (TypeConstructor)parts[0];
          TupleType args = (TupleType)parts[1];
          return constructor.apply(args, false);
        }
      });
    frontend.addTypeMapping(
      new FunctorIdentifier("meta.TupleType", 0), 
      new TypeMapping()
      {
        public Class getMappedClass()
        {
          return TupleType.class;
        }
        
        public Object toTyRuBa(Object obj)
        {
          return ((TupleType)obj).getTypes();
        }
        
        public Object toJava(Object obj)
        {
          Object[] objs = (Object[])obj;
          Type[] types = new Type[objs.length];
          for (int i = 0; i < types.length; i++) {
            types[i] = ((Type)objs[i]);
          }
          return new TupleType(types);
        }
      });
  }
}
