package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.VariableNotBoundException;
import org.apache.jena.vocabulary.DC;
import org.junit.Test;

import static org.junit.Assert.fail;


public class ExpandPrefixTest extends PrefixFunctionsTest {
	
	@Test
	public void testSuccess() {
		assertEval(stringNode(Triplifier.FACADE_X_CONST_NAMESPACE_IRI), "fx:expandPrefix('fx')");
	}
	
	@Test
	public void testSuccess2() {
		prefixes.setNsPrefix("dc", DC.NS);
		assertEval(stringNode(DC.NS), "fx:expandPrefix('dc')");
	}

	@Test
	public void testUnboundArg() {
		try {
			eval("fx:expandPrefix(?unbound)");
			fail();
		} catch (VariableNotBoundException ex) {}
	}

	@Test
	public void testNonStringArg() {
		try {
			eval("fx:expandPrefix(true)");
			fail();
		} catch (ExprEvalException ex) {}
	}
	
	@Test
	public void testUndefinedPrefix() {
		try {
			eval("fx:expandPrefix('dc')");
			fail();
		} catch (ExprEvalException ex) {}
	}
}
