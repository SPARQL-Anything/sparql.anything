/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.jdbc;

//import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BGPAnalyserTest {
	final protected static Logger L = LoggerFactory.getLogger(BGPAnalyserTest.class);
	protected BGPConstraints constraints = null;

	protected BGPAnalyser analyser = null;
	protected BGPInterpretation initialInterpretation = null;
	protected Set<BGPInterpretation> interpretations = null;
	protected Properties properties = null;
	private BasicPattern bp = null;

	@Rule
	public TestName name = new TestName();

	@Before
	public void before(){
		bp = new BasicPattern();
		properties = new Properties();
		properties();
	}

	protected Map<Node, NodeInterpretation> constraints(){
		return Collections.unmodifiableMap(constraints.interpretations());
	}

	protected BasicPattern bp(){
		return bp;
	}

	protected void properties(){
		properties.setProperty(JDBC.PROPERTY_NAMESPACE, "http://www.example.org/");
		properties.setProperty(IRIArgument.NAMESPACE.toString(), "http://www.example.org/data/");
	}

	protected void analyseConstraints(){
		OpBGP op = new OpBGP(bp);
		analyser = new BGPAnalyser(properties, op);
		boolean canResolve = analyser.getConstraints().isException();
		constraints = analyser.getConstraints();
	}

	protected void generateInterpretations(){
		initialInterpretation = new BGPInterpretation(constraints);
		interpretations = analyser.getInterpretations();//traverse(initialInterpretation);
	}

	private Triple t(Node s, Node p, Node o){
		return Triple.create(s,p,o);
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
		return constraints().containsKey(n);
	}

	private boolean isA(Node n, Class<?> cz){
		return  constraints().get(n).type().equals(cz);
	}
	private void add(Triple t){
		bp.add(t);
	}

	/**
	 * Easybgp means 1 triple per line separated in SPARQL syntax, no dots between triples
	 * @param easyBgpFile
	 * @throws IOException
	 */
	private void readBGP(String easyBgpFile) throws IOException {
		BasicPattern bp = new BasicPattern();
//		L.info("{}", easyBgpFile);
		URL url = getClass().getClassLoader().getResource("./" + easyBgpFile + ".easybgp");
		L.trace("easy bgp: {}", url);
		String sBGP =IOUtils.toString(url, StandardCharsets.UTF_8);
//		L.trace("sBGP: {}", sBGP);
		String[] lines = sBGP.split("\n");
//		L.trace("lines: {} {}", lines,lines.length);
		for(String line : lines){
//			L.trace("line: {}", line);
			List<Node> nodes = new ArrayList<Node>();
			String[] tr = line.split(" ");
			Triple t = null;
			for (int c = 0; c<3; c++) {
				if(tr[c].trim().startsWith("<")){
					nodes.add(u(tr[c].trim().substring(1, tr[c].trim().length()-1)));
				}else
				if(tr[c].trim().startsWith("?")){
					nodes.add(v(tr[c].trim().substring(1)));
				}else
				if(tr[c].trim().startsWith("_:")){
					nodes.add(b(tr[c].trim().substring(2)));
				}else
				if(tr[c].trim().startsWith("\"")){
					nodes.add(v(tr[c].trim().substring(1,tr[c].trim().length()-1)));
				}else
				if(tr[c].trim().equals("a")){
					nodes.add(u(RDF.type.getURI()));
				}else{
					// other
					nodes.add(v(tr[c].trim()));
				}
			}
			t = Triple.create(nodes.get(0),
				nodes.get(1),
				nodes.get(2));
			bp.add(t);
		}
		L.trace("BGP: \n{}\n",bp);
		add(bp);
	}

	private void add(Node s, Node p, Node o){
		bp.add(t(s,p,o));
	}

	private void add(BasicPattern bgp){
		bp.addAll(bgp);
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
		analyseConstraints();
		IsA(v("x"), NodeInterpretation.Subject.class);
		IsA(v("p"), NodeInterpretation.Predicate.class);
		IsA(v("f"), NodeInterpretation.Object.class);
		showConstraints();
	}

	@Test
	public void var_rdftype_var(){
		add(v("x"), RDF.type.asNode(), v("f"));
		analyseConstraints();
		IsA(v("x"), NodeInterpretation.Subject.class);
		IsA(RDF.type.asNode(), NodeInterpretation.TypeProperty.class);
		IsA(v("f"), NodeInterpretation.Object.class);
		showConstraints();
	}

	@Test
	public void var_rdftype_table(){
		// ?table a []
		add(t(v("container"), RDF.type.asNode(), xyz("table")));
		analyseConstraints();
		Has(v("container"));
		Has(xyz("table"));
		IsA(v("container"), NodeInterpretation.ContainerRow.class);
		IsA(RDF.type.asNode(), NodeInterpretation.TypeProperty.class);
		IsA(xyz("table"), NodeInterpretation.TypeTable.class);
		showConstraints();
	}

	@Test
	public void var_rdftype_root(){
		// ?table a []
		add(v("table"), RDF.type.asNode(), u(Triplifier.FACADE_X_TYPE_ROOT));
		analyseConstraints();
		IsA(v("table"), NodeInterpretation.ContainerTable.class);
		IsA(RDF.type.asNode(), NodeInterpretation.TypeProperty.class);
		IsA(u(Triplifier.FACADE_X_TYPE_ROOT), NodeInterpretation.FXRoot.class);
		showConstraints();
	}

	@Test
	public void b_cmp_b(){
		Node n = b();
		Node n2 = b();
		add(n, RDF.li(1).asNode(), n2);
		analyseConstraints();
		IsA(n, NodeInterpretation.ContainerTable.class);
		IsA(n2, NodeInterpretation.ContainerRow.class);
		showConstraints();
	}

	@Test
	public void b_var_l(){
		Node n = b();
		add(n, v("p"), l("12"));
		analyseConstraints();
		has(n);
		IsA(n, NodeInterpretation.ContainerRow.class);
		IsA(v("p"), NodeInterpretation.SlotColumn.class);
		IsA(l("12"), NodeInterpretation.SlotValue.class);
		showConstraints();
	}

	@Test
	public void var_var_l(){
		add(v("x"), v("p"), l(12));
		analyseConstraints();
		IsA(v("x"), NodeInterpretation.ContainerRow.class);
		IsA(v("p"), NodeInterpretation.SlotColumn.class);
		IsA(l(12), NodeInterpretation.SlotValue.class);
		showConstraints();
	}

	@Test
	public void s_P_L__s_P_L(){
		add(v("s"), v("p1"), l(12));
		add(v("s"), v("p2"), l("a string"));
		analyseConstraints();
		IsA(v("s"), NodeInterpretation.ContainerRow.class);
		IsA(v("p1"), NodeInterpretation.SlotColumn.class);
		IsA(v("p2"), NodeInterpretation.SlotColumn.class);
		IsA(l(12), NodeInterpretation.SlotValue.class);
		IsA(l("a string"), NodeInterpretation.SlotValue.class);
		showConstraints();
	}

	@Test
	public void S_P_l__S_P_l(){
		add(v("s1"), v("p1"), l(12));
		add(v("s2"), v("p2"), l(12));
		analyseConstraints();
		IsA(v("s1"), NodeInterpretation.ContainerRow.class);
		IsA(v("s2"), NodeInterpretation.ContainerRow.class);
		IsA(v("p1"), NodeInterpretation.SlotColumn.class);
		IsA(v("p2"), NodeInterpretation.SlotColumn.class);
		IsA(l(12), NodeInterpretation.SlotValue.class);
		showConstraints();
	}

	@Test
	public void S_P_v__S_P_v(){
		add(v("s1"), v("p1"), v("x"));
		add(v("s2"), v("p2"), v("x"));
		analyseConstraints();
		IsA(v("s1"), NodeInterpretation.Subject.class);
		IsA(v("s2"), NodeInterpretation.Subject.class);
		IsA(v("p1"), NodeInterpretation.Predicate.class);
		IsA(v("p2"), NodeInterpretation.Predicate.class);
		IsA(v("x"), NodeInterpretation.Object.class);
		showConstraints();
	}

	public void S_P_V__S_P_V_same_P_V(){
		add(v("s1"), v("p1"), v("x"));
		add(v("s2"), v("p1"), v("x"));
		analyseConstraints();
		IsA(v("s1"), NodeInterpretation.Subject.class);
		IsA(v("s2"), NodeInterpretation.Subject.class);
		IsA(v("p1"), NodeInterpretation.Predicate.class);
		IsA(v("p2"), NodeInterpretation.Predicate.class);
		IsA(v("x"), NodeInterpretation.Object.class);
		showConstraints();
	}

	@Test
	public void S_u_v__S_P_v(){
		add(v("s1"), xyz("address"), v("x"));
		add(v("s2"), v("p2"), v("x"));
		analyseConstraints();
		IsA(v("s1"), NodeInterpretation.ContainerRow.class);
		IsA(v("s2"), NodeInterpretation.ContainerRow.class);
		IsA(xyz("address"), NodeInterpretation.SlotColumn.class);
		IsA(v("p2"), NodeInterpretation.SlotColumn.class);
		IsA(v("x"), NodeInterpretation.SlotValue.class);
		showConstraints();
	}

	@Test
	public void u_P_u(){
		add(u("http://www.example.org/tablename"), v("p"), v("o"));
		analyseConstraints();
		IsA(u("http://www.example.org/tablename"), NodeInterpretation.ContainerTable.class);
		IsA(v("p"), NodeInterpretation.Predicate.class);
		IsA(v("o"), NodeInterpretation.Object.class);
		showConstraints();
	}

	@Test
	public void B_P_b__b_P_O() {
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		analyseConstraints();
//		System.err.println(interpretations);
		IsA(t, NodeInterpretation.ContainerTable.class);
		IsA(b, NodeInterpretation.ContainerRow.class);
		IsA(v("p1"), NodeInterpretation.SlotRow.class);
		IsA(v("p2"), NodeInterpretation.Predicate.class);
		IsA(v("o"), NodeInterpretation.Object.class);
		showConstraints();
	}

	public void showConstraints(){
		show(constraints.interpretations());
	}
	public void showInterpretations(){
		StringBuilder b = new StringBuilder();
		b.append("\n\n");
		b.append(bp());
		for(BGPInterpretation in: interpretations){
			b.append("\n --- ");
			for(Map.Entry<Node, NodeInterpretation> entry:  in.signature().entrySet()){
				b.append("\n - ");
				b.append(entry.getKey());
				if(entry.getKey().isConcrete()) {
					b.append(" is a ");
				}else{
					b.append(" must be ");
				}
				b.append(entry.getValue().type().getSimpleName());
			}
		}
		b.append("\n\n");
		L.error("{}",b.toString());
	}
	public void show(Map<Node, NodeInterpretation> map){
//		L.info("{}",bp());
		StringBuilder b = new StringBuilder();
		b.append("\n\n");
		b.append(bp());

		for(Map.Entry<Node, NodeInterpretation> entry:  map.entrySet()){
			b.append("\n --- ");
			b.append(entry.getKey());
			if(entry.getKey().isConcrete()) {
				b.append(" is a ");
			}else{
				b.append(" must be ");
			}
			b.append(entry.getValue().type().getSimpleName());
		}
		b.append("\n\n");
		L.error("{}",b.toString());
	}

	@Test
	public void testRootState_1(){
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		analyseConstraints();
		BGPInterpretation s = new BGPInterpretation(constraints);
		Assert.assertTrue(s.isInitialState());
		BGPInterpretation s2 = new BGPInterpretation(constraints);
		Assert.assertTrue(s.equals(s2));
	}

	@Test
	public void testNextState_1(){
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		analyseConstraints();
		generateInterpretations();
		Assert.assertTrue(initialInterpretation.isInitialState());
		Set<BGPInterpretation> nexts = new HashSet<>();
		for (Pair<Node, NodeInterpretation> ii : BGPAnalyser.expand(initialInterpretation.signature())) {

			Map<Node,NodeInterpretation> possible = new HashMap<>();
			possible.putAll(initialInterpretation.signature());
			possible.put(ii.getLeft(),ii.getRight());
			try{
				BGPConstraints constrained = new BGPConstraints(analyser.getTranslation(), analyser.getOp(), possible);
				BGPInterpretation next = new BGPInterpretation(initialInterpretation, constrained.interpretations());
				nexts.add(next);
			}catch(Exception e){
				L.trace("Ignore invalid combination");
				// Ignore this combination because it is invalid
			}
		}
		for(BGPInterpretation n : nexts){
			Assert.assertFalse(n.isInitialState());
			Assert.assertTrue(n.previous().equals(initialInterpretation));
			Assert.assertTrue(initialInterpretation.next().contains(n));
		}
	}

	@Test
	public void testIsFinalState(){
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		analyseConstraints();
		generateInterpretations();
		Assert.assertTrue(initialInterpretation.isInitialState());
		Assert.assertFalse(initialInterpretation.isFinalState());
	}

	@Test
	public void testTraverseState(){
		Node t = b();
		Node b = b();
		add(t, v("p1"), b);
		add(b, v("p2"), v("o"));
		analyseConstraints();
		generateInterpretations();
		//
		for(BGPInterpretation st: interpretations){
			L.info("[{}] ==> {}", st.isFinalState(), BGPInterpretation.toString(st));
			Assert.assertTrue(st.isFinalState());
		}
		showConstraints();
	}

	@Test
	public void testInterpretations(){
		Node s = b();
		Node o = b();
		add(s, v("property1"), o);
		//add(b, v("p2"), v("o"));
		analyseConstraints();
		generateInterpretations();
		//
		for(BGPInterpretation st: interpretations){
			L.trace("[{}] ==> {}", st.isFinalState(), BGPInterpretation.toString(st));
			Assert.assertTrue(st.isFinalState());
		}
		showInterpretations();
	}


	@Test
	public void testSPO(){

		add(v("s"), v("p"), v("o"));
		//add(b, v("p2"), v("o"));
		analyseConstraints();
		generateInterpretations();
		//
		for(BGPInterpretation st: interpretations){
			L.error("[{}] ==> {}", st.isFinalState(), BGPInterpretation.toString(st));
			Assert.assertTrue(st.isFinalState());
		}
		showInterpretations();
	}

	@Test
	public void testBGP_1() throws IOException {
		readBGP(name.getMethodName().substring(4));
		analyseConstraints();
		generateInterpretations();
		//
		for(BGPInterpretation st: interpretations){
			L.error("[{}] ==> {}", st.isFinalState(), BGPInterpretation.toString(st));
			Assert.assertTrue(st.isFinalState());
		}
		showInterpretations();
	}
}
