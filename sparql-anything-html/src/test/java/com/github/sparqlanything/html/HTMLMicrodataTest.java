package com.github.sparqlanything.html;

import java.util.Properties;

import org.junit.Test;

import com.github.sparqlanything.testutils.AbstractTriplifierTester;

public class HTMLMicrodataTest extends AbstractTriplifierTester {

	public HTMLMicrodataTest() {
		super(new HTMLTriplifier(), new Properties(), "html", "nq");
		this.setPrintWholeGraph(true);
	}

	@Test
	public void testMicrodata1() {
		this.assertResultIsIsomorphicWithExpected();
	}
	
	protected void properties(Properties properties) {
		properties.setProperty(HTMLTriplifier.PROPERTY_METADATA, "true");
	}

}
