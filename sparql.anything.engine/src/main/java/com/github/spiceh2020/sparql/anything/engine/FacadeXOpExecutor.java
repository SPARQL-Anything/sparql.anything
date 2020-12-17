package com.github.spiceh2020.sparql.anything.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.spiceh2020.sparql.anything.facadeiri.FacadeIRIParser;
import com.github.spiceh2020.sparql.anything.metadata.MetadataTriplifier;
import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class FacadeXOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;

	private static final Logger logger = LogManager.getLogger(FacadeXOpExecutor.class);
	private MetadataTriplifier metadataTriplifier = new MetadataTriplifier();
	public static String METADATA_GRAPH_IRI = "facade-x:metadata";
	public static String FACADE_X_NAMESPACE_IRI = "urn:facade-x:ns#";

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		triplifierRegister = TriplifierRegister.getInstance();
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {

		if (opService.getService().isURI()) {
			if (isTupleURI(opService.getService().getURI())) {

				try {
					Triplifier t;
					Properties p = getProperties(opService.getService().getURI());

					logger.trace("Properties extracted " + p.toString());

					String urlLocation = p.getProperty(IRIArgument.LOCATION.toString());

					if (p.containsKey(IRIArgument.TRIPLIFIER.toString())) {
						logger.trace("Triplifier enforced");
						t = (Triplifier) Class.forName(p.getProperty(IRIArgument.TRIPLIFIER.toString()))
								.getConstructor().newInstance();
					} else if (p.containsKey(IRIArgument.MEDIA_TYPE.toString())) {
						logger.trace("MimeType enforced");
						t = triplifierRegister
								.getTriplifierForMimeType(p.getProperty(IRIArgument.MEDIA_TYPE.toString()));
					} else {
						logger.trace(
								"Guess triplifier using file extension " + FilenameUtils.getExtension(urlLocation));
						t = triplifierRegister.getTriplifierForExtension(FilenameUtils.getExtension(urlLocation));
					}
					// If triplifier is null, return an empty graph
					DatasetGraph dg;
					URL url = new URL(urlLocation);
					if (t != null) {
						 dg = t.triplify(url, p);
					}else{
						logger.error("No triplifier available for the input format!");
						dg = DatasetFactory.create().asDatasetGraph();
					}
					if (triplifyMetadata(p)) {
						dg.addGraph(NodeFactory.createURI(METADATA_GRAPH_IRI),
								metadataTriplifier.triplify(url, p).getDefaultGraph());
					}

					return QC.execute(opService.getSubOp(), input, new ExecutionContext(execCxt.getContext(),
							dg.getDefaultGraph(), dg, execCxt.getExecutor()));
				} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException
						| ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return super.execute(opService, input);
	}

	private Properties getProperties(String url) {
		FacadeIRIParser p = new FacadeIRIParser(url);
		Properties properties = p.getProperties();
		// Setting defaults

		// namespace <urn:facade-x/ns#>
		if(!properties.containsKey(IRIArgument.NAMESPACE.toString()){
			properties.setProperty(IRIArgument.NAMESPACE.toString(), FACADE_X_NAMESPACE_IRI);
		}
		return p.getProperties();
	}

	private boolean triplifyMetadata(Properties p) {
		boolean result = false;
		if (p.contains(IRIArgument.METADATA.toString())) {
			try {
				result = Boolean.parseBoolean(p.getProperty(IRIArgument.METADATA.toString()));
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}

	protected boolean isTupleURI(String uri) {
		if (uri.startsWith("facade-x:")) {
			return true;
		}
		return false;
	}

}
