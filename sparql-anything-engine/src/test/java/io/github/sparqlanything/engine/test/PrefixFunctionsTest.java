package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.functions.ExpandPrefixFunction;
import io.github.sparqlanything.engine.functions.ExpandPrefixedNameFunction;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.function.FunctionEnvBase;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.util.ExprUtils;
import org.junit.Before;

import static org.junit.Assert.assertEquals;


public abstract class PrefixFunctionsTest {
	protected PrefixMapping prefixes;
	protected FunctionEnv env;
	
	@Before
	public void setUp() {
		prefixes = new PrefixMappingImpl();
		prefixes.setNsPrefix("fx", Triplifier.FACADE_X_CONST_NAMESPACE_IRI);
		env = new FunctionEnvBase();
		env.getContext().set(ExpandPrefixFunction.PREFIX_MAPPING, prefixes);
		FunctionRegistry.get().put(ExpandPrefixFunction.IRI, ExpandPrefixFunction.class);
		FunctionRegistry.get().put(ExpandPrefixedNameFunction.IRI, ExpandPrefixedNameFunction.class);
	}

	protected void assertEval(Node expected, String expression) {
		assertEquals(expected, eval(expression));
	}
	
	protected Node eval(String expression) {
		Expr expr = ExprUtils.parse(expression, prefixes);
		return expr.eval(BindingFactory.root(), env).asNode();
	}

	protected Node stringNode(String s) {
		return NodeFactory.createLiteralString(s);
	}
}
