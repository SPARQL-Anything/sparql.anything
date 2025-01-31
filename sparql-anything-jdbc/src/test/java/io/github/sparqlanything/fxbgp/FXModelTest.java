package io.github.sparqlanything.fxbgp;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FXModelTest {
	private FXModel FXM;

	@Before
	public void before(){
		FXM = FXModel.getFXModel();
	}

	@Test
	public void inconsistency_1(){
		Assert.assertTrue(FXM.inconsistent(FX.Subject, FX.Predicate));
		Assert.assertTrue(FXM.inconsistent(FX.Predicate, FX.Subject));
	}

	@Test
	public void inconsistency_2(){
		Assert.assertTrue(FXM.inconsistent(FX.Object, FX.Predicate));
		Assert.assertTrue(FXM.inconsistent(FX.Predicate, FX.Object));
	}

	@Test
	public void consistency_1(){
		Assert.assertFalse(FXM.inconsistent(FX.Subject, FX.Object));
		Assert.assertFalse(FXM.inconsistent(FX.Object, FX.Subject));
	}

	@Test
	public void consistency_2(){
		Assert.assertFalse(FXM.inconsistent(FX.Subject, FX.Subject));
		Assert.assertFalse(FXM.inconsistent(FX.Predicate, FX.Predicate));
	}

	@Test
	public void inconsistency_3(){
		Assert.assertTrue(FXM.inconsistent(FX.Container, FX.Predicate));
		Assert.assertTrue(FXM.inconsistent(FX.Container, FX.Slot));
		Assert.assertTrue(FXM.inconsistent(FX.Container, FX.Type));
		Assert.assertTrue(FXM.inconsistent(FX.Container, FX.Root));
		Assert.assertTrue(FXM.inconsistent(FX.Container, FX.Value));
	}

	@Test
	public void consistency_3(){
		Assert.assertFalse(FXM.inconsistent(FX.Container, FX.Subject));
		Assert.assertFalse(FXM.inconsistent(FX.Container, FX.Object));
		Assert.assertFalse(FXM.inconsistent(FX.Slot, FX.SlotNumber));
		Assert.assertFalse(FXM.inconsistent(FX.Slot, FX.SlotString));
		// Instead...
		Assert.assertTrue(FXM.inconsistent(FX.Root, FX.Type));
	}

	public void inconsistency_4(){
		Assert.assertTrue(FXM.inconsistent(FX.SlotString, FX.Subject));
		Assert.assertTrue(FXM.inconsistent(FX.SlotString, FX.Object));
		Assert.assertTrue(FXM.inconsistent(FX.SlotString, FX.Type));
		Assert.assertTrue(FXM.inconsistent(FX.SlotString, FX.Type));
		Assert.assertTrue(FXM.inconsistent(FX.SlotString, FX.SlotNumber));
		Assert.assertTrue(FXM.inconsistent(FX.SlotNumber, FX.SlotString));
	}

	@Test
	public void isSpecialisedBy_1(){
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Subject, FX.Container));
		Assert.assertTrue(FXM.getSpecialisedBy(FX.Subject).contains(FX.Container));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Object, FX.Container));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Object, FX.Value));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Object, FX.Type));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Object, FX.Root));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Predicate, FX.TypeProperty));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Predicate, FX.Slot));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Slot, FX.SlotNumber));
		Assert.assertTrue(FXM.isSpecialisedBy(FX.Slot, FX.SlotString));
	}
	@Test
	public void isSpecialisationOf_1(){
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Container, FX.Subject));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Container, FX.Object));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Value, FX.Object));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Type, FX.Object));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Root, FX.Object));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.TypeProperty, FX.Predicate));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.Slot, FX.Predicate));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.SlotNumber, FX.Slot));
		Assert.assertTrue(FXM.isSpecialisationOf(FX.SlotString, FX.Slot));
	}

	@Test
	public void isGrounded_1(){
		Assert.assertTrue(FXM.isGrounded(FX.Container));
		Assert.assertTrue(FXM.isGrounded(FX.Value));
		Assert.assertTrue(FXM.isGrounded(FX.Root));
		Assert.assertTrue(FXM.isGrounded(FX.SlotString));
		Assert.assertTrue(FXM.isGrounded(FX.SlotNumber));
		Assert.assertTrue(FXM.isGrounded(FX.TypeProperty));
		Assert.assertTrue(FXM.isGrounded(FX.Type));
	}
}
