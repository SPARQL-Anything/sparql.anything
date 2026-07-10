package io.github.sparqlanything.engine.functions;

import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.annotations.FXFunctionDoc;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.graph.NodeFactory;
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

@FXFunctionDoc(
	description = "fx:expandPrefixedName(?curie) expands a prefixed name, such as dc:title, using any prefixes" +
		" defined in the query. The result is an IRI. Originally developed within TARQL project and ported to " +
		"SPARQL-Anything with minimal changes to maximise reuse.",
	example = "BIND(fx:expandPrefixedName(?curie) as ?iri)",
	group = "FUNCTIONS",
	label = "fx:expandPrefixedName"
)
public class ExpandPrefixedNameFunction extends FunctionBase1 implements FXFunction {

	public static String IRI = Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "expandPrefixedName";
	public static String NAME = "fx:expandPrefixedName";
	private PrefixMapping prefixes;

	public ExpandPrefixedNameFunction() {
		super();
	}
	
	public NodeValue exec(NodeValue name, Context context) {
		if (name == null) return null;
		if (!name.isString()) throw new ExprEvalException(NAME + ": not a string: " + name);
		prefixes = get_prefix_mapping(context);
		if (prefixes == null) throw new ExprEvalException(NAME + ": no prefix mapping registered");

		return exec(name);
	}

	@Override
	public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
		if (args == null) {
			throw new ARQInternalErrorException(NAME + ": null args list");
		}
		if (args.size() != 1) {
			throw new ExprEvalException(NAME + ": wrong number of arguments: Wanted 1, got " + args.size());
		}
        return exec(args.get(0).eval(binding, env), env.getContext());
	}

	@Override
	public NodeValue exec(NodeValue name) {
		String pname = name.asString();
		int idx = pname.indexOf(':');
		if (idx == -1) throw new ExprEvalException(NAME + ": not a prefixed name: " + name);
		String prefix = pname.substring(0, idx);
		String iri = prefixes.getNsPrefixURI(prefix);
		if (iri == null) throw new ExprEvalException(NAME + ": prefix not defined: " + prefix);

		return NodeValue.makeNode(NodeFactory.createURI(iri + pname.substring(idx + 1)));
	}


	@Override
	public void checkBuild(String uri, ExprList args) {
		if (args.size() != 1) {
			throw new QueryBuildException("Function '" + Lib.className(this) + "' takes one argument");
		}
	}

	private PrefixMapping get_prefix_mapping(Context context) {
		prefixes = context.get(ExpandPrefixFunction.PREFIX_MAPPING);
		if (prefixes == null) {
			prefixes = new PrefixMappingImpl();
			Query query = context.get(ARQConstants.sysCurrentQuery);
			prefixes.setNsPrefixes(query.getPrefixMapping());
			context.set(ExpandPrefixFunction.PREFIX_MAPPING, prefixes);
		}

		return prefixes;
	}

}
