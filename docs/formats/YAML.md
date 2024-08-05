<!-- This page has been generated with sparql-anything-documentation-generator module -->

# YAML

YAML is a lightweight, human-readable data-serialization language.
YAML is a ``superset&#39;&#39; of JSON (any JSON file can be specified in YAML) and, similarly to JSON, data can be organised in lists or associative arrays.
However, differently from JSON, comments and custom data types are allowed.
Therefore, in addition to the basic data structures required for capturing JSON files, *instance-of* is needed for representing custom data types.


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- yaml

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/yaml
- text/yaml
- x-text/yaml

## Default implementation

- [io.github.sparqlanything.yaml.YAMLTriplifier](../sparql-anything-yaml/src/main/java/io/github/sparqlanything/yaml/YAMLTriplifier.java)

## Default Transformation

### Data

```YAML
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

Located at https://sparql-anything.cc/examples/example.yaml

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.yaml>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

### Facade-X RDF

```turtle
PREFIX dc:     <http://purl.org/dc/elements/1.1/>
PREFIX eg:     <http://www.example.org/>
PREFIX fx:     <http://sparql.xyz/facade-x/ns/>
PREFIX ja:     <http://jena.hpl.hp.com/2005/11/Assembler#>
PREFIX owl:    <http://www.w3.org/2002/07/owl#>
PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rss:    <http://purl.org/rss/1.0/>
PREFIX vcard:  <http://www.w3.org/2001/vcard-rdf/3.0#>
PREFIX whatwg: <https://html.spec.whatwg.org/#>
PREFIX xhtml:  <http://www.w3.org/1999/xhtml#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX xyz:    <http://sparql.xyz/facade-x/data/>

[ rdf:type          fx:root;
  xyz:another-key   [ rdf:_1  [ xyz:name     "john";
                                xyz:surname  "smith"
                              ]
                    ];
  xyz:boolean       true;
  xyz:float         "0.1"^^xsd:double;
  xyz:key           "value";
  xyz:nested-array  [ rdf:_1  [ xyz:nested-array  [ rdf:_1  [ <http://sparql.xyz/facade-x/data/nested%3Akey>
                                                                      "Value with spaces" ]
                                                  ]
                              ]
                    ];
  xyz:two-values    [ rdf:_1  "1"^^xsd:int;
                      rdf:_2  "2"
                    ]
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [yaml.allow-duplicate-keys](#yamlallow-duplicate-keys) | Yaml 1.2 forbids duplicate keys, raising an error (default behaviour). When true, duplicate keys are tolerated (last wins).  | true/false | `false` |

---
### `yaml.allow-duplicate-keys`

#### Description

Yaml 1.2 forbids duplicate keys, raising an error (default behaviour). When true, duplicate keys are tolerated (last wins). 

#### Valid Values

true/false

#### Default Value

`false`






