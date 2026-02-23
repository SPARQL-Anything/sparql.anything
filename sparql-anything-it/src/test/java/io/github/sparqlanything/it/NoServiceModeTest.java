package io.github.sparqlanything.it;

import io.github.sparqlanything.cli.SPARQLAnything;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoServiceModeTest {
	private static Logger L = LoggerFactory.getLogger(NoServiceModeTest.class);

	@Test
	public void noService() throws Exception {
		String location = getClass().getClassLoader().getResource("./test/test.csv").getPath();
		String query = getClass().getClassLoader().getResource("./noservice/abc.sparql").getPath();
		L.info(location);
		L.info(query);
		String output = SPARQLAnything.callMain(
			new String[]{"-q",
				query,
				"-c",
				"strategy=2",
				"-c",
				"location=" + location,
				"-o",
				"target/noservice.csv"
			}
		);
		System.out.println(output);
	}
}
