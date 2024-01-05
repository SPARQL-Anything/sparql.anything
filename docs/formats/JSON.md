<!-- This page has been generated with sparql-anything-documentation-generator module -->

# JSON

The JavaScript Object Notation is specified by [ECMA](https://www.ecma-international.org/publications-and-standards/standards/ecma-404/).
The syntax defines three types of elements:
- *objects*, a set of key-value pairs, where keys are supposed to be unique;
- *values*, which are either strings, numbers, boolean, or the primitive &#39;null&#39;;
- and, *arrays*, which specify sequences (containing other arrays, objects, or values).

According to Facade-X model, SPARQL Anything interprets objects and arrays as containers:
- RDF properties are used to link objects to values.
- Arrays are represented by the ordered sequence component.
- Values are expressed as *rdf:Literal*, selecting relevant XSD datatypes from the RDFS specification: *xsd:string*, *xsd:boolean*, *xsd:int*, *xsd:float*

Currently, fields with the &#39;null&#39; value are ignored.
&lt;!-- However, we may decide to represent it as blank node or to create a primitive entity to express it, for example, similar to \tt{rdf:nil}.}.  --&gt;


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- json

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/json
- application/problem+json

## Default implementation

- [io.github.sparqlanything.json.JSONTriplifier](../sparql-anything-json/src/main/java/io/github/sparqlanything/json/JSONTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/simple.json

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.json>
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

[ rdf:type        fx:root ;
  xyz:arr         [ rdf:_1  "0"^^xsd:int ;
                    rdf:_2  "1"^^xsd:int
                  ] ;
  xyz:booleanArg  true ;
  xyz:intArg      "1"^^xsd:int ;
  xyz:stringArg   "stringValue"
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [json.path](#jsonpath) | One or more JsonPath expressions as filters. E.g. `json.path=value` or `json.path.1`, `json.path.2`, `...` to add multiple expressions. The `json.path` option is only recommended if users need to filter a large JSON file, for example, in combination with the `slice` option. 
    It will pre-process the JSON before the execution of the query. 
    In most cases, it is easier to query the JSON using a triple pattern, as in the [example described before](#Example). | Any valid JsonPath (see [JsonSurfer implementation](https://github.com/jsurfer/JsonSurfer))) | Not set |

---
### `json.path`

#### Description

One or more JsonPath expressions as filters. E.g. `json.path=value` or `json.path.1`, `json.path.2`, `...` to add multiple expressions. The `json.path` option is only recommended if users need to filter a large JSON file, for example, in combination with the `slice` option. 
    It will pre-process the JSON before the execution of the query. 
    In most cases, it is easier to query the JSON using a triple pattern, as in the [example described before](#Example).

#### Valid Values

Any valid JsonPath (see [JsonSurfer implementation](https://github.com/jsurfer/JsonSurfer)))

#### Default Value

Not set

#### Examples

##### Example 1

Retrieving the lists of stars of the TV Series named &quot;Friends&quot; and &quot;Cougar Town&quot;.

###### Input

https://sparql-anything.cc/example1.json

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/example1.json>
      { fx:properties
                  fx:json.path.1  "$[?(@.name==\"Friends\")].stars" ;
                  fx:json.path.2  "$[?(@.name==\"Cougar Town\")].stars" .
        ?s        ?p              ?o
      }
  }

```

###### Result

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
  rdf:_1    [ rdf:_1  "Jennifer Aniston" ;
              rdf:_2  "Courteney Cox" ;
              rdf:_3  "Lisa Kudrow" ;
              rdf:_4  "Matt LeBlanc" ;
              rdf:_5  "Matthew Perry" ;
              rdf:_6  "David Schwimmer"
            ] ;
  rdf:_2    [ rdf:_1  "Courteney Cox" ;
              rdf:_2  "David Arquette" ;
              rdf:_3  "Bill Lawrence" ;
              rdf:_4  "Linda Videtti Figueiredo" ;
              rdf:_5  "Blake McCormick"
            ]
] .

```

##### Example 2

 Retrieving the language of the TV series named &quot;Friends&quot;.

###### Input

https://sparql-anything.cc/example1.json

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?language
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/example1.json>
      { fx:properties
                  fx:json.path  "$[?(@.name==\"Friends\")]" .
        _:b0      xyz:language  ?language
      }
  }

```

###### Result

```turtle
-------------
| language  |
=============
| "English" |
-------------

```

##### Example 3

Constructing a Facade-X RDF Graph selecting only containers that match the Json Path `$[?(@.name==&quot;Friends&quot;)]`.

###### Input

https://sparql-anything.cc/example1.json

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/example1.json>
      { fx:properties
                  fx:json.path  "$[?(@.name==\"Friends\")]" .
        ?s        ?p            ?o
      }
  }

```

###### Result

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
  rdf:_1    [ xyz:genres     [ rdf:_1  "Comedy" ;
                               rdf:_2  "Romance"
                             ] ;
              xyz:language   "English" ;
              xyz:name       "Friends" ;
              xyz:premiered  "1994-09-22" ;
              xyz:stars      [ rdf:_1  "Jennifer Aniston" ;
                               rdf:_2  "Courteney Cox" ;
                               rdf:_3  "Lisa Kudrow" ;
                               rdf:_4  "Matt LeBlanc" ;
                               rdf:_5  "Matthew Perry" ;
                               rdf:_6  "David Schwimmer"
                             ] ;
              xyz:status     "Ended" ;
              xyz:summary    "Follows the personal and professional lives of six twenty to thirty-something-year-old friends living in Manhattan."
            ]
] .

```





