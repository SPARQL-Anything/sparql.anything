package com.github.spiceh2020.sparql.anything;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.ExecutionContext;

import com.github.spiceh2020.json2rdf.transformers.JSONTransformer;

public class TupleOpExecutorFunct extends TupleOpExecutor {

	protected TupleOpExecutorFunct(ExecutionContext execCxt) {
		super(execCxt);
	}

	protected URL getFileURL(String s) throws MalformedURLException {
		return new URL(s.substring(0, s.indexOf("?tuple")));
	}

	protected boolean detectTupleURI(String uri) {
		if (uri.contains("?tuple")) {
			return true;
		}
		return false;
	}

	protected MimeType detectMimeType(String uri) {
		return MimeType.JSON;
	}

	protected Model tuplifyJSON(URL url) throws IOException {

		JSONTransformer jt = new JSONTransformer(url.toString() + "/");
		return jt.transformJSONFromURL(url);

	}

}
