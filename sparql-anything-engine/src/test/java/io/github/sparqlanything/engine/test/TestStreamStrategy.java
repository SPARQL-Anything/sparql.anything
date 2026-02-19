package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.FXBGPFinder;
import io.github.sparqlanything.engine.FXExecutionStrategy;
import io.github.sparqlanything.engine.FXGraphMaterialisationStrategy;
import io.github.sparqlanything.engine.FXStrategySelector;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.PropertyExtractor;
import io.github.sparqlanything.engine.UnboundVariableException;
import io.github.sparqlanything.engine.Utils;
import io.github.sparqlanything.engine.stream.FXStreamExecutionStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

public class TestStreamStrategy {
	public final Logger L = LoggerFactory.getLogger(TestStreamStrategy.class);
	@Rule
	public TestName name = new TestName();
	private String query;
	private String location;
	private QueryExecution execution;
	private FXStrategySelector selector = new FXStrategySelector();
	private FXExecutionStrategy strategy;
	private Properties properties;
	private Set<OpService> services;
	static{
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	@Before
	public void before() throws IOException, UnboundVariableException {
		String methodName = name.getMethodName();
		String[] split = methodName.split("_");
		String qname = split[0];
		String fname = split[1] + "." + split[2];
		this.query = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("./" + qname + ".sparql"), StandardCharsets.UTF_8);
		this.location = getClass().getClassLoader().getResource("./" + fname).toString();
		this.query = query.replace("%%location%%", location);
		this.properties = new Properties();
		Query query = QueryFactory.create(this.query);
		this.services = Utils.findFXOpServices(Algebra.compile(query));

		Assert.assertTrue(this.services.size() == 1);
		PropertyExtractor.extractProperties(this.properties, this.services.iterator().next());
	}

	private void testStrategy(boolean stream){
		strategy = selector.getStrategy(properties, services.iterator().next(), ExecutionContext.create(Context.create()));
		L.info("Test strategy: {} -> {}", query, strategy.getClass().getName());
		if(stream){
			Assert.assertTrue(strategy instanceof FXStreamExecutionStrategy);
		}else{
			Assert.assertTrue(strategy instanceof FXGraphMaterialisationStrategy);
		}
	}

	private ResultSet execAsSelect(){
		execution = QueryExecutionFactory.create(
			query,
			ModelFactory.createDefaultModel());
		return execution.execSelect();
	}


	@Test
	public void select1_test1_csv(){
		L.info("{}", query);
		testStrategy(true);
		ResultSet rs = execAsSelect();
		Assert.assertTrue(rs.hasNext());
		while (rs.hasNext()){
			L.info("{}", rs.next());
		}
	}
}
