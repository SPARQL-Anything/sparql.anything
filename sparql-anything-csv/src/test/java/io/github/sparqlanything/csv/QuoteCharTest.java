package io.github.sparqlanything.csv;

import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class QuoteCharTest extends AbstractTriplifierTester {

	public QuoteCharTest() {
		super(new CSVTriplifier(), new Properties(), "csv");
	}

	public void properties(Properties properties) {
		properties.setProperty(CSVTriplifier.PROPERTY_HEADERS.toString(), "true");
		if (name.getMethodName().startsWith("testNoQuote")) {
			properties.setProperty(CSVTriplifier.PROPERTY_DELIMITER.toString(), "\t");
			if (name.getMethodName().equals("testNoQuote$False")) {
				properties.setProperty(CSVTriplifier.PROPERTY_QUOTE_CHAR.toString(), "false");
			} else if (name.getMethodName().equals("testNoQuote$EmptyString")) {
				properties.setProperty(CSVTriplifier.PROPERTY_QUOTE_CHAR.toString(), "");
			}
		} else if (name.getMethodName().equals("testQuoteCharTrue")) {
			properties.setProperty(CSVTriplifier.PROPERTY_QUOTE_CHAR.toString(), "true");
		}
	}

	@Test
	public void testNoQuote$False() {
		this.assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testNoQuote$EmptyString() {
		this.assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testQuoteCharTrue() {
		this.assertResultIsIsomorphicWithExpected();
	}
}
