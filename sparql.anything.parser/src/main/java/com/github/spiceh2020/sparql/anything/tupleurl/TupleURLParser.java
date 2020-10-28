package com.github.spiceh2020.sparql.anything.tupleurl;

import java.util.Properties;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.spiceh2020.sparql.anything.tupleurl.antlr.TupleURLLexer;

public class TupleURLParser {

	private String tupleURL;

	public TupleURLParser(String tupleURL) {
		super();
		this.tupleURL = tupleURL;
	}

	public String getTupleURL() {
		return tupleURL;
	}

	public void setTupleURL(String tupleURL) {
		this.tupleURL = tupleURL;
	}

	public Properties getProperties() {
		TupleURLLexer lexer = new TupleURLLexer(CharStreams.fromString(tupleURL.substring("tuple:".length())));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		com.github.spiceh2020.sparql.anything.tupleurl.antlr.TupleURLParser parser = new com.github.spiceh2020.sparql.anything.tupleurl.antlr.TupleURLParser(tokens);
		ParseTree tree = parser.basicURL();
		ParseTreeWalker walker = new ParseTreeWalker();
		ParameterListener listener = new ParameterListener();
		walker.walk(listener, tree);
		return listener.getProperties();
	}

}
