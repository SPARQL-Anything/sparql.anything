package it.cnr.istc.stlab;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionCall {

	private static Logger logger = LoggerFactory.getLogger(FunctionCall.class);

	public static void main(String[] args) {
		try {
			Configurations configs = new Configurations();
			Configuration config = configs.properties("config.properties");
			logger.info(config.getString("prova"));
			FunctionRegistry.get().put("http://example.org/function#myFunction", namespace.class);

			Model m = ModelFactory.createDefaultModel();
			m.add(m.createResource("http://example.org/subj#myFunction"),
					m.createProperty("http://example.org/pred#myFunction"),
					m.createResource("http://example.org/obj#myFunction"));
			
			Query q = QueryFactory.create("SELECT (<http://example.org/function#myFunction>(?s) AS ?ns ) WHERE {?s ?p ?o}");
			
			QueryExecution qexec = QueryExecutionFactory.create(q,m);
			
			System.out.println(ResultSetFormatter.asText(qexec.execSelect()));
			
			

			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
