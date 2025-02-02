package io.github.sparqlanything.fxrdb;

import io.github.sparqlanything.fxbgp.Analyser;
import io.github.sparqlanything.fxbgp.AnalyserGrounder;
import io.github.sparqlanything.fxbgp.BGPTestAbstract;
import io.github.sparqlanything.fxbgp.FXModel;
import io.github.sparqlanything.fxbgp.InterpretationOfBGP;
import io.github.sparqlanything.jdbc.NodeInterpretation;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class RDBAnalyserTest extends BGPTestAbstract {
	public RDBAnalyserTest() {
		super(FXRDBModel.getFXRDBModel());
	}

	@Before
	public void setup() {
		setAnalyser(new AnalyserGrounder(properties, FXM()));
	}

	@Test
	public void var_var_var(){
		add(v("a"), v("b"), v("c"));
		runAnalyser();
		InterpretationOfBGP i1 = make(
			v("a"), FXRDB.ContainerEntity,
			v("b"), FXRDB.TypeProperty,
			v("c"), FXRDB.TypeTable
		);
		InterpretationOfBGP i2 = make(
			v("a"), FXRDB.ContainerTable,
			v("b"), FXRDB.TypeProperty,
			v("c"), FXRDB.Root
		);
		InterpretationOfBGP i3 = make(
			v("a"), FXRDB.ContainerEntity,
			v("b"), FXRDB.SlotColumn,
			v("c"), FXRDB.Cell
		);
		InterpretationOfBGP i4 = make(
			v("a"), FXRDB.ContainerTable,
			v("b"), FXRDB.SlotRow,
			v("c"), FXRDB.ContainerEntity
		);
		Assert.assertTrue(interpretations().contains(i1));
		Assert.assertTrue(interpretations().contains(i2));
		Assert.assertTrue(interpretations().contains(i3));
		Assert.assertTrue(interpretations().contains(i4));
		L.error("{}", interpretations());
		Assert.assertEquals(4, interpretations().size());
	}

	@Test
	public void var_rdftype_table(){
		// ?table a []
		add(t(v("container"), RDF.type.asNode(), xyz("table")));
		runAnalyser();
		InterpretationOfBGP i1 = make(
			v("container"), FXRDB.ContainerEntity,
			RDF.type.asNode(), FXRDB.TypeProperty,
			xyz("table"), FXRDB.TypeTable
		);
		Assert.assertEquals(1, interpretations().size());
		Assert.assertTrue(interpretations().contains(i1));
	}
}
