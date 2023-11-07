# Query static RDF files

## From the SERVICE clause

RDF files can be targeted by the option `location`, the content is loaded as-is (no facade-x interpretation, obviously).

### Extensions

SPARQL Anything selects this transformer for the following file extensions:

-  rdf
-  ttl
-  nt
-  jsonld
-  owl
-  trig
-  nq
-  trix
-  trdf

### Media types

SPARQL Anything selects this transformer for the following media types:

- text/turtle
- application/rdf+xml
- application/n-triples
- application/ld+json
- application/rdf+thrift
- application/trix+xml
- application/n-quads
- text/trig
- application/owl+xml

### Default implementation

- [io.github.sparqlanything.rdf.RDFTriplifier](../sparql-anything-rdf/src/main/java/com/github/sparqlanything/rdf/RDFTriplifier.java)


## Querying RDF files with the Command Line Interface

In addition, the SPARQL Anything Command Line Interface can load static RDF files.

The query does not need to include a SERVICE clause, so you can use the tool to just query some RDF file of your choice.
This is useful when you want to break down the process so that RDF files produced by previous SPARQL Anything processes are joined with data coming from additional transformatioons.
Examples of this can be found in the [tutorials](TUTORIALS.md).

This feature is enabled with the command line argument `-l|--load` that accepts a file or a directory.
The files are loaded in a Dataset which becomes the target for the query execution.
A single file will be loaded in the default Graph. 
In the second case, all RDF files in the folder are loaded, each one on a Named Graph.

See also the documentation of the [Command Line Interface (CLI)](CLI.md).
