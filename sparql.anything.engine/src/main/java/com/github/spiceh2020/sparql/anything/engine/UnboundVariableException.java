package com.github.spiceh2020.sparql.anything.engine;

import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpTable;

public class UnboundVariableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String variableName;
	private OpBGP opBGP;
	private OpTable optable;

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

	public void setOpTable(OpTable optable) {
		this.optable = optable;
	}

	public OpTable getOpTable() {
		return optable;
	}

}
