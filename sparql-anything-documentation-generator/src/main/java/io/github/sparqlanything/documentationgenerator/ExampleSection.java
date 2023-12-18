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
		return QueryFactory.create(example.getQuery()).toString(Syntax.syntaxSPARQL_11);
	}

	public String getResource() {
		return example.getResource();
	}

	public String getDescription() {
		return example.getDescription();
	}

	public String getResult() {
		return AnnotationGenerator.getFacadeXRdf(QueryFactory.create(example.getQuery()));
	}
 }
