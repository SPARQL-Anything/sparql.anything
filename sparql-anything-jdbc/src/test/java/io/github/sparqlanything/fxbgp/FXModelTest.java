package io.github.sparqlanything.fxbgp;

import org.junit.Assert;
import org.junit.Test;

public class FXModelTest {
	private FXModel FXM = FXModel.getFXModel();
	@Test
	public void test(){
		Assert.assertTrue(FXM.inconsistent(FX.Subject, FX.Predicate));
	}
}
