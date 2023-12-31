<!-- This page has been generated with sparql-anything-documentation-generator module -->

# RDF

RDF files can be targeted by the option `location`, the content is loaded as-is (no facade-x interpretation, obviously). In addition, the SPARQL Anything Command Line Interface can load static RDF files.

The query does not need to include a SERVICE clause, so you can use the tool to just query some RDF file of your choice.
This is useful when you want to break down the process so that RDF files produced by previous SPARQL Anything processes are joined with data coming from additional transformatioons.
Examples of this can be found in the [tutorials](TUTORIALS.md).

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





