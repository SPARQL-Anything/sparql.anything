package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StrategiesTest {

	String query_s0;
	String query_s1;
	String query_s2;

	String fileName;
	String location;

	@Rule
	public TestName name = new TestName();

	static{
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}
	@Before
	public void prepare(){
		String[] parts = name.getMethodName().split("_");
		fileName = parts[0] + "." + parts[1];
		parts[0] = "location";
		location = Objects.requireNonNull(getClass().getClassLoader().getResource("test/" + fileName)).getPath();
		parts[1] = location;
		String queryMethod = parts[2];
		parts = ArrayUtils.remove(parts, 2);

		try {
			Method	method = this.getClass().getMethod(queryMethod, String.class);
			parts = ArrayUtils.add(parts, "strategy");
			String[] parts_0 = ArrayUtils.add(parts, "0");
			String[] parts_1 = ArrayUtils.add(parts, "1");
			String[] parts_2 = ArrayUtils.add(parts, "2");
			String properties_0 = buildProperties(parts_0);
			String properties_1 = buildProperties(parts_1);
			String properties_2 = buildProperties(parts_2);
			query_s0 = (String) method.invoke(this, properties_0);
			query_s1 = (String) method.invoke(this, properties_1);
			query_s2 = (String) method.invoke(this, properties_2);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public String buildProperties(String[] parts){
		StringBuilder sb = new StringBuilder();
		sb.append("fx:properties ");
		for(int i = 0; i < parts.length; i++){
			if((i & 1) == 0){
				// even / property
				sb.append(" ");
				sb.append("fx:");
				sb.append(parts[i].replace("$",".").replace("£", "-"));
				sb.append(" ");
			}else if(i == parts.length - 1){
				// last
				sb.append("\"\"\"");
				sb.append(parts[i]);
				sb.append("\"\"\"");
				sb.append(" .");
			}else{
				// odd / value
				sb.append("\"\"\"");
				sb.append(parts[i]);
				sb.append("\"\"\"");
				sb.append(" ;\n\t\t\t\t");
			}
		}
		return sb.toString();
	}

	public String selectAll(String properties){
		return String.format("""
PREFIX fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT * WHERE {\s
	SERVICE <x-sparql-anything:> {\s
		%s
		?a ?b ?c
	}
}""", properties);
	}

	public String constructAll(String properties){
		return String.format("""
PREFIX fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

CONSTRUCT{ ?a ?b ?c } WHERE {\s
	SERVICE <x-sparql-anything:> {\s
		%s
		?a ?b ?c
	}
}""", properties);
	}

	Set<QuerySolution> select(String query){
		return asSet(QueryExecutionFactory.create(query, DatasetFactory.createGeneral()).execSelect());
	}

	Model construct(String query){
		return QueryExecutionFactory.create(query, DatasetFactory.createGeneral()).execConstruct();
	}

	void selectEquals(){
		equals(select(query_s0), select(query_s1), select(query_s2));
	}

	void constructEquals(){
		equals(construct(query_s0), construct(query_s1), construct(query_s2));
	}

	Set<QuerySolution> asSet(ResultSet rs){
		Set<QuerySolution> set = new HashSet<>();
		while(rs.hasNext()){
			set.add(rs.next());
		}
		return set;
	}

	Set<String> asStringSet(Set<QuerySolution> rs){
		Set<String> set = new HashSet<>();
		for(QuerySolution qs: rs){
			List<String> qsss = new ArrayList<>();
			Iterator<String> vn = qs.varNames();
			while(vn.hasNext()){
				String v = vn.next();
				qsss.add("(?" + v + " -> " + qs.get(v).toString() + ") ");
			}
			Collections.sort(qsss);
			StringBuilder builder = new StringBuilder();
			for(String qss: qsss){
				builder.append(qss);
			}
			set.add(builder.toString().trim());
		}
		return set;
	}

	void equals(Set<QuerySolution> rs1, Set<QuerySolution> rs2, Set<QuerySolution> rs3){
//		System.out.println("--- 0 ---");
//		System.out.println(asStringSet(rs1));
//		System.out.println("--- 1 ---");
//		System.out.println(asStringSet(rs2));
//		System.out.println("--- 2 ---");
//		System.out.println(asStringSet(rs3));
		Set<String> set1 = asStringSet(rs1);
		Set<String> set2 = asStringSet(rs2);
		Set<String> set3 = asStringSet(rs3);
		Assert.assertEquals(set1, set2);
		Assert.assertEquals(set2, set3);
	}
	void equals(Model m1, Model m2, Model m3){
		m1.isIsomorphicWith(m2);
		m2.isIsomorphicWith(m3);
	}

	@Test
	public void test_csv_selectAll_csv$headers_true(){
		selectEquals();
	}

	@Test
	public void test_csv_selectAll_csv$headers_false(){
		selectEquals();
	}

	@Test
	public void test_csv_selectAll_csv$headers_true_blank£nodes_false(){
		selectEquals();
	}

	@Test
	public void test_csv_constructAll_csv$headers_true(){
		constructEquals();
	}

	@Test
	public void test_csv_constructAll_csv$headers_false(){
		constructEquals();
	}

	@Test
	public void test_csv_constructAll_csv$headers_true_blank£nodes_false(){
		constructEquals();
	}

	@Test
	public void test_json_selectAll_json$include£null£values_true(){
		selectEquals();
	}

	@Test
	public void test_csv_selectAll_json$include£null£values_false(){
		selectEquals();
	}

	@Test
	public void test_json_selectAll_json$include£null£values_true_blank£nodes_false(){
		selectEquals();
	}

	@Test
	public void test_json_constructAll_json$include£null£values_true(){
		constructEquals();
	}

	@Test
	public void test_json_constructAll_json$include£null£values_false(){
		constructEquals();
	}

	@Test
	public void test_json_constructAll_json$include£null£values_true_blank£nodes_false(){
		constructEquals();
	}

	@Test
	public void test_xml_selectAll_blank£nodes_true(){
		selectEquals();
	}

	@Test
	public void test_xml_selectAll_blank£nodes_false(){
		selectEquals();
	}
	@Test
	public void test_xml_constructAll_blank£nodes_true(){
		constructEquals();
	}

	@Test
	public void test_xml_constructAll_blank£nodes_false(){
		constructEquals();
	}

}
