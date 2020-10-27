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

import com.github.spiceh2020.sparql.anything.model.Format;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TupleOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;
	public static final String LOCATION = "location";

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
					String urlLocation = p.getProperty(LOCATION);
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
		Properties p = new Properties();
		String[] parameterPairs = url.substring("tuple:".length()).split(",");
		for (String pair : parameterPairs) {
			String[] kv = pair.split("=");
			p.setProperty(kv[0], kv[1]);
		}

		return p;
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
