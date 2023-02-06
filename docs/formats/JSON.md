# JSON

The JavaScript Object Notation is specified by [ECMA](https://www.ecma-international.org/publications-and-standards/standards/ecma-404/).
The syntax defines three types of elements:
- *objects*, a set of key-value pairs, where keys are supposed to be unique;
- *values*, which are either strings, numbers, boolean, or the primitive 'null';
- and, *arrays*, which specify sequences (containing other arrays, objects, or values).

According to Facade-X model, SPARQL Anything interprets objects and arrays as containers:
- RDF properties are used to link objects to values.
- Arrays are represented by the ordered sequence component.
- Values are expressed as *rdf:Literal*, selecting relevant XSD datatypes from the RDFS specification: *xsd:string*, *xsd:boolean*, *xsd:int*, *xsd:float*

Currently, fields with the 'null' value are ignored.
<!-- However, we may decide to represent it as blank node or to create a primitive entity to express it, for example, similar to \tt{rdf:nil}.}.  -->

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- .json

## Default implementation

- [com.github.sparqlanything.json.JSONTriplifier](../sparql-anything-json/src/main/java/com/github/sparqlanything/json/JSONTriplifier.java)

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/json
- application/problem+json

## Default transformation

### Data

```json
{
    "stringArg": "stringValue",
    "intArg": 1,
    "booleanArg": true,
    "nullArg": null,
    "arr": [ 0, 1 ]
}
```

### Query

```

CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.json>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF

```turtle
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
[ a fx:root ;
    xyz:arr [
        rdf:_1  "0"^^xsd:int ;
        rdf:_2  "1"^^xsd:int ] ;
    xyz:booleanArg  true ;
    xyz:intArg "1"^^xsd:int ;
    xyz:stringArg "stringValue"
] .
```

### Example 1

Consider the following input, also located at [https://sparql-anything.cc/example1.json](https://sparql-anything.cc/example1.json)

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

With SPARQL Anything you can select the TV series starring "Courteney Cox" with the SPARQL query:

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

and get this result:

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

### Example 2

Consider a JSON file with the following content:

```json
{
  "Part01_001MIDDE": {
    "builder": "Peter Gerritsz",
    "originallocation": "Utrecht, Nicolaikerk",
    "year": "1479"
  },
  "Part01_002UTREJ": {
    "builder": "Gerrit Petersz",
    "originallocation": "",
    "year": "1509"
  }
}
```

the query:

```sparql
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX oont: <uri:todo/>
construct {
    ?organ a oont:Organ ;
        oont:builder ?builder_string ;
        oont:consolelocation ?location_string ;
        oont:dateOfBirth ?year_string ;
} WHERE {
 SERVICE <x-sparql-anything:> {
    fx:properties fx:location "/app/history_base.json"  .
bind(iri(concat(str(xyz:),?_organ)) as ?organ_slot)
?root a fx:root ;
      ?organ_slot ?organ .
?organ xyz:builder ?builder_string ;
       xyz:originallocation ?location_string ;
       xyz:year ?year_string .
}
}
```
when  invoked via the command line, as follows:
```bash
java -jar /app/sparql-anything-cli/target/sparql-anything-0.9.0-SNAPSHOT.jar -q 332.rq -values organ=Part01_002UTREJ
```
generates the following RDF:
```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix oont: <uri:todo/> .
@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xyz:  <http://sparql.xyz/facade-x/data/> .

[ rdf:type              oont:Organ ;
  oont:builder          "Gerrit Petersz" ;
  oont:consolelocation  "" ;
  oont:dateOfBirth      "1509"
] .
```

## Options

### Summary

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|json.path|One or more JsonPath expressions as filters. E.g. `json.path=value` or `json.path.1`, `json.path.2`, `...` to add multiple expressions.|Any valid JsonPath (see [JsonSurfer implementation](https://github.com/jsurfer/JsonSurfer)))||


### `json.path`

!!! note
    The `json.path` option is only recommended if users need to filter a large JSON file, for example, in combination with the `slice` option. 
    It will pre-process the JSON before the execution of the query. 
    In most cases, it is easier to query the JSON using a triple pattern, as in the [example described before](#Example).

#### Description

One or more JsonPath expressions as filters. E.g. `json.path=value` or `json.path.1`, `json.path.2`, `...` to add multiple expressions.

#### Valid Values

Any valid JsonPath (see [JsonSurfer implementation](https://github.com/jsurfer/JsonSurfer))

#### Default Value

Not set

#### Examples

##### Input

Located at [https://sparql-anything.cc/example1.json](https://sparql-anything.cc/example1.json)

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

##### Use Case 1: Constructing a Facade-X RDF Graph selecting only containers that match the Json Path ``$[?(@.name=="Friends")]``.

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

@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

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

##### Use Case 2: Retrieving the language of the TV series named "Friends".

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

```
-------------
| language  |
-------------
| "English" |
-------------
```


##### Use Case 3: Retrieving the lists of stars of the TV Series named "Friends" and "Cougar Town".


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


```

@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

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
