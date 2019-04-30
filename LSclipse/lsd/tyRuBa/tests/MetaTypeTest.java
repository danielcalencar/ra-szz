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
package tyRuBa.tests;

import java.io.Serializable;

import tyRuBa.engine.RuleBase;

/**
 * @author kdvolder
 * @codegroup metadata
 */
public class MetaTypeTest extends TyrubaTest implements Serializable {

	public MetaTypeTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		TyrubaTest.initfile = true;
		RuleBase.useCache = true;
		RuleBase.silent = true;
		super.setUp();
		frontend.enableMetaData();
	}

    public void testRepAsJavaTypes() throws Exception {
    		frontend.parse(
    				"TYPE Element = Type | Method | Number " +
    				"TYPE Type AS String " +
				"TYPE Method AS String " +
				"TYPE Number AS Integer " );
    		
    		test_must_equal(
    				"meta.name(?x,Type),meta.typeConstructor(?x)",
    				"?x", frontend.findType("Type"));
    		test_must_findall(
    				"meta.name(?type,Element),meta.subtype(?type,?sub)," +
    				"meta.name(?sub,?subname)",
    				"?subname", 
				new String[] { 
    					"Method", "Number", "Type"
    				});
    		test_must_equal(
    				"meta.name(?Type,Type),meta.subtype(?Element,?Type)",
    				"?Element", 
				frontend.findType("Element"));
    		
    }

    public void testRepAsTupleTypes() throws Exception {
		frontend.parse(
			"TYPE Tree = Empty | Leaf | Node " +
			"TYPE Empty AS <> " +
			"TYPE Leaf AS String " +
			"TYPE Node AS <Tree,Tree> "
			);
		
		test_must_equal(
				"meta.name(?x,Tree),meta.typeConstructor(?x)",
				"?x", frontend.findType("Tree"));
		test_must_findall(
				"meta.name(?type,Tree),meta.subtype(?type,?sub)," +
				"meta.name(?sub,?subname)",
				"?subname", 
			new String[] { 
					"Empty", "Leaf", "Node"
				});
		
		test_must_succeed(
				"meta.name(?x,Node),meta.typeConstructor(?x)," +
				"meta.representation(?x,?rep::meta.TupleType)");
		
		test_must_equal(
				"meta.name(?x,Node),meta.typeConstructor(?x)," +
				"meta.representation(?x,?rep::meta.TupleType)," +
				"length(?rep,?ct)",
				"?ct",
				"2");
		
}
    
}
