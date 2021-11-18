package com.github.sparqlanything.docs.test;

import java.util.Properties;

import org.junit.Test;

import com.github.sparqlanything.docs.DocxTriplifier;
import com.github.sparqlanything.testutils.AbstractTriplifierTester;

public class DocxTriplifierTest extends AbstractTriplifierTester {

	public DocxTriplifierTest() {
		super(new DocxTriplifier(), new Properties(), "docx");
		this.setPrintWholeGraph(true);
	}

	@Test
	public void testDoc1() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testDoc2() {
		assertResultIsIsomorphicWithExpected();
	}

}
