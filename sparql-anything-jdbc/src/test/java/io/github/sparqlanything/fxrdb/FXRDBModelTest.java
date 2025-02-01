package io.github.sparqlanything.fxrdb;

import io.github.sparqlanything.fxbgp.FX;
import io.github.sparqlanything.fxbgp.FXModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FXRDBModelTest {
	private FXModel FXM;

	@Before
	public void before(){
		FXM = FXRDBModel.getFXRDBModel();
	}

	@Test
	public void inconsistency_1(){
		Assert.assertTrue(FXM.inconsistent(FXRDB.ContainerTable, FXRDB.ContainerEntity));
		Assert.assertTrue(FXM.inconsistent(FXRDB.SlotRow, FXRDB.SlotColumn));
	}

	@Test
	public void inconsistency_2(){
		Assert.assertTrue(FXM.inconsistent(FXRDB.TypeTable, FXRDB.TypeProperty));
		Assert.assertTrue(FXM.inconsistent(FXRDB.TypeTable, FXRDB.Root));
	}

	@Test
	public void grounded(){
		Assert.assertTrue(FXM.isGrounded(FXRDB.TypeTable));
		Assert.assertTrue(FXM.isGrounded(FXRDB.TypeProperty));
		Assert.assertTrue(FXM.isGrounded(FXRDB.Root));
		Assert.assertTrue(FXM.isGrounded(FXRDB.ContainerTable));
		Assert.assertTrue(FXM.isGrounded(FXRDB.ContainerEntity));
		Assert.assertTrue(FXM.isGrounded(FXRDB.Cell));
		Assert.assertTrue(FXM.isGrounded(FXRDB.SlotRow));
		Assert.assertTrue(FXM.isGrounded(FXRDB.SlotColumn));
	}

	@Test
	public void notGrounded(){
		Assert.assertFalse(FXM.isGrounded(FXRDB.Type));
		Assert.assertFalse(FXM.isGrounded(FXRDB.Container));
		Assert.assertFalse(FXM.isGrounded(FXRDB.Object));
		Assert.assertFalse(FXM.isGrounded(FXRDB.SlotNumber));
		Assert.assertFalse(FXM.isGrounded(FXRDB.SlotString));
		Assert.assertFalse(FXM.isGrounded(FXRDB.Value));
		Assert.assertFalse(FXM.isGrounded(FXRDB.Slot));
		Assert.assertFalse(FXM.isGrounded(FXRDB.Predicate));
	}

	@Test
	public void specialization_1(){
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.Type, FXRDB.TypeTable));
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.Container, FXRDB.ContainerEntity));
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.Container, FXRDB.ContainerTable));
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.Value, FXRDB.Cell));
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.SlotNumber, FXRDB.SlotRow));
		Assert.assertTrue(FXM.isSpecialisedBy(FXRDB.SlotString, FXRDB.SlotColumn));
	}
}
