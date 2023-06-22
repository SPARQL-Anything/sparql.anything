package io.github.sparqlanything.html;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class HTMLBaseTest extends AbstractTriplifierTester {
	public HTMLBaseTest() {
		super(new HTMLTriplifier(), new Properties(), "html");
	}

	protected void properties(Properties properties) {
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "true");
	}

	@Test
	public void testBase() {
		this.assertResultIsIsomorphicWithExpected();
	}

}
