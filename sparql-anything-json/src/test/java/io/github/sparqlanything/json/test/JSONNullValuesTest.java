package io.github.sparqlanything.json.test;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class JSONNullValuesTest extends AbstractTriplifierTester {

	public JSONNullValuesTest() {
		super(new JSONTriplifier(), new Properties(), "json");
	}

	@Override
	protected void properties(Properties properties) {

		if (name.getMethodName().equals("testNullValues")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty(JSONTriplifier.PROPERTY_JSONINCLUDENULLVALUES.toString(), "true");
		}

		if (name.getMethodName().equals("testNullValues_2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty(JSONTriplifier.PROPERTY_JSONINCLUDENULLVALUES.toString(), "true");
		}
	}

	@Test
	public void testNullValues(){
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testNullValues_2(){
		assertResultIsIsomorphicWithExpected();
	}
}
