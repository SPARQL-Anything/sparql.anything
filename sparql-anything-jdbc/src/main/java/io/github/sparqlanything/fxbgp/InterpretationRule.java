package io.github.sparqlanything.fxbgp;

import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.function.Function;

public interface InterpretationRule {
	abstract Interpretation infer();
}
