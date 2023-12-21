package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.model.annotations.Example;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;

public class ExampleSection {

	private Example example;

	public ExampleSection(Example example) {
		this.example = example;
	}

	public String getQuery(){
		return QueryFactory.create(example.query()).toString(Syntax.syntaxSPARQL_11);
	}

	public String getResource() {
		return example.resource();
	}

	public String getDescription() {
		return example.description();
	}

	public String getResult() {
		return Utils.getFacadeXRdf(QueryFactory.create(example.query()));
	}
 }
