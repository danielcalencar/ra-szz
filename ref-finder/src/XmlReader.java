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

package edu.utexas.seal.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class XmlReader {
	private String filePath = null;
	private Document doc = null;
	private String refactoringTypeName = null;
	private Element element;
	private ArrayList<String> renamedMethods = new ArrayList<String>();;

	public XmlReader(String filePath, String refactoringTypeName)
	{
		this.refactoringTypeName = refactoringTypeName;
		this.filePath = filePath;
		try {
			File file = new File(filePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			this.doc = db.parse(file);
		} catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public XmlReader(String filePath)
	{
		this(filePath, null);
	}

	public int getNumberOfRefactoring(String refactoringTypeName)
	{
		int count = 0;
		String result = null;
		NodeList listOfElements = this.doc.getElementsByTagName("Refactoring");
		int totalElements = listOfElements.getLength();
		for (int i = 0; i < totalElements; i++) {
			Element element = (Element)listOfElements.item(i);
			result = element.getAttribute("type");
			if (result.equals(StringConstants.pullUpMethodString)) {
				count++;
			}
			if (result.equals(StringConstants.pullUpFieldString)) {
				count++;
			}
			if (result.equals(StringConstants.pushDownMethodString)) {
				count++;
			}
			if (result.equals(StringConstants.pushDownFieldString)) {
				count++;
			}
		}
		return count;
	}

	public boolean contains(List<String> container, String item)
	{
		for (String itemInContainer : container)
		{
			if (item.equals(itemInContainer)) return true;
		}
		return false;
	}

	public String getRefactoringType()
	{
		String result = "";
		NodeList listOfElements = this.doc.getElementsByTagName("Refactoring");
		int totalElements = listOfElements.getLength();
		if (totalElements == 1) {
			Element element = (Element)listOfElements.item(0);
			result = element.getAttribute("type");
		}
		// if multiple elements
		else {
			List<String> stacks = new ArrayList<String>();
			// We need better algorithm, but now it's ad hoc.
			for (int i = 0; i < totalElements; i++) {
				Element element = (Element)listOfElements.item(i);
				result = element.getAttribute("type");
				stacks.add(result);
			}

			//TODO
			// ad-hoc, it should be correctly identified
			// search and select
			if (contains(stacks, StringConstants.pullUpMethodString)) {
				return StringConstants.pullUpMethodString;
			} else if (contains(stacks,StringConstants.pullUpFieldString)) {
				return StringConstants.pullUpFieldString;
			} else if (contains(stacks, StringConstants.pushDownFieldString)) {
				return StringConstants.pushDownFieldString;
			} else if (contains(stacks,StringConstants.pushDownMethodString)) {
				return StringConstants.pushDownMethodString;
			} else if (contains(stacks, StringConstants.moveFieldString) 
					&& contains(stacks, StringConstants.consolidateDuplicateCondFragments)) {
				return StringConstants.moveFieldString;
			} else if (contains(stacks, StringConstants.renameMethodString) 
					&& contains(stacks, StringConstants.consolidateDuplicateCondFragments)) {
				return StringConstants.renameMethodString;
			} else if (contains(stacks, StringConstants.moveMethodString) 
					&& contains(stacks, StringConstants.consolidateDuplicateCondFragments)) {
				return StringConstants.moveMethodString;
			}

		}
		return result;
	}

	/*
	 * <Results>
       <Refactoring type="Rename Method">
          <package>smcho</package>
	 */
	public String getPackage()
	{
		return getNodeValue("package");
	}

	public String getPackage(String type)
	{
		return getNodeValue(type, "package");
	}

	public String getClassName()
	{
		return getNodeValue("class");
	}

	public String getClassName(String type)
	{
		return getNodeValue(type, "class");
	}

	public String getNodeValue(String refactoringType, String elementName, int index)
	{
		String result = "";
		NodeList listOfElements = this.doc.getElementsByTagName("Refactoring");
		int totalElements = listOfElements.getLength();
		int count = 0;
		for (int i = 0; i < totalElements; i++) {
			Element element = (Element)listOfElements.item(i);
			String attributeName = element.getAttribute("type");
			if (attributeName.equals(refactoringType)) {
				if (count == index)
				{
					NodeList elements = element.getElementsByTagName(elementName);
					Element elem = (Element)elements.item(0);

					return elem.getChildNodes().item(0).getNodeValue();
				}
				count++;
			}
		}
		return result;
	}

	public String getNodeValue(String elementName, int index)
	{
		String result = "";

		if (this.refactoringTypeName == null) {
			NodeList listOfElements = this.doc.getElementsByTagName(elementName);
			Element element = (Element)listOfElements.item(index);
			NodeList childNodes = element.getChildNodes();
			result = childNodes.item(index).getNodeValue();
			return result;
		} 
		else
		{
			return getNodeValue(this.refactoringTypeName, elementName, index);
		}
	}


	public String getNodeValue(String type, String elementName)
	{
		return getNodeValue(type, elementName, 0);
	}

	public String getNodeValue(String elementName)
	{
		return getNodeValue(elementName, 0);
	}

	public ArrayList<Element> getRefactorings(){
		ArrayList<Element> refs = new ArrayList<Element>();
		NodeList listOfElements = doc.getElementsByTagName("Refactoring");
		int totalElements = listOfElements.getLength();

		for(int s=0; s<totalElements ; s++){
			Element element = (Element)listOfElements.item(s);
			String nm = element.getAttribute("newMethod");
			refs.add(element);
		}
		return refs;
	}

	public Map<String, String> getMapFromXml(String filePath)
	{
		Map<String, String> map = new HashMap<String, String>();
		try {
			File file = new File(filePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			NodeList listOfElements = doc.getElementsByTagName("Refactoring");
			int totalElements = listOfElements.getLength();
			System.out.println(totalElements);

			for(int s=0; s<totalElements ; s++){
				Element element = (Element)listOfElements.item(s);
				String key = element.getAttribute("type");
				String  value = element.getAttribute("value");

				map.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void main(String[] args) {
		XmlReader xml = new XmlReader("/Users/smcho/workspace/runtime-EclipseApplication/.metadata/reffinder/output/[renameRefactoringExampleCode]-[renameRefactoringExampleCodeA]/RefList.xml");
		System.out.println(xml.getRefactoringType());
		System.out.println(xml.getPackage());
		System.out.println(xml.getClassName());
	}

	public void setElement(Element element) {
		this.element = element;

	}

	public String getRefactoringType2() {
		return element.getAttribute("type");
	}

	public String getAttribute(String attName) {
		NodeList childNodes = element.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node child = childNodes.item(j);
			String nodeName = child.getNodeName();
			if (nodeName.equals(attName))
				return child.getFirstChild().getNodeValue();
		}
		return null;
	}

	public ArrayList<String> getRenamedMethods() {
		return this.renamedMethods ;
	}

	public void setRenamedMethods(ArrayList<String> renamedMethods) {
		this.renamedMethods = renamedMethods;
	}
}