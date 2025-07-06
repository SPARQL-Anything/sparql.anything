package io.github.sparqlanything.fxbgp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalyserTest extends BGPTestAbstract {
	final protected static Logger L = LoggerFactory.getLogger(AnalyserTest.class);

	@Rule
	public TestName name = new TestName();
	public AnalyserTest() {
		super(FXModel.getFXModel());
	}

	@Override
	public void before() {
		super.before();
		setAnalyser(new AnalyserGrounder(properties, FXM()));
		//setAnalyser(new AnalyserAsSearch(properties, FXM()));
	}

	@Test
	public void AT1() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(6,interpretations().size());
		for(InterpretationOfBGP fi: interpretations()){
			L.info("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void AT2() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(36,interpretations().size());
		for(InterpretationOfBGP fi: interpretations()){
			L.info("AT2 -- {}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void AT3() throws IOException {
		readBGP(name.getMethodName());
		L.info("Size: {}",interpretations().size());
		for(InterpretationOfBGP fi: interpretations()){
			L.info("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void NS1() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS2() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS3() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}


	@Test
	public void NS4() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}


	@Test
	public void NS5() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS6() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS7() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS8() throws IOException {
		// Unique path to container as object
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS9() throws IOException {
		// Unique fx:root in BGP
		readBGP(name.getMethodName());
		//System.err.println(interpretations());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS10() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS11() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS12() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS13() throws IOException {
		readBGP(name.getMethodName());
		Assert.assertEquals(0, interpretations().size());
	}

	@Test
	public void NS14() throws IOException {
		readBGP(name.getMethodName());
		//System.out.println(interpretations());
		Assert.assertEquals(0, interpretations().size());
	}


	@Test
	public void NS15() throws IOException {
		readBGP(name.getMethodName());
		//System.out.println(interpretations());
		Assert.assertEquals(0, interpretations().size());
	}


	@Test
	public void NS16() throws IOException {
		readBGP(name.getMethodName());
		//System.out.println(interpretations());
		Assert.assertEquals(0, interpretations().size());
	}


	@Test
	public void BGP_1_allGrounded() throws IOException {
		readBGP("BGP_1");
		L.info("size: {}",interpretations().size());
		for(InterpretationOfBGP fi: interpretations()){
			L.info("{}",fi.toString());
			Assert.assertTrue(fi.isGrounded());
		}
	}

	@Test
	public void BGP_2() throws IOException {
		readBGP(name.getMethodName());
		L.info("size: {}",interpretations().size());
		Assert.assertEquals(2,interpretations().size());
		InterpretationOfBGP i1 = make(
			v("s"), FX.Container,
			u("http://www.example.org/address"), FX.SlotString,
			v("address"), FX.Container,
			u("http://www.example.org/Person"), FX.Type,
			RDF.type.asNode(), FX.TypeProperty,
			u("http://www.example.org/id"), FX.SlotString,
			v("addressId"), FX.Value
		);
		InterpretationOfBGP i2 = make(
			v("s"), FX.Container,
			u("http://www.example.org/address"), FX.SlotString,
			v("address"), FX.Container,
			u("http://www.example.org/Person"), FX.Type,
			RDF.type.asNode(), FX.TypeProperty,
			u("http://www.example.org/id"), FX.SlotString,
			v("addressId"), FX.Container
		);
		Assert.assertTrue(interpretations().contains(i1));
		Assert.assertTrue(interpretations().contains(i2));
	}

	//@Ignore // This is for development only
	@Test
	public void BGP_X() throws IOException {
//		readBGP(name.getMethodName());
//		L.info("size: {}",interpretations().size());
//		System.out.println(interpretations());

		Set product = Sets.cartesianProduct(ImmutableList.of(ImmutableSet.of("a","b"), ImmutableSet.of("a", "b")));
		//System.out.println(product);
		for(Object lo:product){
			List<String> ls = (List<String>) lo;
			String l = ls.get(0);
			String r = ls.get(1);
			//System.out.println(l.equals(r));
		}
	}

	@Test
	public void var_var_var(){
		add(v("x"), v("p"), v("f"));
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

		Assert.assertTrue(interpretations().contains(i1));
		Assert.assertTrue(interpretations().contains(i2));
		Assert.assertTrue(interpretations().contains(i3));
		Assert.assertTrue(interpretations().contains(i4));
	}

	@Test
	public void var_rdftype_var(){
		Node T = RDF.type.asNode();
		add(v("x"), T , v("f"));
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
		Assert.assertTrue(interpretations().contains(i1));
		Assert.assertTrue(interpretations().contains(i2));
		// No other possible
		Assert.assertTrue(interpretations().size() == 2);
	}

}
