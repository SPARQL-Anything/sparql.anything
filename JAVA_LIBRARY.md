# SPARQL Anything as a Java Library

SPARQL Anything is available on [Maven Central](https://central.sonatype.com/search?namespace=io.github.sparql-anything).
Therefore, you can use SPARQL Anything as a Java library by including individual modules as dependencies in your project.
You can import SPARQL Anything with all of its modules by including the `engine` module in your dependencies.

```
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-engine</artifactId>
    <version>version</version>
</dependency>
```

Otherwise, if you only want to use one triplifier of a format (e.g. the JSON triplifier), you can just include the module of the format you are interested in.

```
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-json</artifactId>
    <version>version</version>
</dependency>
```



## Using via SPARQL

A way of using SPARQL Anything as a Java library is via Jena ARQ's APIs.
To this end, you just need to tell the ARQ's engine to use FacadeXOpExecutor as the default operation executor via the following line

```java
QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
```

A complete example showing how to execute a simple query is the following.

```java
package sparqlanything.user ;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;

public class SPARQLAnythingClientViaSPARQL {

    public static void main(String[] args){

        // Set FacadeX OpExecutor as default executor factory
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        // Execute the query by using standard Jena ARQ's API
        Dataset kb = DatasetFactory.createGeneral();

        Query query = QueryFactory.create(
                "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> " +
                        "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
                        "SELECT ?o { " +
                        "SERVICE <x-sparql-anything:> { " +
                        "fx:properties fx:content '[1,2,3]' ; " +
                        "fx:media-type 'application/json' . " +
                        "?s fx:anySlot ?o" +
                        "}}");

        System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,kb).execSelect()));


    }
}
```

which prints

```
-----------------------------------------------
| o                                           |
===============================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> |
-----------------------------------------------
```

A maven project showing how to SPARQL Anything as a Java library is available [here](https://github.com/SPARQL-Anything/JavaClientExample).

## Using via Triplifier API

You can also simply use the SPARQL Anything Triplifiers to transform a resource into an RDF dataset.
In this case, you just need to add the module addressing the format you want to transform.
Suppose, you want to triplify [this JSON file](https://sparql-anything.cc/examples/simpleArray.json) according to the Facade-X metamodel

```java
package sparqlanything.user;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;

import java.io.IOException;
import java.util.Properties;

public class SPARQLAnythingClientViaTriplifier {

    public static void main(String[] args) throws TriplifierHTTPException, IOException {

        // Instantiate the triplifier
        JSONTriplifier jsonTriplifier = new JSONTriplifier();

        // Set the triplifier options
        Properties options = new Properties();

        // Set the location of the resource
        options.setProperty(IRIArgument.LOCATION.toString(), "https://sparql-anything.cc/examples/simpleArray.json");

        // Create the builder
        FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(options);

        // Build the Facade-X DatasetGraph
        jsonTriplifier.triplify(options, builder);

        // Retrieve the Facade-X DatasetGraph
        DatasetGraph datasetGraph = builder.getDatasetGraph();

        // Use the Facade-X DatasetGraph via the Jena APIs
        RDFDataMgr.write(System.out, datasetGraph, Lang.TRIG);


    }
}
```

which prints

```
_:b0    a       <http://sparql.xyz/facade-x/ns/root> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                _:b1 ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                _:b2 ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                _:b3 .

_:b3    <http://sparql.xyz/facade-x/data/movie>
                "Kill Bill" ;
        <http://sparql.xyz/facade-x/data/name>
                "Beatrix" ;
        <http://sparql.xyz/facade-x/data/surname>
                "Kiddo" .

_:b2    <http://sparql.xyz/facade-x/data/movie>
                "Pulp fiction" ;
        <http://sparql.xyz/facade-x/data/name>
                "Winnfield" ;
        <http://sparql.xyz/facade-x/data/surname>
                "Vega" .

_:b1    <http://sparql.xyz/facade-x/data/movie>
                "Pulp fiction" ;
        <http://sparql.xyz/facade-x/data/name>
                "Vincent" ;
        <http://sparql.xyz/facade-x/data/surname>
                "Vega" .

<https://sparql-anything.cc/examples/simpleArray.json#> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root> ;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1 ;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2 ;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    _:b3 .
    
    _:b3    <http://sparql.xyz/facade-x/data/movie>
                    "Kill Bill" ;
            <http://sparql.xyz/facade-x/data/name>
                    "Beatrix" ;
            <http://sparql.xyz/facade-x/data/surname>
                    "Kiddo" .
    
    _:b2    <http://sparql.xyz/facade-x/data/movie>
                    "Pulp fiction" ;
            <http://sparql.xyz/facade-x/data/name>
                    "Winnfield" ;
            <http://sparql.xyz/facade-x/data/surname>
                    "Vega" .
    
    _:b1    <http://sparql.xyz/facade-x/data/movie>
                    "Pulp fiction" ;
            <http://sparql.xyz/facade-x/data/name>
                    "Vincent" ;
            <http://sparql.xyz/facade-x/data/surname>
                    "Vega" .
}
```


A maven project showing how to SPARQL Anything as a Java library is available [here](https://github.com/SPARQL-Anything/JavaClientViaTriplifiers).
