# Extension Mechanisms

You can extend SPARQL Anything by including new triplifiers.
To this end, you have into include the `engine` module within your dependencies.

```
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-engine</artifactId>
    <version>version</version>
</dependency>
```

A maven project showing how to extend SPARQL Anything is available [here](https://github.com/SPARQL-Anything/JavaExtensionExample).

This project includes defines a new triplifier, called MyTriplifier, which reads the input resource byte-by-byte, transforms each byte into a character, and adds the character as a slot of the root container.
MyTriplifier is used to transform resources having mime type `my-mime-type` and extension `myext`.

Source code of the MyTriplifier

```java
package sparqlanything.user;

import com.google.common.collect.Sets;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class MyTriplifier implements Triplifier {

    @Override
    public void triplify(Properties properties, FacadeXGraphBuilder facadeXGraphBuilder) throws IOException, TriplifierHTTPException {

        // Declare the identifier of the data source id, use the default data source id "".
        String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;

        // Get the identifier of the root container
        String rootId = Triplifier.getRootArgument(properties);

        // Get the input stream form the resource
        InputStream inputStream = Triplifier.getInputStream(properties);

        // add the root container
        facadeXGraphBuilder.addRoot(dataSourceId);

        // add slots to the root container
        int slot = 1;
        for (int byteRead = inputStream.read(); byteRead != -1; byteRead = inputStream.read()) {
            facadeXGraphBuilder.addValue(dataSourceId, rootId, slot++, (char) byteRead);
        }
        inputStream.close();
    }

    /*
    Define the mime types of the triplifier
     */
    @Override
    public Set<String> getMimeTypes() {
        return Sets.newHashSet("my-mime-type");
    }

    /*
    Define the mime types of the extensions
     */
    @Override
    public Set<String> getExtensions() {
        return Sets.newHashSet("myext");
    }
}

```

Then, you can register the new triplifier via the following line.

```
FacadeX.Registry.registerTriplifier(MyTriplifier.class.getCanonicalName(), new String[]{"myext"}, new String[]{"my-mime-type"});
```

Finally, you can use the Triplifier [as usual](README.md#usage). For example, via a Java client

```java
package sparqlanything.user ;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.TriplifierRegisterException;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;

public class SPARQLAnythingClientViaSPARQL {

	public static void main(String[] args) throws TriplifierRegisterException {

		// Set FacadeX OpExecutor as default executor factory
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		// Register the new Triplifier
		FacadeX.Registry.registerTriplifier(MyTriplifier.class.getCanonicalName(), new String[]{"myext"}, new String[]{"my-mime-type"});

		// Execute the query by using standard Jena ARQ's API
		Dataset kb = DatasetFactory.createGeneral();

		Query query = QueryFactory.create(
						"PREFIX fx:  <http://sparql.xyz/facade-x/ns/> " +
						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
						"SELECT ?slotNumber ?o { " +
								"SERVICE <x-sparql-anything:> { " +
									"fx:properties fx:content 'abc' ; " +
										"fx:media-type 'my-mime-type' . " +
									"?s ?p ?o " +
									"BIND(fx:cardinal(?p) AS ?slotNumber) " +
									"FILTER(BOUND(?slotNumber))" +
								"}}");

		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,kb).execSelect()));


	}
}
```

which prints

```
--------------------
| slotNumber | o   |
====================
| 1          | "a" |
| 2          | "b" |
| 3          | "c" |
--------------------
```