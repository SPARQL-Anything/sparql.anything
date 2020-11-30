package com.github.spiceh2020.sparql.anything.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.github.spiceh2020.sparql.anything.tupleurl.ParameterListener;
import com.github.spiceh2020.sparql.anything.tupleurl.TupleURLParser;

public class TupleOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;
	public final static String MIME_TYPE = "mimeType";
	public final static String TRIPLIFIER = "triplifier";

	private static final Logger logger = LogManager.getLogger(TupleOpExecutor.class);

	public TupleOpExecutor(ExecutionContext execCxt) {
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

					String urlLocation = p.getProperty(ParameterListener.LOCATION);

					if (p.containsKey(TRIPLIFIER)) {
						logger.trace("Triplifier enforced");
						t = (Triplifier) Class.forName(p.getProperty(TRIPLIFIER)).getConstructor().newInstance();
					} else if (p.containsKey(MIME_TYPE)) {
						logger.trace("MimeType enforced");
						t = triplifierRegister.getTriplifierForMimeType(p.getProperty(MIME_TYPE));
					} else {
						logger.trace("Guess triplifier using file extension "+FilenameUtils.getExtension(urlLocation));
						t = triplifierRegister.getTriplifierForExtension(FilenameUtils.getExtension(urlLocation));
					}
					
					DatasetGraph dg = t.triplify(new URL(urlLocation), p);
					
					return QC.execute(opService.getSubOp(), input,
							new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));
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
		TupleURLParser p = new TupleURLParser(url);
		return p.getProperties();
	}

	protected boolean isTupleURI(String uri) {
		if (uri.startsWith("tuple:")) {
			return true;
		}
		return false;
	}

}
