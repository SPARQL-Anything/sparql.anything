# Welcome
sparql.anything is a system for Semantic Web re-engineering that allows users to ... query anything with SPARQL.

# Facade-X
sparql.anything uses a single generic abstraction for all data source formats called Facade-X.
Facade-X is a simplistic meta-model used by sparql.anything transformers to generate RDF data from data sources.
Intuitively, Facade-X uses a subset of RDF as a general approach to represent content.
The model combines two type of elements: containers and literals.
Facade-X data always a single root container. 
Container members are a combination of key-value pairs, where keys are either RDF properties or container membership properties.
Instead, values can be either other containers or RDF literals.
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
# Formats


## CSV

## JSON

## XML

## HTML

## BIN

## Image formats

# CLI