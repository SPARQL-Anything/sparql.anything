package io.github.sparqlanything.fxrdb;

import io.github.sparqlanything.fxbgp.Analyser;
import io.github.sparqlanything.fxbgp.AnalyserGrounder;
import io.github.sparqlanything.fxbgp.BGPTestAbstract;
import io.github.sparqlanything.fxbgp.FXModel;
import io.github.sparqlanything.fxbgp.InterpretationOfBGP;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class RDBAnalyserTest extends BGPTestAbstract {

	protected Analyser ANA = null;

	public RDBAnalyserTest() {
		super(FXRDBModel.getFXRDBModel());
	}

	@Before
	public void setup() {
		ANA = new AnalyserGrounder(properties, FXM());
	}

	@Test
	public void var_var_var(){
		add(v("x"), v("p"), v("f"));


	}
}
