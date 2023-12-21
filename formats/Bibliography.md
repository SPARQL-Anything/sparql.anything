<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Bibliography

BibTeX is a text format used (typically together with LaTeX) to specify a list of references in a database file with the aim of separating bibliographic information from its presentation.
A BibTeX database file is formed by a list of bibliographic entries where each entry consists of the type (e.g. article, inproceedings etc.), a citation key, and key-value pairs for the other characteristics of an entry.
Each BibTeX entry can be represented as a  typed container that holds a set of key-value pairs.


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- bib
- bibtex

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/x-bibtex

## Default implementation

- [io.github.sparqlanything.bib.BibtexTriplifier](../sparql-anything-bibliography/src/main/java/io/github/sparqlanything/bibliography/BibtexTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/example.bib

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.bib>
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

[ rdf:type  fx:root ;
  rdf:_1    [ rdf:type       xyz:article ;
              xyz:author     "Donald E. Knuth" ;
              xyz:journal    "The Computer Journal" ;
              xyz:number     "2" ;
              xyz:pages      "97--111" ;
              xyz:publisher  "Oxford University Press" ;
              xyz:title      "Literate Programming" ;
              xyz:volume     "27" ;
              xyz:year       "1984"
            ] ;
  rdf:_2    [ rdf:type     xyz:article ;
              xyz:author   "Berners-Lee, Tim and Hendler, James and Lassila, Ora" ;
              xyz:journal  "Scientific american" ;
              xyz:number   "5" ;
              xyz:pages    "34--43" ;
              xyz:title    "The semantic web" ;
              xyz:volume   "284" ;
              xyz:year     "2001"
            ]
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|





