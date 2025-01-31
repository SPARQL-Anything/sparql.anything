package io.github.sparqlanything.fxbgp;

import org.apache.jena.sparql.algebra.op.OpBGP;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class AnalyserTest extends BGPTestAbstract {
	final protected static Logger L = LoggerFactory.getLogger(AnalyserTest.class);

	@Rule
	public TestName name = new TestName();

	private Analyser ANA = null;

	@Override
	public void before() {
		super.before();
		//ANA = new AnalyserAsSearch(properties, new FXModel());
		ANA = new AnalyserGrounder(properties, new FXModel());
	}

	@Test
	public void AT1() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(6,ibgps.size());
		for(InterpretationOfBGP fi: ibgps){
			L.error("{}",fi.toString());
			//Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void AT2() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(36,ibgps.size());
		for(InterpretationOfBGP fi: ibgps){
			L.error("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void AT3() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		L.error("Size: {}",ibgps.size());
		for(InterpretationOfBGP fi: ibgps){
			L.error("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void NS1() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(0, ibgps.size());
	}

	@Test
	public void NS2() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		L.info("{}",ibgps);
		Assert.assertEquals(0, ibgps.size());
	}

	@Test
	public void NS3() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(0, ibgps.size());
	}


	@Test
	public void NS4() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(0, ibgps.size());
	}


	@Test
	public void NS5() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		Assert.assertEquals(0, ibgps.size());
	}

	@Test
	public void BGP_X() throws IOException {
		readBGP(name.getMethodName());
		Set<InterpretationOfBGP> ibgps = ANA.interpret(new OpBGP(bp()));
		L.error("size: {}",ibgps.size());
		//Assert.assertEquals(36,ibgps.size());
		for(InterpretationOfBGP fi: ibgps){
			L.error("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void trythis() throws IOException {
		readBGP("AT1");
		Analyser analyser = new AnalyserGrounder(properties, new FXModel());
		analyser.interpret(new OpBGP(bp()));
	}
}
