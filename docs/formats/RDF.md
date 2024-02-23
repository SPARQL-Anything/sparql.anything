<!-- This page has been generated with sparql-anything-documentation-generator module -->

# RDF

RDF files can be targeted like any other format by the option `location`. The content is queried as-is (no facade-x interpretation needed, obviously). 


In addition, the [Command Line Interface (CLI)](../CLI.md) can load static RDF files.

This is useful when you want to break down the task so that RDF files produced by previous SPARQL Anything executions are joined with additional transformations.
Examples of this can be found in the [tutorials](../TUTORIALS.md).

This feature is enabled with the command line argument `-l|--load` that accepts a file or a directory.
The files are loaded in a Dataset, which becomes the target for the query execution.
A single file will be loaded in the default Graph. 
If pointing to a folder, all RDF files in the folder are loaded, each one on a Named Graph.

See also the documentation of the [Command Line Interface (CLI)](../CLI.md).


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- jsonld
- nq
- nt
- owl
- rdf
- trdf
- trig
- trix
- ttl

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/ld+json
- application/n-quads
- application/n-triples
- application/owl+xml
- application/rdf+thrift
- application/rdf+xml
- application/trix+xml
- text/trig
- text/turtle

## Default implementation

- [io.github.sparqlanything.rdf.RDFTriplifier](../sparql-anything-rdf/src/main/java/io/github/sparqlanything/rdf/RDFTriplifier.java)

## Default Transformation

### Data

```RDF
@prefix ex: <http://example.org/> .

ex:subject ex:predicate ex:object .

```

Located at https://sparql-anything.cc/examples/simple.ttl

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.ttl>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

### Facade-X RDF

```turtle
@prefix dc:     <http://purl.org/dc/elements/1.1/> .
@prefix eg:     <http://www.example.org/> .
@prefix fx:     <http://sparql.xyz/facade-x/ns/> .
@prefix ja:     <http://jena.hpl.hp.com/2005/11/Assembler#> .
@prefix owl:    <http://www.w3.org/2002/07/owl#> .
@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .
@prefix rss:    <http://purl.org/rss/1.0/> .
@prefix vcard:  <http://www.w3.org/2001/vcard-rdf/3.0#> .
@prefix whatwg: <https://html.spec.whatwg.org/#> .
@prefix xhtml:  <http://www.w3.org/1999/xhtml#> .
@prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .

<http://example.org/subject>
        <http://example.org/predicate>  <http://example.org/object> .

```





