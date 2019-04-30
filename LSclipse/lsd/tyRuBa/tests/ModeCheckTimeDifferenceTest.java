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
//package tyRuBa.tests;
//
///*
// * Created on June 27, 2003
// */
//import tyRuBa.engine.Frame;
//import tyRuBa.engine.FrontEnd;
//import tyRuBa.engine.RBExpression;
//import tyRuBa.engine.RuleBase;
//import tyRuBa.engine.SimpleRuleBaseBucket;
//import tyRuBa.modes.TypeModeError;
//import tyRuBa.parser.ParseException;
//import tyRuBa.tdbc.PreparedQuery;
//import tyRuBa.util.ElementSource;
//
//public class ModeCheckTimeDifferenceTest extends TyrubaTest {
//
//	public ModeCheckTimeDifferenceTest(String arg0) {
//		super(arg0);
//	}
//
//	protected void setUp() throws Exception {
//		TyrubaTest.initfile = true;
//		RuleBase.useCache = false;
//		RuleBase.silent = true;
//		super.setUp();
//	}
//	
//	public void tstTypeCheckTime() throws ParseException, TypeModeError {
//		SimpleRuleBaseBucket bucket = new SimpleRuleBaseBucket(frontend);
//		RBExpression e = bucket.makeExpression("string_append(abc,def,?a)");//		long runtime;
//
//		for (int loop = 0; loop <= 100; loop++) {
//			runtime = System.currentTimeMillis();
//			for (int i = 0; i < 1000; i++) {
//				ElementSource results = bucket.frameQuery(e);
//				Frame f = (Frame) results.nextElement();
//				f.get(FrontEnd.makeVar("?a"));
//			}
//			runtime = System.currentTimeMillis() - runtime;
//			if (loop % 25 == 0)
//				System.err.println(loop + ": Slow running time = " + runtime / 1000.0);
//
//			PreparedQuery runnable = bucket.prepareForRunning(e);
//			runtime = System.currentTimeMillis();
//			for (int i = 0; i < 1000; i++) {
//				ElementSource results = runnable.start();
//				Frame f = (Frame) results.nextElement();
//				f.get(FrontEnd.makeVar("?a"));
//			}
//			runtime = System.currentTimeMillis() - runtime;
//			if (loop % 25 == 0)
//				System.err.println(loop + ": Fast running time = " + runtime / 1000.0);
//		}
//
//	}
//}
