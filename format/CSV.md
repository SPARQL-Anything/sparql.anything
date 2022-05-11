# CSV

A comma-separated values (CSV) file is a text file that uses a comma to separate an ordered sequence of values in a data record and a carriage return to separate the data records of a sequence.

A CSV can be represented as a list of lists in which the outer list captures the sequence of data records (representable as containers), while the inner list captures the sequence of primitive values within a record.

## Supported file extensions

.csv, .tsv, .tab

## Default Transformation


### Data:

```csv
email,name,surname
laura@example.com,Laura,Grey
craig@example.com,Craig,Johnson
mary@example.com,Mary,Jenkins
jamie@example.com,Jamie,Smith

```

### Query

```

CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.csv>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF:

```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

[ a fx:root ;
  rdf:_1  [ rdf:_1  "laura@example.com" ;
            rdf:_2  "2070" ;
            rdf:_3  "Laura" ;
            rdf:_4  "Grey"
          ] ;
  rdf:_2  [ rdf:_1  "craig@example.com" ;
            rdf:_2  "4081" ;
            rdf:_3  "Craig" ;
            rdf:_4  "Johnson"
          ] ;
  rdf:_3  [ rdf:_1  "mary@example.com" ;
            rdf:_2  "9346" ;
            rdf:_3  "Mary" ;
            rdf:_4  "Jenkins"
          ] ;
  rdf:_4  [ rdf:_1  "jamie@example.com" ;
            rdf:_2  "5079" ;
            rdf:_3  "Jamie" ;
            rdf:_4  "Smith"
          ]
] .

```

## Options

### Summary

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|csv.format|The format of the input CSV file.|Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache's commons CSV library|Default|
|csv.headers|It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples.|true/false|false|
|csv.delimiter|The column delimiter, usually `,`,`;`,`\t`, ...|any single char|`,`|
|csv.quote-char|The quoting character|any single char|`"`|
|csv.null-string|It tells the CSV triplifier to not produce triples where the specificed string would be in the object position of the triple.|any string|not set|

### csv.format

#### Description

The format of the input CSV file.


#### Valid Values

Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache's commons CSV library.

#### Default Value

Default

#### Examples
