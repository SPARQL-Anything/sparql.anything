package io.github.sparqlanything.fxbgp;

import io.github.sparqlanything.jdbc.NodeInterpretation;

import java.util.Set;

public interface InterpretationOfNode extends Interpretation{
	Set<Class<? extends NodeInterpretation>> inconsistentWith();

	Set<Class<? extends NodeInterpretation>> specialisationOf();
}
