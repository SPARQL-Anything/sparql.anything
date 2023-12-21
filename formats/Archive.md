<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Archive

Archives and directories can be seen as files with the purpose of collecting other files. According to the Facade-X metamodel archives and directories can be seen as lists of filenames.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- zip
- tar

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/zip
- application/x-tar

## Default implementation

- [io.github.sparqlanything.zip.FolderTriplifier](../sparql-anything-archive/src/main/java/io/github/sparqlanything/archive/FolderTriplifier.java)
- [io.github.sparqlanything.zip.ZipTriplifier](../sparql-anything-archive/src/main/java/io/github/sparqlanything/archive/ZipTriplifier.java)
- [io.github.sparqlanything.zip.TarTriplifier](../sparql-anything-archive/src/main/java/io/github/sparqlanything/archive/TarTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/example.tar

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>
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
  rdf:_1    "example/" ;
  rdf:_2    "example/test.csv" ;
  rdf:_3    "example/test.json" ;
  rdf:_4    "example/test.xml" ;
  rdf:_5    "example/test.txt"
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| archive.matches | It tells sparql.anything to evaluate a regular expression on the filenames within the archives. In this case the slots will be filled with the files that match the regex only. | Any valid regular expression | .* |

### `archive.matches`

#### Description

It tells sparql.anything to evaluate a regular expression on the filenames within the archives. In this case the slots will be filled with the files that match the regex only.

#### Valid Values

Any valid regular expression

#### Default Value

.*

#### Examples

##### Example 1

Select and triplify only .csv and .txt files within the archive.

###### Input

https://sparql-anything.cc/examples/example.tar

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

CONSTRUCT 
  { 
    ?s1 ?p1 ?o1 .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>
      { fx:properties
                  fx:archive.matches  ".*txt|.*csv" .
        ?s        fx:anySlot          ?file1
        SERVICE <x-sparql-anything:>
          { fx:properties
                      fx:location      ?file1 ;
                      fx:from-archive  "https://sparql-anything.cc/examples/example.tar" .
            ?s1       ?p1              ?o1
          }
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
  rdf:_1    "this is a test" ;
  rdf:_1    [ rdf:_1  "Year" ;
              rdf:_2  "Make" ;
              rdf:_3  "Model" ;
              rdf:_4  "Description" ;
              rdf:_5  "Price"
            ] ;
  rdf:_2    [ rdf:_1  "1997" ;
              rdf:_2  "Ford" ;
              rdf:_3  "E350" ;
              rdf:_4  "ac, abs, moon" ;
              rdf:_5  "3000.00"
            ] ;
  rdf:_3    [ rdf:_1  "1999" ;
              rdf:_2  "Chevy" ;
              rdf:_3  "Venture \"Extended Edition\"" ;
              rdf:_4  "" ;
              rdf:_5  "4900.00"
            ] ;
  rdf:_4    [ rdf:_1  "1999" ;
              rdf:_2  "Chevy" ;
              rdf:_3  "Venture \"Extended Edition, Very Large\"" ;
              rdf:_4  "" ;
              rdf:_5  "5000.00"
            ] ;
  rdf:_5    [ rdf:_1  "1996" ;
              rdf:_2  "Jeep" ;
              rdf:_3  "Grand Cherokee" ;
              rdf:_4  "MUST SELL!\nair, moon roof, loaded" ;
              rdf:_5  "4799.00"
            ]
] .

```





