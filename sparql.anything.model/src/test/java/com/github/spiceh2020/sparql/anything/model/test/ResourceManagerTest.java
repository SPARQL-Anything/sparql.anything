package com.github.spiceh2020.sparql.anything.model.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.model.ResourceManager;

public class ResourceManagerTest {

	@Test
	public void test() {
		ResourceManager rm = ResourceManager.getInstance();
		URL urlArchive;
		try {
			urlArchive = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			InputStream is = rm.getInputStreamFromArchive(urlArchive, "test/test.csv", Charset.defaultCharset());
			String expected = "Year,Make,Model,Description,Price\n" + "1997,Ford,E350,\"ac, abs, moon\",3000.00\n"
					+ "1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00\n"
					+ "1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",,5000.00\n"
					+ "1996,Jeep,Grand Cherokee,\"MUST SELL!\n" + "air, moon roof, loaded\",4799.00";
			assertEquals(expected, new String(is.readAllBytes()));
		} catch (MalformedURLException | URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArchiveException e) {
			e.printStackTrace();
		}

	}

}
