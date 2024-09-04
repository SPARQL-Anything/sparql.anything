<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Spreadsheet

Spreadsheets are files that organise data as a collection of named tables.
Similarly to CSV, each table of a spreadsheet can be seen as a container of data records.
Each container is then stored in a different RDF graph.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- xls
- xlsx

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/vnd.ms-excel
- application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

## Default implementation

- [io.github.sparqlanything.spreadsheet.SpreadsheetTriplifier](../sparql-anything-spreadsheet/src/main/java/io/github/sparqlanything/spreadsheet/SpreadsheetTriplifier.java)

## Default Transformation

### Data


Located at https://sparql-anything.cc/examples/Book1.xlsx

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx>
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

[ rdf:type  fx:root;
  rdf:_1    [ rdf:_1  "A1" , "A";
              rdf:_2  "B1" , "B";
              rdf:_3  "C1" , "C"
            ];
  rdf:_2    [ rdf:_1  "A11" , "A1";
              rdf:_2  "B11" , "B1";
              rdf:_3  "C11" , "C1"
            ];
  rdf:_3    [ rdf:_1  "A12" , "A2";
              rdf:_2  "B12" , "B2";
              rdf:_3  "C12" , "C2"
            ]
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [spreadsheet.headers](#spreadsheetheaders) | It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples. | true/false | `false` |
| [spreadsheet.evaluate-formulas](#spreadsheetevaluate-formulas) | It tells the spreadsheet triplifier to evaluate formulas of the spreadsheet. | true/false | `false` |
| [spreadsheet.composite-values](#spreadsheetcomposite-values) | It tells the spreadsheet triplifier to extract from the cells hyperlinks and comments. If enabled, the cells will be triplified as containers instead of literals (see [#308](https://github.com/SPARQL-Anything/sparql.anything/issues/308)) | true/false | `false` |
| [spreadsheet.headers-row](#spreadsheetheaders-row) | It specifies the number of the row to use for extracting column headers. -- see [#179](https://github.com/SPARQL-Anything/sparql.anything/issues/179) | Any integer | `1` |
| [spreadsheet.ignore-columns-with-no-header](#spreadsheetignore-columns-with-no-header) | It tells the spreadsheet triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when spreadsheet.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see [#180](https://github.com/SPARQL-Anything/sparql.anything/issues/180) | Any integer | `false` |

---
### `spreadsheet.headers`

#### Description

It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Construct the dataset by using the headers of the columns to mint the property URIs.

###### Input


https://sparql-anything.cc/examples/Book1.xlsx

###### Query

```
CONSTRUCT 
  { 
    GRAPH ?g 
      { ?s ?p ?o .}
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

###### Result

```turtle
<https://sparql-anything.cc/examples/Book1.xlsx#Sheet1> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2 .
    
    _:b1    <http://sparql.xyz/facade-x/data/A>
                    "A1";
            <http://sparql.xyz/facade-x/data/B>
                    "B1";
            <http://sparql.xyz/facade-x/data/C>
                    "C1" .
    
    _:b2    <http://sparql.xyz/facade-x/data/A>
                    "A2";
            <http://sparql.xyz/facade-x/data/B>
                    "B2";
            <http://sparql.xyz/facade-x/data/C>
                    "C2" .
}

<https://sparql-anything.cc/examples/Book1.xlsx#Sheet2> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2 .
    
    _:b1    <http://sparql.xyz/facade-x/data/A1>
                    "A11";
            <http://sparql.xyz/facade-x/data/B1>
                    "B11";
            <http://sparql.xyz/facade-x/data/C1>
                    "C11" .
    
    _:b2    <http://sparql.xyz/facade-x/data/A1>
                    "A12";
            <http://sparql.xyz/facade-x/data/B1>
                    "B12";
            <http://sparql.xyz/facade-x/data/C1>
                    "C12" .
}

```

---
### `spreadsheet.evaluate-formulas`

#### Description

It tells the spreadsheet triplifier to evaluate formulas of the spreadsheet.

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Construct the dataset by evaluating the formulas.

###### Input


https://sparql-anything.cc/examples/Book2.xlsx

###### Query

```
CONSTRUCT 
  { 
    GRAPH ?g 
      { ?s ?p ?o .}
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book2.xlsx,spreadsheet.evaluate-formulas=true>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

###### Result

```turtle
<https://sparql-anything.cc/examples/Book2.xlsx#Sheet1> {
    [ a       <http://sparql.xyz/facade-x/ns/root>;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A";
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B";
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C"
              ];
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "1.0"^^<http://www.w3.org/2001/XMLSchema#double>;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "2.0"^^<http://www.w3.org/2001/XMLSchema#double>;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "3.0"^^<http://www.w3.org/2001/XMLSchema#double>
              ];
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "3.0"^^<http://www.w3.org/2001/XMLSchema#double>;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "4.0"^^<http://www.w3.org/2001/XMLSchema#double>;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "7.0"^^<http://www.w3.org/2001/XMLSchema#double>
              ]
    ] .
}

```

---
### `spreadsheet.composite-values`

#### Description

It tells the spreadsheet triplifier to extract from the cells hyperlinks and comments. If enabled, the cells will be triplified as containers instead of literals (see [#308](https://github.com/SPARQL-Anything/sparql.anything/issues/308))

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Construct the dataset by extracting hyperlinks and comments from the cells.

###### Input


https://sparql-anything.cc/examples/Book3.xlsx

###### Query

```
CONSTRUCT 
  { 
    GRAPH ?g 
      { ?s ?p ?o .}
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book3.xlsx,spreadsheet.composite-values=true>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

###### Result

```turtle
<https://sparql-anything.cc/examples/Book3.xlsx#Sheet1> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    _:b3 .
    
    _:b4    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A" .
    
    _:b1    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_1_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_1_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_1_2" .
    
    _:b5    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B" .
    
    _:b6    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C" .
    
    _:b7    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A1" .
    
    _:b2    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_2_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_2_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_2_2" .
    
    _:b8    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B1" .
    
    _:b9    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C1" .
    
    _:b10   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A2" .
    
    _:b3    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_3_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_3_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_3_2" .
    
    _:b11   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B2" .
    
    _:b12   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C2" .
}

<https://sparql-anything.cc/examples/Book3.xlsx#Sheet2> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    _:b3 .
    
    _:b4    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A1" .
    
    _:b1    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_1_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_1_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_1_2" .
    
    _:b5    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B1" .
    
    _:b6    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C1" .
    
    _:b7    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A11" .
    
    _:b2    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_2_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_2_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_2_2" .
    
    _:b8    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B11" .
    
    _:b9    a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C11" .
    
    _:b10   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "A12" .
    
    _:b3    <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "_Row_3_0";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    "_Row_3_1";
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                    "_Row_3_2" .
    
    _:b11   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "B12";
            <http://sparql.xyz/facade-x/data/author>
                    "tc={EE8AE53B-0251-A642-8227-573C96F44050}";
            <http://sparql.xyz/facade-x/data/threadedComment>
                    "[Threaded comment]\n\nYour version of Excel allows you to read this threaded comment; however, any edits to it will get removed if the file is opened in a newer version of Excel. Learn more: https://go.microsoft.com/fwlink/?linkid=870924\n\nComment:\n    This is a comment\nReply:\n    This is a reply\nReply:\n    This is a second reply" .
    
    _:b12   a       <http://sparql.xyz/facade-x/data/STRING>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    "C12";
            <http://sparql.xyz/facade-x/data/address>
                    "http://www.example.org/C12";
            <http://sparql.xyz/facade-x/data/label>
                    "C12" .
}

```

---
### `spreadsheet.headers-row`

#### Description

It specifies the number of the row to use for extracting column headers. -- see [#179](https://github.com/SPARQL-Anything/sparql.anything/issues/179)

#### Valid Values

Any integer

#### Default Value

`1`

#### Examples

##### Example 1

Construct the dataset by using the headers of the columns to mint the property URIs.

###### Input


https://sparql-anything.cc/examples/Book1.xlsx

###### Query

```
CONSTRUCT 
  { 
    GRAPH ?g 
      { ?s ?p ?o .}
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true,spreadsheet.headers-row=2>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

###### Result

```turtle
<https://sparql-anything.cc/examples/Book1.xlsx#Sheet1> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2 .
    
    _:b1    <http://sparql.xyz/facade-x/data/A1>
                    "A";
            <http://sparql.xyz/facade-x/data/B1>
                    "B";
            <http://sparql.xyz/facade-x/data/C1>
                    "C" .
    
    _:b2    <http://sparql.xyz/facade-x/data/A1>
                    "A2";
            <http://sparql.xyz/facade-x/data/B1>
                    "B2";
            <http://sparql.xyz/facade-x/data/C1>
                    "C2" .
}

<https://sparql-anything.cc/examples/Book1.xlsx#Sheet2> {
    _:b0    a       <http://sparql.xyz/facade-x/ns/root>;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                    _:b1;
            <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                    _:b2 .
    
    _:b1    <http://sparql.xyz/facade-x/data/A11>
                    "A1";
            <http://sparql.xyz/facade-x/data/B11>
                    "B1";
            <http://sparql.xyz/facade-x/data/C11>
                    "C1" .
    
    _:b2    <http://sparql.xyz/facade-x/data/A11>
                    "A12";
            <http://sparql.xyz/facade-x/data/B11>
                    "B12";
            <http://sparql.xyz/facade-x/data/C11>
                    "C12" .
}

```

---
### `spreadsheet.ignore-columns-with-no-header`

#### Description

It tells the spreadsheet triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when spreadsheet.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see [#180](https://github.com/SPARQL-Anything/sparql.anything/issues/180)

#### Valid Values

Any integer

#### Default Value

`false`

#### Examples

##### Example 1

Construct the dataset by using the headers of the columns to mint the property URIs.

###### Input


https://sparql-anything.cc/examples/spreadsheet.xls

###### Query

```
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT DISTINCT  ?fred ?sally
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/spreadsheet.xls>
      { fx:properties
                  fx:spreadsheet.headers  true ;
                  fx:spreadsheet.ignore-columns-with-no-header  true
        GRAPH <https://sparql-anything.cc/examples/spreadsheet.xls#Sheet1>
          { ?root  rdf:type  fx:root ;
                   rdf:_1    _:b0 .
            _:b0   rdf:_1    ?fred .
            ?root  rdf:_2    _:b1 .
            _:b1   rdf:_1    ?sally
          }
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





