package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestStrategy {
	public final Logger L = LoggerFactory.getLogger(TestStrategy.class);
	@Rule
	public TestName name = new TestName();
	private String query;
	private String location;
	private QueryExecution execution;
	static{
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	@Before
	public void before() throws IOException {
		String methodName = name.getMethodName();
		String[] split = methodName.split("_");
		String qname = split[0];
		String fname = split[1] + "." + split[2];
		this.query = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("./" + qname + ".sparql"), StandardCharsets.UTF_8);
		this.location = getClass().getClassLoader().getResource("./" + fname).toString();
		query = query.replace("%%location%%", location);
		L.info("Executing query: {}", query);
		L.info("{}", query);
		L.info("{}", location);
		//System.out.println("Executing query: " + query);
		execution = QueryExecutionFactory.create(
			query,
			ModelFactory.createDefaultModel());
	}

	@Test
	public void select1_test1_csv(){
		L.info("{}", query);
		ResultSet rs = execution.execSelect();
		Assert.assertTrue(rs.hasNext());
		while (rs.hasNext()){
			L.info("{}", rs.next());
		}
	}
}
