package com.github.spiceh2020.sparql.anything.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.spiceh2020.sparql.anything.model.Format;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.github.spiceh2020.sparql.anything.tupleurl.ParameterListener;
import com.github.spiceh2020.sparql.anything.tupleurl.TupleURLParser;

public class TupleOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;

	private static final Logger logger = LogManager.getLogger(TupleOpExecutor.class);

	protected TupleOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		triplifierRegister = TriplifierRegister.getInstance();
	}

	protected QueryIterator execute(final OpService opGraph, QueryIterator input) {

		if (opGraph.getService().isURI()) {
			if (detectTupleURI(opGraph.getService().getURI())) {

				Format f = guessFormat(opGraph.getService().getURI());
				Triplifier t;
				try {
					t = triplifierRegister.getTriplifierForFormat(f).getConstructor().newInstance();

					Properties p = getProperties(opGraph.getService().getURI());
					logger.trace("Properties extracted " + p.toString());
					String urlLocation = p.getProperty(ParameterListener.LOCATION);
					t.setParameters(p);
					ExecutionContext cxt2;
					cxt2 = new ExecutionContext(execCxt, t.triplify(getFileURL(urlLocation)));
					return QC.execute(opGraph.getSubOp(), input, cxt2);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.execute(opGraph, input);
	}

	protected URL getFileURL(String s) throws MalformedURLException {
		return new URL(s.replace("tuple:", ""));
	}

	private Properties getProperties(String url) {
		TupleURLParser p = new TupleURLParser(url);
		return p.getProperties();
	}

	protected boolean detectTupleURI(String uri) {
		if (uri.startsWith("tuple:")) {
			return true;
		}
		return false;
	}

	protected Format guessFormat(String uri) {
		if (uri.endsWith(".json")) {
			return Format.JSON;
		}
		return null;
	}

}
