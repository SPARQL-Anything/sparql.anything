package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.functions.ExpandPrefixedNameFunction;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.VariableNotBoundException;
import org.apache.jena.vocabulary.DC;
import org.junit.Test;

import static org.apache.jena.graph.NodeFactory.createURI;
import static org.junit.Assert.fail;


public class ExpandPrefixedNameTest extends PrefixFunctionsTest {
	
	@Test
	public void testSuccess() {
		assertEval(createURI(ExpandPrefixedNameFunction.IRI), "fx:expandPrefixedName('fx:expandPrefixedName')");
	}
	
	@Test
	public void testSuccess2() {
		prefixes.setNsPrefix("dc", DC.NS);
		assertEval(createURI(DC.NS + "title"), "fx:expandPrefixedName('dc:title')");
	}

	@Test
	public void testUnboundArg() {
		try {
			eval("fx:expandPrefixedName(?unbound)");
			fail();
		} catch (VariableNotBoundException ex) {}
	}

	@Test
	public void testNonStringArg() {
		try {
			eval("fx:expandPrefixedName(true)");
			fail();
		} catch (ExprEvalException ex) {}
	}
	
	@Test
	public void testUndefinedPrefix() {
		try {
			eval("fx:expandPrefixedName('dc')");
			fail();
		} catch (ExprEvalException ex) {}
	}
	
	@Test
	public void testNotAPrefixedName1() {
		try {
			eval("fx:expandPrefixedName('')");
			fail();
		} catch (ExprEvalException ex) {}
	}
	
	@Test
	public void testNotAPrefixedName2() {
		try {
			eval("fx:expandPrefixedName(':')");
			fail();
		} catch (ExprEvalException ex) {}
	}
	
	@Test
	public void testNotAPrefixedName3() {
		try {
			eval("fx:expandPrefixedName(':a a')");
			fail();
		} catch (ExprEvalException ex) {}
	}
}
