/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.xml;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class XPathSandbox {

	@Ignore
	@Test
	public void test() throws Exception {
		printExprValue("/books/*");
		printExprValue("//book");
		printExprValue("//book[@isbn='978-3-12-148410-0']");
		printExprValue("//book/@isbn");
		printExprValue("//title/text()");
	}

	@Ignore
	@Test
	public void test2() throws Exception {
		traverseMatches("//title");
//		printExprValue("/books/*");
//		printExprValue("//book");
//		printExprValue("//book[@isbn='978-3-12-148410-0']");
//		printExprValue("//book/@isbn");
//		printExprValue("//title/text()");
	}

	public void traverseMatches(String xpath) throws Exception{
		System.out.println("Expression: " + xpath);
		VTDGen vg = new VTDGen();
		byte[] bytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("./test-xpaths.xml"));
		vg.setDoc(bytes);
		vg.parse(false);
		VTDNav vn = vg.getNav();
//		vg.parse(false);  // set namespace awareness to true
		AutoPilot ap = new AutoPilot(vn);
		//ap.declareXPathNameSpace("ns1","http://purl.org/dc/elements/1.1/");
		ap.selectXPath(xpath);
		int result = -1;
		int count = 0;
		while ((result = ap.evalXPath()) != -1) {
			traverse(vn, result);
			System.out.println(" *** ");
			count++;
		}
		System.out.println("Total # of element " + count);
		System.out.println("----------------------------");
	}

	private void printExprValue(String xpath) throws Exception{
		System.out.println("Expression: " + xpath);
		VTDGen vg = new VTDGen();
		byte[] bytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("./test-xpaths.xml"));
		vg.setDoc(bytes);
		vg.parse(false);
		VTDNav vn = vg.getNav();
//		vg.parse(false);  // set namespace awareness to true
		AutoPilot ap = new AutoPilot(vn);
		//ap.declareXPathNameSpace("ns1","http://purl.org/dc/elements/1.1/");
		ap.selectXPath(xpath);
		int result = -1;
		int count = 0;
		while ((result = ap.evalXPath()) != -1) {
			System.out.print("" + result + "[" + vn.getTokenType(result) + "] ");
			switch(vn.getTokenType(result)){
				case VTDNav.TOKEN_STARTING_TAG:
					System.out.println("Tag "+vn.toString(result));
					int nesting = vn.getNestingLevel();
					// Attributes
					int attrCount = vn.getAttrCount();
					if (attrCount > 0) {
						for (int i = result + 1; i <= result + attrCount; i += 2) {
							System.out.println(" - Attr " + vn.toString(i) + " = " + vn.toString(i + 1));
						}
					}
					// Print text
					int t = vn.getText(); // get the index of the text (char data or CDATA)
					if (t != -1)
						System.out.println(" Text  ==> " + vn.toNormalizedString(t));
					break;
				case VTDNav.TOKEN_ATTR_NAME:
					// Attribute
					System.out.println("Attribute " + vn.toString(result) );
					System.out.println( " = " + vn.toString(result + 1));
					break;
				case VTDNav.TOKEN_ATTR_VAL:
					// Attribute value
					System.out.println("Attribute value" + vn.toString(result) );
//					System.out.println( " = " + vn.toString(result + 1));
					break;
				case VTDNav.TOKEN_CHARACTER_DATA:
					// Text
					System.out.println("Text");
					System.out.println(" " + vn.toNormalizedString(result));
					break;
				case VTDNav.TOKEN_DEC_ATTR_NAME:
					System.out.println("Attribute (dec) " + vn.toString(result) );
					System.out.println( " = " + vn.toString(result + 1));
					break;
				case VTDNav.TOKEN_DEC_ATTR_VAL:
					System.out.println("Attribute value (dec) " + vn.toString(result) );
					break;
				default:
					System.out.println("????");
					System.out.println(" ==> " + vn.toString(result));

			}
			count++;
		}
		System.out.println("Total # of element " + count);
		System.out.println("----------------------------");
	}

	public void traverse(VTDNav nav, int index) throws Exception{
		int tokenDepth = nav.getTokenDepth(index);
		String tokenName = nav.toString(index);
		// Leave out attributes
		// Find child elements
		while(true){
			index++;
//			nav.toElement(index);
			int type =  nav.getTokenType(index);
			String s = nav.toString(index);
			int d = nav.getTokenDepth(index);
			// If type is element and depth is not greater than tokenDepth, break!
			if((type == VTDNav.TOKEN_STARTING_TAG && d <=tokenDepth) || (type == VTDNav.TOKEN_STARTING_TAG && s.equals(""))){
				break;
			}
			System.out.println( " ...  index: " + index + " depth: " + d + " type: " + type + " string: " + s);

		}
	}
}
