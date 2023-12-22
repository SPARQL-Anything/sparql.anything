<!-- This page has been generated with sparql-anything-documentation-generator module -->

# CSV

A comma-separated values (CSV) file is a text file that uses a comma to separate an ordered sequence of values in a data record and a carriage return to separate the data records of a sequence.

A CSV can be represented as a list of lists in which the outer list captures the sequence of data records (representable as containers), while the inner list captures the sequence of primitive values within a record.



## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- csv
- tab
- tsv

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/csv
- text/tab-separated-values

## Default implementation

- [io.github.sparqlanything.csv.CSVTriplifier](../sparql-anything-csv/src/main/java/io/github/sparqlanything/csv/CSVTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/simple.csv

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.csv>
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
  rdf:_1    [ rdf:_1  "email" ;
              rdf:_2  "name" ;
              rdf:_3  "surname"
            ] ;
  rdf:_2    [ rdf:_1  "laura@example.com" ;
              rdf:_2  "Laura" ;
              rdf:_3  "Grey"
            ] ;
  rdf:_3    [ rdf:_1  "craig@example.com" ;
              rdf:_2  "Craig" ;
              rdf:_3  "Johnson"
            ] ;
  rdf:_4    [ rdf:_1  "mary@example.com" ;
              rdf:_2  "Mary" ;
              rdf:_3  "Jenkins"
            ] ;
  rdf:_5    [ rdf:_1  "jamie@example.com" ;
              rdf:_2  "Jamie" ;
              rdf:_3  "Smith"
            ]
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [csv.headers](#csvheaders) | It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples. | true/false | false |
| [csv.headers-row](#csvheadersrow) | It specifies the number of the row to use for extracting column headers. Note this option affects the performance as it requires to pass through input twice. -- see #179 | Any integer | 1 |
| [csv.format](#csvformat) | The format of the input CSV file. | Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache&#39;s commons CSV library. | Default |
| [csv.delimiter](#csvdelimiter) | It sets the column delimiter, usually ,;\t etc. | Any single character | , |
| [csv.quote-char](#csvquotechar) | It sets the quoting character | Any single character | &quot; |
| [csv.null-string](#csvnullstring) | It tells the CSV triplifier to not produce triples where the specified string would be in the object position of the triple | Any String | Not set |
| [csv.ignore-columns-with-no-header](#csvignorecolumnswithnoheader) | It tells the csv triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when csv.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see #180 | true/false | false |

---
### `csv.headers`

#### Description

It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Example 1

Compute the average petal length of the species having sepal length greater than 4.9

###### Input

https://sparql-anything.cc/examples/simple.tsv

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>

SELECT  (AVG(xsd:float(?petalLength)) AS ?avgPetalLength)
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true,csv.format=TDF>
      { ?s  xyz:Sepal_length  ?length ;
            xyz:Petal_length  ?petalLength
        FILTER ( xsd:float(?length) > 4.9 )
      }
  }

```

###### Result

```turtle
------------------
| avgPetalLength |
==================
| 0              |
------------------

```

---
### `csv.headers-row`

#### Description

It specifies the number of the row to use for extracting column headers. Note this option affects the performance as it requires to pass through input twice. -- see #179

#### Valid Values

Any integer

#### Default Value

1

#### Examples

##### Example 1

Compute the average petal length of the species having sepal length greater than 4.9

###### Input

https://sparql-anything.cc/examples/simple.tsv

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>

SELECT  (AVG(xsd:float(?petalLength)) AS ?avgPetalLength)
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true,csv.format=TDF,csv.headers-row=3>
      { ?s  xyz:Sepal_length  ?length ;
            xyz:Petal_length  ?petalLength
        FILTER ( xsd:float(?length) > 4.9 )
      }
  }

```

###### Result

```turtle
------------------
| avgPetalLength |
==================
| 0              |
------------------

```

---
### `csv.format`

#### Description

The format of the input CSV file.

#### Valid Values

Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache&#39;s commons CSV library.

#### Default Value

Default

#### Examples

##### Example 1

Constructing a Facade-X RDF graph out of the TSV file available at https://sparql-anything.cc/examples/simple.tsv

###### Input

https://sparql-anything.cc/examples/simple.tsv

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.format=TDF>
      { ?s  ?p  ?o }
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
  rdf:_1    [ rdf:_1  "Sepal_length\tSepal_width\tPetal_length\tPetal_width\tSpecies" ] ;
  rdf:_2    [ rdf:_1  "5.1\t3.5\t1.4\t0.2\tI. setosa" ] ;
  rdf:_3    [ rdf:_1  "4.9\t3.0\t1.4\t0.2\tI. setosa" ] ;
  rdf:_4    [ rdf:_1  "4.7\t3.2\t1.3\t0.2\tI. setosa" ] ;
  rdf:_5    [ rdf:_1  "4.6\t3.1\t1.5\t0.2\tI. setosa" ] ;
  rdf:_6    [ rdf:_1  "5.0\t3.6\t1.4\t0.2\tI. setosa" ]
] .

```

---
### `csv.delimiter`

#### Description

It sets the column delimiter, usually ,;\t etc.

#### Valid Values

Any single character

#### Default Value

,

#### Examples

##### Example 1

Compute the maximum petal length of the species having sepal length less than 4.9

###### Input

https://sparql-anything.cc/examples/simple.tsv

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  (MAX(xsd:float(?petalLength)) AS ?maxPetalLength)
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true>
      { fx:properties
                  fx:csv.delimiter  "\t" .
        ?s        xyz:Sepal_length  ?length ;
                  xyz:Petal_length  ?petalLength
        FILTER ( xsd:float(?length) < 4.9 )
      }
  }

```

###### Result

```turtle
---------------------------------------------------
| maxPetalLength                                  |
===================================================
| "1.5"^^<http://www.w3.org/2001/XMLSchema#float> |
---------------------------------------------------

```

---
### `csv.quote-char`

#### Description

It sets the quoting character

#### Valid Values

Any single character

#### Default Value

&quot;

#### Examples

##### Example 1

Constructing a Facade-X RDF graph out of the CSV available at https://sparql-anything.cc/examples/csv_with_commas.csv

###### Input

https://sparql-anything.cc/examples/csv_with_commas.csv

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/csv_with_commas.csv,csv.headers=true,csv.quote-char='>
      { ?s  ?p  ?o }
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
  rdf:_1    [ xyz:email    "laura@example.com" ;
              xyz:name     "Laura, Nancy" ;
              xyz:surname  "Grey"
            ] ;
  rdf:_2    [ xyz:email    "craig@example.com" ;
              xyz:name     "Craig" ;
              xyz:surname  "Johnson"
            ] ;
  rdf:_3    [ xyz:email    "mary@example.com" ;
              xyz:name     "Mary" ;
              xyz:surname  "Jenkins"
            ] ;
  rdf:_4    [ xyz:email    "jamie@example.com" ;
              xyz:name     "Jamie" ;
              xyz:surname  "Smith"
            ]
] .

```

---
### `csv.null-string`

#### Description

It tells the CSV triplifier to not produce triples where the specified string would be in the object position of the triple

#### Valid Values

Any String

#### Default Value

Not set

#### Examples

##### Example 1

Retrieving name surname of who doesn&#39;t have an email address.

###### Input

https://sparql-anything.cc/examples/simple_with_null.csv

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  ?name ?surname
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple_with_null.csv,csv.headers=true>
      { fx:properties
                  fx:csv.null-string  "" .
        ?c        xyz:name            ?name ;
                  xyz:surname         ?surname
        FILTER NOT EXISTS { ?c  xyz:email  ?email }
      }
  }

```

###### Result

```turtle
----------------------
| name   | surname   |
======================
| "Mary" | "Jenkins" |
----------------------

```

---
### `csv.ignore-columns-with-no-header`

#### Description

It tells the csv triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when csv.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see #180

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Example 1



###### Input

Inline content

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT DISTINCT  ?fred ?sally
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:csv.headers        true ;
                  fx:content            ",state\nfred,CO\nsally,FL" ;
                  fx:media-type         "text/csv" ;
                  fx:csv.ignore-columns-with-no-header  true .
        ?root     rdf:type              fx:root ;
                  rdf:_1                _:b0 .
        _:b0      rdf:_1                ?fred .
        ?root     rdf:_2                _:b1 .
        _:b1      rdf:_1                ?sally
      }
  }

```

###### Result

```turtle
----------------
| fred | sally |
================
----------------

```





