package io.github.sparqlanything.fxbgp;


import io.github.sparqlanything.jdbc.JDBC;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class BGPTestAbstract {
	final protected static Logger L = LoggerFactory.getLogger(BGPTestAbstract.class);

	protected Properties properties = null;
	private BasicPattern bp = null;

	protected Triple t(Node s, Node p, Node o) {
		return Triple.create(s, p, o);
	}

	protected Node v(String v) {
		return NodeFactory.createVariable(v);
	}

	protected Node u(String v) {
		return NodeFactory.createURI(v);
	}

	protected Node b(String v) {
		return NodeFactory.createBlankNode(v);
	}

	protected Node b() {
		return NodeFactory.createBlankNode();
	}

	protected Node l(Object o) {
		return ResourceFactory.createTypedLiteral(o).asNode();
	}

	protected BasicPattern bp(){
		return bp;
	}

	/**
	 * Extend to configure properties for the concrete tests
	 */
	protected void properties(){}
	@Before
	public void before(){
		bp = new BasicPattern();
		properties = new Properties();
		properties();
	}



	protected void add(Node s, Node p, Node o){
		bp().add(t(s,p,o));
	}

	protected void add(BasicPattern bgp){
		bp().addAll(bgp);
	}


	protected Node xyz(String localName){
		return NodeFactory.createURI(JDBC.getNamespace(properties) + localName);
	}

	/**
	 * Easybgp means 1 triple per line separated in SPARQL syntax, no dots between triples
	 * @param easyBgpFile
	 * @throws IOException
	 */
	protected void readBGP(String easyBgpFile) throws IOException {
		BasicPattern bp = new BasicPattern();
//		L.info("{}", easyBgpFile);
		URL url = getClass().getClassLoader().getResource("./" + easyBgpFile + ".easybgp");
		L.trace("easy bgp: {}", url);
		String sBGP = IOUtils.toString(url, StandardCharsets.UTF_8);
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
}
