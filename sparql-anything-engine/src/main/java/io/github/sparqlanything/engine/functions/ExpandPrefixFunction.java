package io.github.sparqlanything.engine.functions;

import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.annotations.FXFunctionDoc;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.ARQConstants;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;

@FXFunctionDoc(
	description = "fx:expandPrefix(?p) returns expanded prefix defined in the query to its associated namespace IRI." +
		"Originally developped within TARQL project and ported to SPARQL-Anything with minimal changes to maximise reuse.",
	example = "BIND(STR(fx:expandPrefix(?px)) as ?expx)",
	group = "FUNCTIONS",
	label = "fx:expandPrefix"
)
public class ExpandPrefixFunction extends FunctionBase1 implements FXFunction {

	public static String IRI = Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "expandPrefix";
	public static String NAME = "fx:expandPrefix";
	public static final Symbol PREFIX_MAPPING = Symbol.create("prefixMapping");
	private PrefixMapping prefixes;

	public ExpandPrefixFunction() {
		super();
	}

	public NodeValue exec(NodeValue prefix, Context context) {
		if (prefix == null) {
			return null;
		}
		if (!prefix.isString()) {
			throw new ExprEvalException(NAME + ": not a string: " + prefix);
		}
		prefixes = get_prefix_mapping(context);
		if (prefixes == null) {
			throw new ExprEvalException(NAME + ": no prefix mapping registered");
		}
		String iri = prefixes.getNsPrefixURI(prefix.asString());
		if (iri == null) {
			throw new ExprEvalException(NAME + ": prefix not defined: " + prefix);
		}
		return exec(prefix);
	}

	@Override
	public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
		if (args == null) {
			throw new ARQInternalErrorException("ExpandPrefixFunction: Null args list");
		}
		if (args.size() != 1) {
			throw new ExprEvalException("ExpandPrefixFunction: Wrong number of arguments: Wanted 1, got " + args.size());
		}
        return exec(args.get(0).eval(binding, env), env.getContext());
	}

	@Override
	public NodeValue exec(NodeValue prefix) {
		String iri = prefixes.getNsPrefixURI(prefix.asString());
		if (iri == null) {
			throw new ExprEvalException(NAME + ": prefix not defined: " + prefix);
		}

		return NodeValue.makeString(iri);
	}


	@Override
	public void checkBuild(String uri, ExprList args) {
		if (args.size() != 1) {
			throw new QueryBuildException("Function '" + Lib.className(this) + "' takes one argument");
		}
	}

	private PrefixMapping get_prefix_mapping(Context context) {
		prefixes = context.get(PREFIX_MAPPING);
		if (prefixes == null) {
			prefixes = new PrefixMappingImpl();
			Query query = context.get(ARQConstants.sysCurrentQuery);
			prefixes.setNsPrefixes(query.getPrefixMapping());
			context.set(ExpandPrefixFunction.PREFIX_MAPPING, prefixes);
		}

		return prefixes;
	}

}
