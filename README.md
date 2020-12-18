# Welcome
sparql.anything is a system for Semantic Web re-engineering that allows users to ... query anything with SPARQL.

## Facade-X
sparql.anything uses a single generic abstraction for all data source formats called Facade-X.
Facade-X is a simplistic meta-model used by sparql.anything transformers to generate RDF data from diverse data sources.
Intuitively, Facade-X uses a subset of RDF as a general approach to represent the source content *as-it-is* but in RDF.
The model combines two type of elements: containers and literals.
Facade-X has always a single root container. 
Container members are a combination of key-value pairs, where keys are either RDF properties or container membership properties.
Instead, values can be either RDF literals or other containers.
This is a generic example of a Facade-X data object (more examples below):
```
@prefix fx: <urn:facade-x:ns#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
[] rdf:_1 [
    fx:someKey "some value" ;
    rdf:_1 "another value with unspecified key" ;
    rdf:_2 [
        rdf:type fx:MyType
        rdf:_1 "another value" 
    ]
```
## Querying anything
sparql.anything extends the Apache Jena ARQ processors by *overloading* the SERVICE operator, as in the following example:
```

```
##

## Supported Formats
Currently, sparql.anything supports the following formats: "json", "html", "xml", "csv", "bin", "png","jpeg","jpg","bmp","tiff","tif", "ico", "txt" ... bu the possibilities are limitless!
