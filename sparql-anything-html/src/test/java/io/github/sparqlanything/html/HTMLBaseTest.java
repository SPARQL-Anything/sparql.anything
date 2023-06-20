package io.github.sparqlanything.html;

import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class HTMLBaseTest extends AbstractTriplifierTester {
	public HTMLBaseTest() {
		super(new HTMLTriplifier(), new Properties(), "html");
		this.printWholeGraph = true;
	}

	protected void properties(Properties properties) {
		properties.setProperty("blank-nodes", "true");
	}

	@Test
	public void testBase() {
		this.assertResultIsIsomorphicWithExpected();
	}

}
