/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sparqlanything.model.test;

import com.github.sparqlanything.model.ResourceManager;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

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
