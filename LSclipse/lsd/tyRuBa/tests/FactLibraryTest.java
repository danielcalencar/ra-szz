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
///*
// * Created on Jul 8, 2004
// */
//package tyRuBa.tests;
//
//import java.io.File;
//import java.io.IOException;
//
//import tyRuBa.engine.FrontEnd;
//import tyRuBa.engine.RuleBaseBucket;
//import tyRuBa.modes.TypeModeError;
//import tyRuBa.parser.ParseException;
//import tyRuBa.util.ElementSource;
//
///**
// * @author riecken
// */
//public class FactLibraryTest extends TyrubaTest {
//
//	private RubFileBucket bucket;
//	
//	/**
//	 * @param arg0
//	 */
//	public FactLibraryTest(String arg0) {
//		super(arg0);
//	}
//	
//	protected void setUp() throws Exception {
//		frontend = new FrontEnd(initfile);
//		File f = new File("fdb/test.jar");
//		if (!f.exists()) {
//			fail("test not setup properly, fdb/test.jar does not exist");
//		}
//		f = new File("fdb/test.rub");
//		if (!f.exists()) {
//			fail("test not setup properly, fdb/test.rub does not exist");
//		}
//		
//		bucket = new RubFileBucket(frontend, "fdb/test.rub");
//		frontend.getFactLibraryManager().addLibraryJarFile("fdb/test.jar");
//		frontend.parse("TYPE Package AS String " +
//					   "TYPE CU AS String " +
//				       "TYPE Field AS String " + 
//                       "TYPE Class AS String " +
//                       "TYPE Interface AS String " +
//                       "TYPE RefType = Class | Interface " +
//                       "TYPE Primitive AS String " +
//                       "TYPE Type = RefType | Primitive " +
//                       "TYPE Method AS String " +
//                       "TYPE Constructor AS String " +
//                       "TYPE Callable = Method | Constructor " +
//                       "TYPE Initializer AS String " +
//                       "TYPE Block = Callable | Initializer " +
//                       "TYPE Element = Package | CU | Field | Type | Block");
//		frontend.parse("package :: Package PERSISTENT MODES (F) IS NONDET END");
//		frontend.parse("class :: Class PERSISTENT MODES (F) IS NONDET END");
//        frontend.parse("child :: Element, Element PERSISTENT MODES (F,F) IS NONDET (B,F) IS NONDET (F,B) IS SEMIDET END");
//	}
//	
//	public void testFactLibrary() throws Exception {
//	}
//
//	public void tstFactLibrary() throws Exception {
//        ElementSource result = bucket.frameQuery("package(?P),child(?P,?CU),child(?CU,?T)");
//		while (result.hasMoreElements()) {
//		    System.err.println(result.nextElement());
//        }
//	}
//	
//	class RubFileBucket extends RuleBaseBucket {
//		
//		String myfile;
//		
//		RubFileBucket(FrontEnd fe,String filename) {
//			super(fe,filename);
//			myfile = filename;
//		}
//
//		public void update() throws ParseException, TypeModeError {
//			try {
//				load(myfile);
//			}
//			catch (IOException e) {
//				throw new Error("IOError for file "+myfile+": "+e.getMessage());
//			}
//		}
//		
//		public String toString() {
//			return "RubFileBucket("+myfile+")";
//		}
//
//	}
//
//}
