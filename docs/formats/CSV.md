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

```CSV
email,name,surname
laura@example.com,Laura,Grey
craig@example.com,Craig,Johnson
mary@example.com,Mary,Jenkins
jamie@example.com,Jamie,Smith

```

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
| [csv.headers](#csvheaders) | It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples. | true/false | `false` |
| [csv.headers-row](#csvheaders-row) | It specifies the number of the row to use for extracting column headers. Note this option affects the performance as it requires to pass through input twice. -- see [#179](https://github.com/SPARQL-Anything/sparql.anything/issues/179) | Any integer | `1` |
| [csv.format](#csvformat) | The format of the input CSV file. | Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache&#39;s commons CSV library. | `Default` |
| [csv.delimiter](#csvdelimiter) | It sets the column delimiter, usually ,;\t etc. | Any single character | `,` |
| [csv.quote-char](#csvquote-char) | It sets the quoting character | Any single character | `&quot;` |
| [csv.null-string](#csvnull-string) | It tells the CSV triplifier to not produce triples where the specified string would be in the object position of the triple | Any String | Not set |
| [csv.ignore-columns-with-no-header](#csvignore-columns-with-no-header) | It tells the csv triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when csv.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see [#180](https://github.com/SPARQL-Anything/sparql.anything/issues/180) | true/false | `false` |

---
### `csv.headers`

#### Description

It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Compute the average petal length of the species having sepal length greater than 4.9

###### Input

```CSV
Sepal_length	Sepal_width	Petal_length	Petal_width	Species
5.1	3.5	1.4	0.2	I. setosa
4.9	3.0	1.4	0.2	I. setosa
4.7	3.2	1.3	0.2	I. setosa
4.6	3.1	1.5	0.2	I. setosa
5.0	3.6	1.4	0.2	I. setosa

```

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

It specifies the number of the row to use for extracting column headers. Note this option affects the performance as it requires to pass through input twice. -- see [#179](https://github.com/SPARQL-Anything/sparql.anything/issues/179)

#### Valid Values

Any integer

#### Default Value

`1`

#### Examples

##### Example 1

Compute the average petal length of the species having sepal length greater than 4.9

###### Input

```CSV
Sepal_length	Sepal_width	Petal_length	Petal_width	Species
5.1	3.5	1.4	0.2	I. setosa
4.9	3.0	1.4	0.2	I. setosa
4.7	3.2	1.3	0.2	I. setosa
4.6	3.1	1.5	0.2	I. setosa
5.0	3.6	1.4	0.2	I. setosa

```

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

`Default`

#### Examples

##### Example 1

Constructing a Facade-X RDF graph out of the TSV file available at https://sparql-anything.cc/examples/simple.tsv

###### Input

```CSV
Sepal_length	Sepal_width	Petal_length	Petal_width	Species
5.1	3.5	1.4	0.2	I. setosa
4.9	3.0	1.4	0.2	I. setosa
4.7	3.2	1.3	0.2	I. setosa
4.6	3.1	1.5	0.2	I. setosa
5.0	3.6	1.4	0.2	I. setosa

```

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
@prefix rss:    <http://purl.or