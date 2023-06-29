package io.github.sparqlanything.engine;

import org.apache.jena.sparql.util.Symbol;

public class FXSymbol extends Symbol {
	protected FXSymbol(String symbol) {
		super(symbol);
	}

	public static FXSymbol create(String symbol){
		return new FXSymbol(symbol);
	}
}
