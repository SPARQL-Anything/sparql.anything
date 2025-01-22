package io.github.sparqlanything.fxbgp;

import org.apache.jena.sparql.algebra.op.OpBGP;

public interface Interpretation {

	OpBGP getOpBGP();
}
