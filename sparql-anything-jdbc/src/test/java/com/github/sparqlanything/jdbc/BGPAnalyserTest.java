/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.jdbc;

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class BGPAnalyserTest {
	final protected static Logger L = LoggerFactory.getLogger(BGPAnalyserTest.class);
	protected Map<Node,Interpretation> interpretations = null;
	protected Properties properties = null;
	private BasicPattern bp = null;

	@Rule
	public TestName name = new TestName();

	@Before
	public void before(){
		bp = new BasicPattern();
		properties = new Properties();
	}

	protected Map<Node,Interpretation> interpretations(){
		return Collections.unmodifiableMap(interpretations);
	}

	protected BasicPattern bp(){
		return bp;
	}

	protected void properties(){
		//
	}

	protected void run(){
		OpBGP op = new OpBGP(bp);
		properties();
		BGPAnalyser analyser = new BGPAnalyser(properties, op);
		boolean canResolve = analyser.isException();
		interpretations = analyser.interpretations();
		for(Map.Entry<Node,Interpretation> entry :interpretations.entrySet()){
			L.info(" >>> {}",entry);
		}
	}

	private Triple t(Node s, Node p, Node o){
		return new Triple(s,p,o);
	}
	private Node v(String v){
		return NodeFactory.createVariable(v);
	}

	private Node u(String v){
		return NodeFactory.createURI(v);
	}

	private Node b(String v){
		return NodeFactory.createBlankNode(v);
	}

	private Node b(){
		return NodeFactory.createBlankNode();
	}

	private Node l(Object o){
		return ResourceFactory.createTypedLiteral(o).asNode();
	}

	private boolean has(Node n){
		return interpretations.containsKey(n);
	}

	private boolean isA(Node n, Class<?> cz){
		return interpretations.get(n).type().equals(cz);
	}
	private void add(Triple t){
		bp.add(t);
	}

	private void add(Node s, Node p, Node o){
		bp.add(t(s,p,o));
	}

	private void IsA(Node n, Class<?> cz){
		Assert.assertTrue(isA(n,cz));
	}
	private void Has(Node n){
		Assert.assertTrue(has(n));
	}

	private Node xyz(String localName){
		return NodeFactory.createURI(JDBC.getNamespace(properties) + localName);
	}


	@Test
	public void var_var_var(){
		add(v("x"), v("p"), v("f"));
		run();
		IsA(v("x"), Assumption.Subject.class);
		IsA(v("p"), Assumption.Predicate.class);
		IsA(v("f"), Assumption.Object.class);
	}

	@Test
	public void var_rdftype_var(){
		add(v("x"), RDF.type.asNode(), v("f"));
		run();
		IsA(v("x"), Assumption.Subject.class);
		IsA(RDF.type.asNode(), Assumption.TypeProperty.class);
		IsA(v("f"), Assumption.Object.class);
	}

	@Test
	public void var_rdftype_table(){
		// ?table a []
		add(t(v("container"), RDF.type.asNode(), xyz("table")));
		run();
		Has(v("container"));
		Has(xyz("table"));
		IsA(v("container"), Assumption.ContainerRow.class);
		IsA(RDF.type.asNode(), Assumption.TypeProperty.class);
		IsA(xyz("table"), Assumption.TypeTable.class);
	}

	@Test
	public void var_rdftype_root(){
		// ?table a []
		add(v("table"), RDF.type.asNode(), u(Triplifier.FACADE_X_TYPE_ROOT));
		run();
		IsA(v("table"), Assumption.ContainerTable.class);
		IsA(RDF.type.asNode(), Assumption.TypeProperty.class);
		IsA(u(Triplifier.FACADE_X_TYPE_ROOT), Assumption.FXRoot.class);
	}

	@Test
	public void b_cmp_b(){
		Node n = b();
		Node n2 = b();
		add(n, RDF.li(1).asNode(), n2);
		run();
		IsA(n, Assumption.ContainerTable.class);
		IsA(n2, Assumption.ContainerRow.class);
	}

	@Test
	public void b_var_l(){
		Node n = b();
		add(n, v("p"), l("12"));
		run();
		has(n);
		IsA(n, Assumption.ContainerRow.class);
		IsA(v("p"), Assumption.SlotColumn.class);
		IsA(l("12"), Assumption.SlotValue.class);
	}

	@Test
	public void var_var_l(){
		add(v("x"), v("p"), l(12));
		run();
		IsA(v("x"), Assumption.ContainerRow.class);
		IsA(v("p"), Assumption.SlotColumn.class);
		IsA(l(12), Assumption.SlotValue.class);
	}

	@Test
	public void s_P_L__s_P_L(){
		add(v("s"), v("p1"), l(12));
		add(v("s"), v("p2"), l("a string"));
		run();
		IsA(v("s"), Assumption.ContainerRow.class);
		IsA(v("p1"), Assumption.SlotColumn.class);
		IsA(v("p2"), Assumption.SlotColumn.class);
		IsA(l(12), Assumption.SlotValue.class);
		IsA(l("a string"), Assumption.SlotValue.class);
	}

	@Test
	public void S_P_l__S_P_l(){
		add(v("s1"), v("p1"), l(12));
		add(v("s2"), v("p2"), l(12));
		run();
		IsA(v("s1"), Assumption.ContainerRow.class);
		IsA(v("s2"), Assumption.ContainerRow.class);
		IsA(v("p1"), Assumption.SlotColumn.class);
		IsA(v("p2"), Assumption.SlotColumn.class);
		IsA(l(12), Assumption.SlotValue.class);
	}

	@Test
	public void S_P_v__S_P_v(){
		add(v("s1"), v("p1"), v("x"));
		add(v("s2"), v("p2"), v("x"));
		run();
		IsA(v("s1"), Assumption.Subject.class);
		IsA(v("s2"), Assumption.Subject.class);
		IsA(v("p1"), Assumption.Predicate.class);
		IsA(v("p2"), Assumption.Predicate.class);
		IsA(v("x"), Assumption.Object.class);
	}

	@Test
	public void S_u_v__S_P_v(){
		add(v("s1"), xyz("address"), v("x"));
		add(v("s2"), v("p2"), v("x"));
		run();
		IsA(v("s1"), Assumption.ContainerRow.class);
		IsA(v("s2"), Assumption.ContainerRow.class);
		IsA(xyz("address"), Assumption.SlotColumn.class);
		IsA(v("p2"), Assumption.SlotColumn.class);
		IsA(v("x"), Assumption.SlotValue.class);
	}

	@Test
	public void u_P_u(){
		properties.setProperty(JDBC.PROPERTY_NAMESPACE, "http://www.example.org/");
		properties.setProperty(IRIArgument.NAMESPACE.toString(), "http://www.example.org/data/");
		add(u("http://www.example.org/tablename"), v("p"), v("o"));
		run();
		IsA(u("http://www.example.org/tablename"), Assumption.ContainerTable.class);
		IsA(v("p"), Assumption.Predicate.class);
		IsA(v("o"), Assumption.Object.class);
	}

	@Test
	public void B_P_b__b_P_O() {
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		run();
		System.err.println(interpretations);
		IsA(t, Assumption.ContainerTable.class);
		IsA(b, Assumption.ContainerRow.class);
		IsA(v("p1"), Assumption.SlotRow.class);
		IsA(v("p2"), Assumption.Predicate.class);
		IsA(v("o"), Assumption.Object.class);
	}
}
