[![DOI](https://zenodo.org/badge/303967701.svg)](https://zenodo.org/badge/latestdoi/303967701)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![How to](https://img.shields.io/badge/How%20to-cite-green.svg)](#how-to-cite-our-work)
[![Java 11](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java11.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java11.yml)
[![Java 14](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java14.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java14.yml)
[![Java 14](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java17.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java17.yml)

# SPARQL Anything
SPARQL Anything is a system for Semantic Web re-engineering that allows users to ... query anything with SPARQL.

Main features:

- Query files in plain SPARQL 1.1, via the `SERVICE <x-sparql-anything:>` (see [configuration](#Configuration)) and build knowledge graphs with `CONSTRUCT` queries
- [Supported formats](#supported-formats): XML, JSON, CSV, HTML, Excel, Text, Binary, EXIF, File System, Zip/Tar, Markdown, YAML, Bibtex, DOCx (see [pages dedicated to single formats](#supported-formats))
- Transforms [files, inline content, or the output of an external command](#general-purpose-options)
- Generates RDF, RDF-Star, and tabular data (thanks to SPARQL) 
- Full fledged [HTTP client](Configuration.md#http-options) to query Web APIs (headers, authentication, all methods supported)
- [Functions library](#functions-and-magic-properties) for RDF sequences, strings, hashes, easy entity building, ...
- Combine multiple SERVICE clauses into complex data integration queries (thanks to SPARQL)
- Query templates (using [BASIL variables](#query-templates-and-variable-bindings))
- Save and reuse SPARQL `Results Sets` as input for [parametric queries](#query-templates-and-variable-bindings)
- Slice large CSV files with an iterator-like execution style (soon [JSON](https://github.com/SPARQL-Anything/sparql.anything/issues/202) and [XML](https://github.com/SPARQL-Anything/sparql.anything/issues/203))
- Supports an [on-disk option](#Configuration) (with Apache Jena TDB2)

## Quickstart
SPARQL Anything uses a single generic abstraction for all data source formats called Facade-X.
### Facade-X
Facade-X is a simplistic meta-model used by SPARQL Anything transformers to generate RDF data from diverse data sources.
Intuitively, Facade-X uses a subset of RDF as a general approach to represent the source content *as-it-is* but in RDF.
The model combines two types of elements: containers and literals.
Facade-X always has  a single root container.
Container members are a combination of key-value pairs, where keys are either RDF properties or container membership properties.
Instead, values can be either RDF literals or other containers.
This is a generic example of a Facade-X data object (more examples below):

```turtle
@prefix fx: <http://sparql.xyz/facade-x/ns/> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
[] a fx:root ; rdf:_1 [
    xyz:someKey "some value" ;
    rdf:_1 "another value with unspecified key" ;
    rdf:_2 [
        rdf:type xyz:MyType ;
        rdf:_1 "another value"
    ]
] .
```

### Querying anything
SPARQL Anything extends the Apache Jena ARQ processors by *overloading* the SERVICE operator, as in the following example:

Suppose having this JSON file as input (also available at ``https://sparql-anything.cc/example1.json``)

```json
[
  {
    "name":"Friends",
    "genres":[
      "Comedy",
      "Romance"
    ],
    "language":"English",
    "status":"Ended",
    "premiered":"1994-09-22",
    "summary":"Follows the personal and professional lives of six twenty to thirty-something-year-old friends living in Manhattan.",
    "stars":[
      "Jennifer Aniston",
      "Courteney Cox",
      "Lisa Kudrow",
      "Matt LeBlanc",
      "Matthew Perry",
      "David Schwimmer"
    ]
  },
  {
    "name":"Cougar Town",
    "genres":[
      "Comedy",
      "Romance"
    ],
    "language":"English",
    "status":"Ended",
    "premiered":"2009-09-23",
    "summary":"Jules is a recently divorced mother who has to face the unkind realities of dating in a world obsessed with beauty and youth. As she becomes older, she starts discovering herself.",
    "stars":[
      "Courteney Cox",
      "David Arquette",
      "Bill Lawrence",
      "Linda Videtti Figueiredo",
      "Blake McCormick"
    ]
  }
]
```

With SPARQL Anything you can select the TV series starring "Courteney Cox" with the SPARQL query

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

and get this result without caring of transforming JSON to RDF.

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

### Using the Command Line Interface
SPARQL Anything requires `Java >= 11` to be installed in your operating system.
Download the latest version of the SPARQL Anything command line from the [releases page](https://github.com/SPARQL-Anything/sparql.anything/releases).
The command line is a file named `sparql-anything-<version>.jar`. 
Prepare a file with the query above and name it, for example `query.sparql`.
The query can be executed as follows:

```bash
java -jar sparql-anything-0.7.0.jar -q query.sparql
```
See the [usage section](#Usage) for details on the command line interface.

### Using the server
SPARQL Anything is also released as a server, embedded into an instance of the Apache Jena Fuseki server.
The server requires `Java >= 11` to be installed in your operating system.
Download the latest version of the SPARQL Anything server from the [releases page](https://github.com/SPARQL-Anything/sparql.anything/releases).
The command line is a file named `sparql-anything-server-<version>.jar`.

Run the server as follows:

```bash
$ java -jar sparql-anything-server-0.7.0.jar 
[main] INFO com.github.sparqlanything.fuseki.Endpoint - sparql.anything endpoint
[main] INFO com.github.sparqlanything.fuseki.Endpoint - Starting sparql.anything endpoint..
[main] INFO com.github.sparqlanything.fuseki.Endpoint - The server will be listening on http://localhost:3000/sparql.anything
[main] INFO com.github.sparqlanything.fuseki.Endpoint - The server will be available on http://localhost:3000/sparql
[main] INFO org.eclipse.jetty.server.Server - jetty-10.0.6; built: 2021-06-29T15:28:56.259Z; git: 37e7731b4b142a882d73974ff3bec78d621bd674; jvm 11.0.10+9
[main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@782a4fff{org.apache.jena.fuseki.Servlet,/,null,AVAILABLE}
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@c7a975a{HTTP/1.1, (http/1.1)}{0.0.0.0:3000}
[main] INFO org.eclipse.jetty.server.Server - Started Server@35beb15e{STARTING}[10.0.6,sto=0] @889ms
[main] INFO org.apache.jena.fuseki.Server - Start Fuseki (http=3000)

```
Access the SPARQL UI at the address `http://localhost:3000/sparql`, where you can copy the query above and execute it.
See the [usage section](#Usage) for details on the SPARQL Anything Fuseki server.

## Supported Formats
Currently, SPARQL Anything supports the following list of formats but the possibilities are limitless!
The data is interpreted as in the following examples (using default settings).

A detailed description of the interpretation can be found in the following pages:

- [JSON](formats/JSON.md)
- [HTML](formats/HTML.md)
- [CSV](formats/CSV.md)
- [XML](formats/XML.md)
- [Binary](formats/Binary.md)
- [TXT](formats/TXT.md)
- [Markdown](formats/Markdown.md)
- [File system and archives (ZIP, Tar)](formats/Archive.md)
- [Spreadsheets: XLS, XLSx](formats/Spreadsheet.md)
- [Documents: DOCx](formats/Word_Processing_Document.md)
- [EXIF Metadata](formats/Metadata.md)
- [Bibtex](formats/Bibtex.md)
- [YAML](formats/YAML.md)

... and, of course, the triples generated from the these formats can be integrated with the content of any [RDF Static file](formats/RDF_Files.md)

## Configuration

SPARQL Anything behaves as a standard SPARQL query engine.
For example, the SPARQL Anything server will act as a virtual endpoint that can be queried exactly as a remote SPARQL endpoint.
In addition, SPARQL Anything provides a rich Command Line Interface (CLI).
For information for how to run SPARQL Anything, please see the [quickstart](README.md#Quickstart) and [usage](README.md#usage) sections of the documentation.

### Passing triplification options via SERVICE IRI

In order to instruct the query processor to delegate the execution to SPARQL Anything, you can use the  following IRI-schema within SERVICE clauses.

```
x-sparql-anything ':' ([option] ('=' [value])? ','?)+
```

A minimal URI that uses only the resource locator is also possible.

```
x-sparql-anything ':' URL
```

In this case SPARQL Anything guesses the data source type from the file extension.


### Passing triplification options via Basic Graph Pattern

Alternatively, options can be provided as basic graph pattern inside the SERVICE clause as follows

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:> {
        fx:properties fx:location "https://sparql-anything.cc/example1.json" .
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

Note that

1. The SERVICE IRI scheme must be ``x-sparql-anything:``.
2. Each triplification option to pass to the engine corresponds to a triple of the Basic Graph Pattern inside the SERVICE clause.
3. Such triples must have ``fx:properties`` as subject, ``fx:[OPTION-NAME]`` as predicate, and a literal or a variable as object.

You can also mix the two modalities as follows.

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:blank-nodes=false> {
        fx:properties fx:location "https://sparql-anything.cc/example1.json" .
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

### General purpose options

| Option name                                   | Description                                                                                                                                                                                                                                                                                                                                   | Valid Values                                                                                                                                                                          | Default Value                                                                                                                                        |
|-----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| [location](Configuration.md#location)*        | The URL of the data source.                                                                                                                                                                                                                                                                                                                   | Any valid URL or (absolute or relative) path of the file system.                                                                                                                      | \*                                                                                                                                                   |
| [content](Configuration.md#content)*          | The content to be transformed.                                                                                                                                                                                                                                                                                                                | Any valid literal.                                                                                                                                                                    | \*                                                                                                                                                   |
| [command](Configuration.md#command)*          | An external command line to be executed. The output is handled according to the option 'media-type'                                                                                                                                                                                                                                           | Any valid literal.                                                                                                                                                                    | \*                                                                                                                                                   |
| [from-archive](Configuration.md#from-archive) | The filename of the resource to be triplified within an archive.                                                                                                                                                                                                                                                                              | Any filename.                                                                                                                                                                         | No value                                                                                                                                             |
| [root](Configuration.md#root)                 | The IRI of generated root resource.                                                                                                                                                                                                                                                                                                           | Any valid IRI.                                                                                                                                                                        | location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set) |
| [media-type](Configuration.md#media-type)     | The media-type of the data source.                                                                                                                                                                                                                                                                                                            | Any valid [Media-Type](https://en.wikipedia.org/wiki/Media_type).  Supported media types are specified in the [pages dedicated to the supported formats](README.md#supported-formats) | No value (the media-type will be guessed from the the file extension)                                                                                |
| [namespace](Configuration.md#namespace)       | The namespace prefix for the properties that will be generated.                                                                                                                                                                                                                                                                               | Any valid namespace prefix.                                                                                                                                                           | http://sparql.xyz/facade-x/data/                                                                                                                     |
| blank-nodes                                   | It tells sparql.anything to generate blank nodes or not.                                                                                                                                                                                                                                                                                      | true/false                                                                                                                                                                            | true                                                                                                                                                 |
| trim-strings                                  | Trim all string literals.                                                                                                                                                                                                                                                                                                                     | true/false                                                                                                                                                                            | false                                                                                                                                                |
| null-string                                   | Do not produce triples where the specificed string would be in the object position of the triple.                                                                                                                                                                                                                                             | any string                                                                                                                                                                            | not set                                                                                                                                              |
| http.*                                        | A set of options for customising HTTP request method, headers, querystring, and others. [More details on the HTTP request configuration](Configuration.md#HTTP Options)                                                                                                                                                                       | No value                                                                                                                                             |
| triplifier                                    | It forces sparql.anything to use a specific triplifier for transforming the data source                                                                                                                                                                                                                                                       | A canonical name of a Java class                                                                                                                                                      | No value                                                                                                                                             |
| charset                                       | The charset of the data source.                                                                                                                                                                                                                                                                                                               | Any charset.                                                                                                                                                                          | UTF-8                                                                                                                                                |
| metadata                                      | It tells sparql.anything to extract metadata from the data source and to store it in the named graph with URI &lt;http://sparql.xyz/facade-x/data/metadata&gt;                                                                                                                                                                                | true/false                                                                                                                                                                            | false                                                                                                                                                |
| ondisk                                        | It tells sparql.anything to use an on disk graph (instead of the default in memory graph). The string should be a path to a directory where the on disk graph will be stored. Using an on disk graph is almost always slower (than using the default in memory graph) but with it you can triplify large files without running out of memory. | a path to a directory                                                                                                                                                                 | not set                                                                                                                                              |
| ondisk.reuse                                  | When using an on disk graph, it tells sparql.anything to reuse the previous on disk graph.                                                                                                                                                                                                                                                    | true                                                                                                                                                                                  | not set                                                                                                                                              |
| strategy                                      | The execution strategy. 0 = in memory, all triples; 1 = in memory, only triples matching any of the triple patterns in the where clause                                                                                                                                                                                                       | 0,1                                                                                                                                                                                   | 1                                                                                                                                                    |
| slice                                         | The resources is sliced and the SPARQL query executed on each one of the parts. Supported by: CSV (row by row); JSON (when array slice by item, when json object requires `json.path`); XML (requires `xml.path`)                                                                                                                             | true/false                                                                                                                                                                            | false                                                                                                                                                |
| use-rdfs-member                               | It tells SPARQL Anything to use the (super)property rdfs:member instead of container membership properties (rdf:_1, rdf:_2 ...)                                                                                                                                                                                                               | true/false                                                                                                                                                                            | false                                                                                                                                                |

\* It is mandatory to provide either `location`, `content`, or `command`.

[More details on configuration](Configuration.md)

## Query templates and variable bindings (CLI only)

The SPARQL Anything CLI supports parametrised queries.
SPARQL Anything uses the [BASIL convention for variable names in queries](https://github.com/basilapi/basil/wiki/SPARQL-variable-name-convention-for-WEB-API-parameters-mapping).

The syntax is based on the underscore character: '_', and can be easily learned by examples:

- `?_name` The variable specifies the API mandatory parameter _name_. The value is incorporated in the query as plain literal.
- `?__name` The parameter _name_ is optional.
- `?_name_iri` The variable is substituted with the parameter value as a IRI.
- `?_name_en` The parameter value is considered as literal with the language 'en' (e.g., en,it,es, etc.).
- `?_name_integer` The parameter value is considered as literal and the XSD datatype 'integer' is added during substitution.
- `?_name_prefix_datatype` The parameter value is considered as literal and the datatype 'prefix:datatype' is added during substitution. The prefix must be specified according to the SPARQL syntax.

Variable bindings can be passed in two ways via the CLI argument `-v|--values`:

- Inline arguments, e.g.: `-v paramName=value1 -v paramName=value2 -v paramName2=other` 
- Passing an SPARQL Result Set file, e.g.: `-v selectResult.xml`

In the first case, the engine computes the cardinal product of all the variables bindings included and execute the query for each one of the resulting set of bindings.

In the second case, the query is executed for each set of bindings in the result set.

The following is an example of how parameter can be used in a query:
```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot ?_starName .
    }

}
```
The value of `?_starName` can be passed via the CLI as follows:
```bash
java -jar sparql-anything-<version>.jar -q query.sparql -v starName="Courteney Cox"
```

## Functions and magic properties
SPARQL Anything is built on Apache Jena, see a list of supported functions on the [Apache Jena documentation](https://jena.apache.org/documentation/query/library-function.html).
### Working with sequences
#### The `fx:anySlot` magic property
The execution engine is sensible to the magic property

``<http://sparql.xyz/facade-x/ns/anySlot>``

This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).

#### Functions on container membership properties
The system supports the following functions on container membership properties (See [issue 78](https://github.com/SPARQL-Anything/sparql.anything/issues/78)):

- `fx:cardinal(?a)` returns the corresponding cardinal integer from `?a` (`rdf:_24` -> `24`)
- `fx:before(?a, ?b)` returns `true` if `?a` and `?b` are container membership properties and `?a` is lower than `?b`, `false` otherwise
- `fx:after(?a, ?b)`  returns `true` if `?a` and `?b` are container membership properties and `?a` is higher than `?b`, `false` otherwise
- `fx:previous(?a)` returns the container membership property that preceeds `?a` (`rdf:_2` -> `rdf:_1`)
- `fx:next(?b)` returns the container membership property that succeedes `?b` (`rdf:_1` -> `rdf:_2`)
- `fx:forward(?a, ?b)` returns the container membership property that follows `?a` of `?b` steps (`rdf:_2, 5` -> `rdf:_7`)
- `fx:backward(?a, ?b)` returns the container membership property that preceeds `?a` of `?b` steps (`rdf:_24, 4` -> `rdf:_20`)

### Working with strings
The system supports the following functions for string manipulation (See [issue 104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):

- `fx:String.startsWith` wraps [`java.lang.String.startsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.endsWith` wraps [`java.lang.String.endsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.indexOf` wraps [`java.lang.String.indexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.substring` wraps [`java.lang.String.substring`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.toLowerCase` wraps [`java.lang.String.toLowerCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.toUpperCase` wraps [`java.lang.String.toUpperCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.trim` wraps [`java.lang.String.trim`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

The system supports the following functions to manipulate words (See [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):

- `WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))
- `WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))
- `WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))
- `WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))
- `WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))

### Hash functions
The system supports the following functions for computing hash digest from strings (See issues [104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):
- `fx:DigestUtils.md2Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md2Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md2Hex-java.lang.String-)
- `fx:DigestUtils.md5Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md5Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md5Hex-java.lang.String-)
- `fx:DigestUtils.sha1Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha1Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha1Hex-java.lang.String-)
- `fx:DigestUtils.sha256Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha256Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha256Hex-java.lang.String-)
- `fx:DigestUtils.sha384Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha384Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha384Hex-java.lang.String-)
- `fx:DigestUtils.sha512Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha512Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha512Hex-java.lang.String-)

### Functions on URLs
The system supports the following functions operating on strings that are URLs (See [issue 176](https://github.com/SPARQL-Anything/sparql.anything/issues/)):
- `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html#encode(java.lang.String,java.lang.String))
- `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))

### The function `fx:serial`:
The function `fx:serial (?a ... ?n)` generates an incremental number using the arguments as reference counters. For example, calling `fx:serial("x")` two times will generate `1` and then `2`. Instead, calling `fx:serial(?x)` multiple times will generate sequential numbers for each value of `?x`.

### The function `fx:entity`
The function `fx:entity (?a ... ?n)` accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (`rdf:_1`,`rdf:_2`,...) are cast to numbers and then to strings (`"1","2"`).
```
BIND ( fx:entity ( myns:, "dummy-entity", 1) AS ?myentity)
# is equivalent to
BIND ( IRI( CONCAT ( STR (myns:), "dummy-entity", STR(1) ) AS ?myentity )
```
See also [issue 106](https://github.com/SPARQL-Anything/sparql.anything/issues/106)

### The function `fx:literal`
The function `fx:literal( ?a , ?b )` builds a literal from the string representation of `?a`, using `?b` either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).

### The function `fx:bnode`
The function `fx:bnode( ?a) ` builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph. Apparently, the standard function `BNODE` does generate a new node for each query solution (see issue [#273](https://github.com/SPARQL-Anything/sparql.anything/issues/273) for an explanatory case).

## Usage
### Command Line Interface (CLI)

An executable JAR can be obtained from the [Releases](https://github.com/spice-h2020/sparql.anything/releases) page.

The jar can be executed as follows:

```
usage: java -jar sparql.anything-<version>  -q query [-f <output
            format>] [-v <filepath | name=value> ... ]  [-l path] [-o
            filepath]
 -q,--query <query>                    The path to the file storing the
                                       query to execute or the query
                                       itself.
 -o,--output <file>                    OPTIONAL - The path to the output
                                       file. [Default: STDOUT]
 -e,--explain                          OPTIONAL - Explain query execution
 -l,--load <load>                      OPTIONAL - The path to one RDF file
                                       or a folder including a set of
                                       files to be loaded. When present,
                                       the data is loaded in memory and
                                       the query executed against it.
 -f,--format <string>                  OPTIONAL -  Format of the output
                                       file. Supported values: JSON, XML,
                                       CSV, TEXT, TTL, NT, NQ. [Default:
                                       TEXT or TTL]
 -s,--strategy <strategy>              OPTIONAL - Strategy for query
                                       evaluation. Possible values: '1' -
                                       triple filtering (default), '0' -
                                       triplify all data. The system
                                       fallbacks to '0' when the strategy
                                       is not implemented yet for the
                                       given resource type.
 -p,--output-pattern <outputPattern>   OPTIONAL - Output filename pattern,
                                       e.g. 'myfile-?friendName.json'.
                                       Variables should start with '?' and
                                       refer to bindings from the input
                                       file. This option can only be used
                                       in combination with 'input' and is
                                       ignored otherwise. This option
                                       overrides 'output'.
 -v,--values <values>                  OPTIONAL - Values passed as input
                                       parameter to a query template. When
                                       present, the query is pre-processed
                                       by substituting variable names with
                                       the values provided. The argument
                                       can be used in two ways. (1)
                                       Providing a single SPARQL ResultSet
                                       file. In this case, the query is
                                       executed for each set of bindings
                                       in the input result set. Only 1
                                       file is allowed. (2) Named variable
                                       bindings: the argument value must
                                       follow the syntax:
                                       var_name=var_value. The argument
                                       can be passed multiple times and
                                       the query repeated for each set of
                                       values.
 -i,--input <input>                    [Deprecated] OPTIONAL - The path to
                                       a SPARQL result set file to be used
                                       as input. When present, the query
                                       is pre-processed by substituting
                                       variable names with values from the
                                       bindings provided. The query is
                                       repeated for each set of bindings
                                       in the input result set.
```
Logging can be configured adding the following option (SLF4J):
```
-Dorg.slf4j.simpleLogger.defaultLogLevel=trace
```

### Fuseki

An executable JAR of a SPARQL-Anything-powered Fuseki endpoint can be obtained from the [Releases](https://github.com/spice-h2020/sparql.anything/releases) page.

The jar can be executed as follows:

```
usage: java -jar sparql-anything-server-<version>.jar [-p port] [-e
            sparql-endpoint-path] [-g endpoint-gui-path]
 -e,--path <path>   The path where the server will be running on (Default
                    /sparql.anything).
 -g,--gui <gui>     The path of the SPARQL endpoint GUI (Default /sparql).
 -p,--port <port>   The port where the server will be running on (Default
                    3000 ).
```

Also a docker image can be used by following the instructions [here](BROWSER.md).

## Licence

SPARQL Anything is distributed under [Apache 2.0 License](LICENSE)

## How to cite our work
Daga, Enrico; Asprino, Luigi; Mulholland, Paul and Gangemi, Aldo (2021). Facade-X: An Opinionated Approach to SPARQL Anything. In: Alam, Mehwish; Groth, Paul; de Boer, Victor; Pellegrini, Tassilo and Pandit, Harshvardhan J. eds. Volume 53: Further with Knowledge Graphs, Volume 53. IOS Press, pp. 58–73.

DOI: https://doi.org/10.3233/ssw210035 | [PDF](http://oro.open.ac.uk/78973/1/78973.pdf)
```bibtex
@incollection{oro78973,
          volume = {53},
           month = {August},
          author = {Enrico Daga and Luigi Asprino and Paul Mulholland and Aldo Gangemi},
       booktitle = {Volume 53: Further with Knowledge Graphs},
          editor = {Mehwish Alam and Paul Groth and Victor de Boer and Tassilo Pellegrini and Harshvardhan J. Pandit},
           title = {Facade-X: An Opinionated Approach to SPARQL Anything},
       publisher = {IOS Press},
            year = {2021},
         journal = {Studies on the Semantic Web},
           pages = {58--73},
        keywords = {SPARQL; meta-model; re-engineering},
             url = {http://oro.open.ac.uk/78973/},
        abstract = {The Semantic Web research community understood since its beginning how crucial it is to equip practitioners with methods to transform non-RDF resources into RDF. Proposals focus on either engineering content transformations or accessing non-RDF resources with SPARQL. Existing solutions require users to learn specific mapping languages (e.g. RML), to know how to query and manipulate a variety of source formats (e.g. XPATH, JSON-Path), or to combine multiple languages (e.g. SPARQL Generate). In this paper, we explore an alternative solution and contribute a general-purpose meta-model for converting non-RDF resources into RDF: {\ensuremath{<}}i{\ensuremath{>}}Facade-X{\ensuremath{<}}/i{\ensuremath{>}}. Our approach can be implemented by overriding the SERVICE operator and does not require to extend the SPARQL syntax. We compare our approach with the state of art methods RML and SPARQL Generate and show how our solution has lower learning demands and cognitive complexity, and it is cheaper to implement and maintain, while having comparable extensibility and efficiency.}
}
```
