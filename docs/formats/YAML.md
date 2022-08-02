# YAML

YAML is a human-readable data-serialization language. 
It is commonly used for configuration files and in applications where data is being stored or transmitted. 
YAML targets many of the same communications applications as Extensible Markup Language (XML) but has a minimal syntax which intentionally differs from SGML.
It uses both Python-style indentation to indicate nesting, and a more compact format that uses [...] for lists and {...} for maps thus JSON files are valid YAML 1.2.
[[From Wikipedia](https://en.wikipedia.org/wiki/YAML)]

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- yaml

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/yaml
- text/yaml
- x-text/yaml

## Default Transformation


### Data

```yaml
key: value
another-key:
  - name: john
    surname: smith
# A comment
boolean: true
float: 0.1
two-values:
  - 1
  - "2"
nested-array:
  - nested-array:
      - nested:key: "Value with spaces"
```

Located at https://sparql-anything.cc/examples/example.tar

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.yaml>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type          fx:root ;
  xyz:another-key   [ rdf:_1  [ xyz:name     "john" ;
                                xyz:surname  "smith"
                              ]
                    ] ;
  xyz:boolean       true ;
  xyz:float         "0.1"^^xsd:double ;
  xyz:key           "value" ;
  xyz:nested-array  [ rdf:_1  [ xyz:nested-array  [ rdf:_1  [ <http://sparql.xyz/facade-x/data/nested%3Akey>
                                                                      "Value with spaces" ]
                                                  ]
                              ]
                    ] ;
  xyz:two-values    [ rdf:_1  "1"^^xsd:int ;
                      rdf:_2  "2"
                    ]
] .
```



<!--
# 



## Extensions

SPARQL Anything selects this transformer for the following file extensions:

-

## Media types

SPARQL Anything selects this transformer for the following media types:

- 

## Default Transformation


### Data

```

```

Located at https://sparql-anything.cc/examples/example.tar

### Query

```

```

### Facade-X RDF

```turtle

```


## Options

### Summary



### ``

#### Description



#### Valid Values


#### Default Value


#### Examples

##### Input

### Data

```
```

Located at https://sparql-anything.cc/examples/example.tar

##### Use Case 1: 

###### Query

```
```

###### Result

```turtle
```


-->