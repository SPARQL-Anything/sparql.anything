package io.github.sparqlanything.spreadsheet.test;

import io.github.sparqlanything.spreadsheet.SpreadsheetTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class HeadersRowTest  extends AbstractTriplifierTester  {

	public HeadersRowTest() {
		super(new SpreadsheetTriplifier(), new Properties(), "xls", "trig");
	}

	@Test
	public void testHeaderRow(){
		assertResultIsIsomorphicWithExpected();
	}

	@Override
	protected void properties(Properties properties) {

		if(name.getMethodName().equals("testHeaderRow")){
			properties.setProperty(SpreadsheetTriplifier.PROPERTY_HEADER_ROW.toString(), "4");
			properties.setProperty(SpreadsheetTriplifier.PROPERTY_HEADERS, "true");
		}

	}
}
