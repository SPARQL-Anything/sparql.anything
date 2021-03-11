package com.github.spiceh2020.sparql.anything.model.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TestTriplifierEscaper {

	@Test
	public void testEscaping() {
		assertEquals("asd%20%C3%85", Triplifier.toSafeURIString("asd Å"));
	}

}
