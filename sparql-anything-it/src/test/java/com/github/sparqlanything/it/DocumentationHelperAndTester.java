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

package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class DocumentationHelperAndTester {


	public static void extractFunctionAndMagicProperties() throws IOException, URISyntaxException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryFileName = "DocExamples/extract.sparql";
		URI queryFile = DocumentationHelperAndTester.class.getClassLoader().getResource(queryFileName).toURI();
		String location = "https://raw.githubusercontent.com/SPARQL-Anything/sparql.anything/v0.8-DEV/FUNCTIONS_AND_MAGIC_PROPERTIES.md";
		String queryStringPattern = IOUtils.toString(queryFile, StandardCharsets.UTF_8);
		String queryString = String.format(queryStringPattern, location);

		Query extractionQuery = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(extractionQuery, ds);

		String outDirectoryPath = "FunctionsAndMagicProperties/";
		new File(outDirectoryPath).mkdirs();

		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String query = qs.getLiteral("query").getValue().toString();
			Query queryFunction = QueryFactory.create(query);
			String result = qs.getLiteral("result").getValue().toString();
			String functionName = qs.getLiteral("functionName").getValue().toString().replace(":", "").replace(".", "").toLowerCase();
			System.out.println(functionName);
			System.out.println(query);
			if (queryFunction.isSelectType()) {
				String actualResult = ResultSetFormatter.asText(QueryExecutionFactory.create(queryFunction, ds).execSelect());

				if (!actualResult.equals(result)) {
					System.err.println("Error");
				}
				System.out.println("Actual");
				System.out.println(actualResult);

			}
			System.out.println("Expected");
			System.out.println(result);
			System.out.println(new File(outDirectoryPath + functionName + ".sparql"));
			FileUtils.write(new File(outDirectoryPath + functionName + ".sparql"), query, "UTF-8");
			FileUtils.write(new File(outDirectoryPath + functionName + ".csv"), result, "UTF-8");
		}
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		extractFunctionAndMagicProperties();
	}

}
