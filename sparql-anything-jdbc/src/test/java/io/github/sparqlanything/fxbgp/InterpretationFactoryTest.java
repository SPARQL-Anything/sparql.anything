package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;

public class InterpretationFactoryTest extends BGPTestAbstract {

	protected InterpretationFactory IF;

	@Rule
	public TestName name = new TestName();

	@Override
	public void before() {
		super.before();
		IF = new InterpretationFactory(new FXModel());
	}

	@Test
	public void testIF1() throws IOException {
		readBGP(name.getMethodName().substring(4));
		InterpretationOfBGP ibgp = IF.make(new OpBGP(bp()));

		Assert.assertTrue(ibgp.isStart());
		Assert.assertFalse(ibgp.isGrounded());

		Assert.assertTrue(ibgp.getInterpretationOfNodes().isEmpty());
	}

	@Test
	public void testIF2() throws IOException {
		readBGP(name.getMethodName().substring(4));
		InterpretationOfBGP ibgp = IF.make(new OpBGP(bp()));

		Assert.assertTrue(ibgp.isStart());
		Assert.assertFalse(ibgp.isGrounded());
		Assert.assertTrue(ibgp.getInterpretationOfNodes().isEmpty());

		// Let's interpret the first subject as a container
		Node s = ibgp.getOpBGP().getPattern().getList().iterator().next().getSubject();
		InterpretationOfBGP ibgp2 = IF.make(ibgp, s, FX.Container);
		Assert.assertFalse(ibgp2.isStart());
		Assert.assertEquals(ibgp, ibgp2.previous());
	}
}
