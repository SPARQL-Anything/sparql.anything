package io.github.sparqlanything.fxbgp;

import io.github.sparqlanything.fxrdb.FXRDBModel;
import io.github.sparqlanything.jdbc.NodeInterpretation;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AnalyserTest extends BGPTestAbstract {
	final protected static Logger L = LoggerFactory.getLogger(AnalyserTest.class);

	@Rule
	public TestName name = new TestName();

	private Analyser ANA = null;

	public AnalyserTest() {
		super(FXModel.getFXModel());
	}

	@Override
	public void before() {
		super.before();
		ANA = new AnalyserGrounder(properties, FXM());
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
	public void var_var_var(){
		add(v("x"), v("p"), v("f"));
		Set<InterpretationOfBGP> ints = ANA.interpret(opBGP());
		// We expect the following interpretation to be present
		// Container TypeProperty Type
		InterpretationOfBGP i1 = make(
			v("x"), FX.Container,
			v("p"), FX.TypeProperty,
			v("f"), FX.Type
		);
		InterpretationOfBGP i2 = make(
			v("x"), FX.Container,
			v("p"), FX.SlotNumber,
			v("f"), FX.Container
		);
		InterpretationOfBGP i3 = make(
			v("x"), FX.Container,
			v("p"), FX.SlotNumber,
			v("f"), FX.Value
		);
		InterpretationOfBGP i4 = make(
			v("x"), FX.Container,
			v("p"), FX.TypeProperty,
			v("f"), FX.Root
		);
		Assert.assertTrue(ints.contains(i1));
		Assert.assertTrue(ints.contains(i2));
		Assert.assertTrue(ints.contains(i3));
		Assert.assertTrue(ints.contains(i4));
	}

	@Test
	public void var_rdftype_var(){
		Node T = RDF.type.asNode();
		add(v("x"), T , v("f"));
		Set<InterpretationOfBGP> ints = ANA.interpret(opBGP());
		// We expect the following interpretation to be present
		// Container TypeProperty Type
		InterpretationOfBGP i1 = make(
			v("x"), FX.Container,
			T, FX.TypeProperty,
			v("f"), FX.Type
		);
		InterpretationOfBGP i2 = make(
			v("x"), FX.Container,
			T, FX.TypeProperty,
			v("f"), FX.Root
		);
		Assert.assertTrue(ints.contains(i1));
		Assert.assertTrue(ints.contains(i2));
		// No other possible
		Assert.assertTrue(ints.size() == 2);
	}

}
