package io.github.sparqlanything.model;

import org.apache.jena.sparql.util.Context;

import java.util.HashMap;

public class SPARQLAnythingContext extends Context {

	private static final SPARQLAnythingContext instance ;

	static {
		instance = new SPARQLAnythingContext();
		instance.set(SPARQLAnythingConstants.PROFILE, new HashMap<SPARQLAnythingConstants.PROFILE_EVENT, Long>());
	}

	public static SPARQLAnythingContext getInstance(){
		return instance;
	}
}
