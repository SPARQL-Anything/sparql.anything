package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class HeadersRowTest extends AbstractTriplifierTester {

	public HeadersRowTest() {
		super(new CSVTriplifier(), new Properties(), "csv");
		this.printWholeGraph = true;
	}

	public void properties(Properties properties) {
		if (name.getMethodName().equals("testHeadersRow")) {
			properties.setProperty(CSVTriplifier.PROPERTY_HEADER_ROW.toString(), "4");
			properties.setProperty(CSVTriplifier.PROPERTY_HEADERS.toString(), "true");
		}
	}

	@Test
	public void testHeadersRow(){
		this.assertResultIsIsomorphicWithExpected();
	}


}
