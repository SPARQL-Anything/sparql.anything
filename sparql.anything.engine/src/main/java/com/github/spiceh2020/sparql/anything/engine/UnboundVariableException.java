package com.github.spiceh2020.sparql.anything.engine;

import org.apache.jena.sparql.algebra.op.OpBGP;

public class UnboundVariableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String variableName;
	private OpBGP opBGP;

	public UnboundVariableException(String variableName, OpBGP op) {
		super();
		this.variableName = variableName;
		this.opBGP = op;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getVariableName() {
		return variableName;
	}

	public OpBGP getOpBGP() {
		return opBGP;
	}

}
