package io.github.sparqlanything.it;


import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ReuseQueryTest  {

	@Test
	public void reuseSelectQueryTest() throws URISyntaxException, IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("queryReuse/reuseSelect.rq")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec1.execSelect();
		Assert.assertTrue(rs.hasNext());
		rs.next();
		Assert.assertFalse(rs.hasNext());
	}

	@Test
	public void reuseConstructQueryTest() throws URISyntaxException, IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("queryReuse/reuseConstruct.rq")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec1.execSelect();
		Assert.assertTrue(rs.hasNext());
		Assert.assertEquals("abc", rs.next().get("o").asLiteral().getString());
		Assert.assertFalse(rs.hasNext());
	}

	@Test
	public void reuseConstructDatasetQueryTest() throws URISyntaxException, IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("queryReuse/reuseConstructDataset.rq")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec1.execSelect();
		Assert.assertTrue(rs.hasNext());
		Assert.assertEquals("abc", rs.next().get("o").asLiteral().getString());
		Assert.assertFalse(rs.hasNext());
	}

	@Ignore
	@Test
	public void reuseDescribeQueryTest() throws URISyntaxException, IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("queryReuse/subQueryDescribe.rq")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		//System.out.println(ResultSetFormatter.asText(qExec1.execSelect()));
		Model result = qExec1.execDescribe();
		RDFDataMgr.write(System.out, result, Lang.TTL);
//		Assert.assertTrue(rs.hasNext());
//		Assert.assertEquals("abc", rs.next().get("o").asLiteral().getString());
//		Assert.assertFalse(rs.hasNext());
	}

	@Test
	public void reuseSelectQueryBGPTest() throws URISyntaxException, IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("queryReuse/reuseSelectBGP.rq")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec1.execSelect();
		Assert.assertTrue(rs.hasNext());
		rs.next();
		Assert.assertFalse(rs.hasNext());
	}


}
