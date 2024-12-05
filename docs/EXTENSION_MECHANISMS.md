# Extension Mechanisms

You can extend SPARQL Anything by including custom triplifiers, functions or magic properties.
There are two ways of doing that [wrapping the extension into a new package](#wrapping-the-extension-into-a-new-package) or [develop a plugin](#develop-a-plugin).

## Wrapping the extension into a new package

To wrap  the extension into a new package, you have into include the `engine` module within your dependencies.

```
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-engine</artifactId>
    <version>version</version>
</dependency>
```

A maven project showing how to extend SPARQL Anything is available [here](https://github.com/SPARQL-Anything/JavaExtensionExample).

This project defines a new triplifier, called `MyTriplifier`, which reads the input resource byte-by-byte, transforms each byte into a character, and adds the character as a slot of the root container.
`MyTriplifier` is used to transform resources having mime type `my-mime-type` and extension `myext`.

Source code of the `MyTriplifier` class

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

Moreover, the project defines:
- a **function**, implemented by the class `TheAnswer.class`, which returns the integer 42 for any input; and,
- a **magic property**, implemented by the class `Assign42.class`, that assigns to the object of the triple pattern the string "42".

```java
package sparqlanything.user;
/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class TheAnswer extends FunctionBase1  {

	@Override
	public NodeValue exec(NodeValue nodeValue) {
		return NodeValue.makeInteger(42);
	}
}
```

```java
/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package sparqlanything.user;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.pfunction.PFuncAssignToObject;

public class Assign42 extends PFuncAssignToObject {

    @Override
    public Node calc(Node node) {
        return NodeFactory.createLiteralString("42");
    }
}
```

Then, you can register the new triplifier, the new function and the new magic property via the following lines.

```java
TriplifierRegister.getInstance().registerTriplifier(MyTriplifier.class.getCanonicalName(), new String[]{"myext"}, new String[]{"my-mime-type"});
FunctionRegistry.get().put("http://example.org/theAnswer", TheAnswer.class);
PropertyFunctionRegistry.get().put("http://example.org/assign42", Assign42.class);
```

Finally, you can use the Triplifier [as usual](README.md#usage). For example, via a Java client

```java
package sparqlanything.user ;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.TriplifierRegister;
import io.github.sparqlanything.model.TriplifierRegisterException;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.sys.JenaSystem;

public class SPARQLAnythingClientViaSPARQL {

    public static void main(String[] args) throws TriplifierRegisterException, TriplifierRegisterException {

        System.out.println(PropertyFunctionRegistry.chooseRegistry(ARQ.getContext()));

        JenaSystem.init();

        // Set FacadeX OpExecutor as default executor factory
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        // Register the new Triplifier
        TriplifierRegister.getInstance().registerTriplifier(MyTriplifier.class.getCanonicalName(), new String[]{"myext"}, new String[]{"my-mime-type"});

        // Register the new function
        FunctionRegistry.get().put("http://example.org/theAnswer", TheAnswer.class);

        // Register the new magic property
        PropertyFunctionRegistry.get().put("http://example.org/assign42", Assign42.class);

        // Execute the query by using standard Jena ARQ's API
        Dataset kb = DatasetFactory.createGeneral();

        Query query = QueryFactory.create(
                "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> " +
                        "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
                        "SELECT ?slotNumber ?o ?assignment ?answer{ " +
                        "SERVICE <x-sparql-anything:> { " +
                        "fx:properties fx:content 'abc' ; " +
                        "fx:media-type 'my-mime-type' . " +
                        "?s ?p ?o ." +
                        "?s <http://example.org/assign42> ?assignment " +
                        "BIND(fx:cardinal(?p) AS ?slotNumber) " +
                        "BIND(<http://example.org/theAnswer>(?p) AS ?answer) " +
                        "FILTER(BOUND(?slotNumber))" +
                        "}}");

        System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,kb).execSelect()));


    }
}

```

which prints

```
------------------------------------------
| slotNumber | o   | assignment | answer |
==========================================
| 1          | "a" | "42"       | 42     |
| 2          | "b" | "42"       | 42     |
| 3          | "c" | "42"       | 42     |
------------------------------------------
```

### Develop a plugin

You can integrate MyTriplifier, TheAnswer, Assign42 into SPARQL Anything by wrapping it in a separate JAR and dynamically loading it at runtime.

To this end, instead of importing the engine module, you need to import the `model` module.

```
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-model</artifactId>
    <version>version</version>
</dependency>
```

Then, you need to implement the `PluginInitializer` interface that will register the custom extensions.

```java
package sparqlanything.user;

import io.github.sparqlanything.model.PluginInitializer;
import io.github.sparqlanything.model.TriplifierRegister;
import io.github.sparqlanything.model.TriplifierRegisterException;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;

public class Extension implements PluginInitializer {

    @Override
    public void run() {
        try {
            // Register the new Triplifier
            TriplifierRegister.getInstance().registerTriplifier(MyTriplifier.class.getCanonicalName(), new String[]{"myext"}, new String[]{"my-mime-type"});
        } catch (TriplifierRegisterException e) {
            throw new RuntimeException(e);
        }

        // Register function and magic properties as in Apache Jena

        // Register the new function
        FunctionRegistry.get().put("http://example.org/theAnswer", TheAnswer.class);

        // Register the new magic property
        PropertyFunctionRegistry.get().put("http://example.org/assign42", Assign42.class);
    }
}
```

Then, you need to build the JAR wrapping all the classes and pass the path of the JAR to the CLI via the `-j` argument.

[This project](https://github.com/SPARQL-Anything/plugin-example) provides a complete example of plugin.

Finally, to load the plugin you need to pass the path of the JAR via the `-j` option of the CLI.

```bash
java -jar sparql.anything-<version> -q <query> -j /path/to/the/project/target/plugin-0.0.1-shaded.jar
```

