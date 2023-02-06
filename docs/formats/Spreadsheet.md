# Spreadsheet documents

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

- [com.github.sparqlanything.spreadsheet.SpreadsheetTriplifier](../sparql-anything-spreadsheet/src/main/java/com/github/sparqlanything/spreadsheet/SpreadsheetTriplifier.java)

## Default Transformation


### Data

**Sheet1**

| A  | B  | C  |
|----|----|----|
| A1 | B1 | C1 |
| A2 | B2 | C2 |

**Sheet2**

| A1  | B1  | C1  |
|-----|-----|-----|
| A11 | B11 | C11 |
| A12 | B12 | C12 |


Located at https://sparql-anything.cc/examples/Book1.xslx

### Query

```
CONSTRUCT 
  { 
    GRAPH ?g 
      { ?s ?p ?o .}
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

<https://sparql-anything.cc/examples/Book1.xlsx#Sheet1> {
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C1"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A2" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B2" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C2"
              ]
    ] .

<https://sparql-anything.cc/examples/Book1.xlsx#Sheet2> {
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C1"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A11" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B11" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C11"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A12" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B12" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C12"
              ]
    ] .
}


```


## Options

### Summary

| Option name                   | Description                                                                                                                                                                  | Valid Values | Default Value |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------|---------------|
| spreadsheet.headers           | It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.                                          | true/false   | false         |
| spreadsheet.evaluate-formulas | It tells the spreadsheet triplifier to evaluate formulas of the spreadsheet.                                                                                                 | true/false   | false         |
| spreadsheet.composite-values  | It tells the spreadsheet triplifier to extract from the cells hyperlinks and comments. If enabled, the cells will be triplified as containers instead of literals (see #308) | true/false   | false         |


### `spreadsheet.headers`

#### Description

It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

**Sheet1**

| A  | B  | C  |
|----|----|----|
| A1 | B1 | C1 |
| A2 | B2 | C2 |

**Sheet2**

| A1  | B1  | C1  |
|-----|-----|-----|
| A11 | B11 | C11 |
| A12 | B12 | C12 |

Located at https://sparql-anything.cc/examples/Book1.xlsx

##### Use Case 1: Construct the dataset by using the headers of the columns to mint the property URIs.

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
<https://sparql-anything.cc/examples/Book1.xlsx#Sheet2> {
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://sparql.xyz/facade-x/data/A1>
                        "A11" ;
                <http://sparql.xyz/facade-x/data/B1>
                        "B11" ;
                <http://sparql.xyz/facade-x/data/C1>
                        "C11"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://sparql.xyz/facade-x/data/A1>
                        "A12" ;
                <http://sparql.xyz/facade-x/data/B1>
                        "B12" ;
                <http://sparql.xyz/facade-x/data/C1>
                        "C12"
              ]
    ] .
}

<https://sparql-anything.cc/examples/Book1.xlsx#Sheet1> {
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://sparql.xyz/facade-x/data/A>
                        "A1" ;
                <http://sparql.xyz/facade-x/data/B>
                        "B1" ;
                <http://sparql.xyz/facade-x/data/C>
                        "C1"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://sparql.xyz/facade-x/data/A>
                        "A2" ;
                <http://sparql.xyz/facade-x/data/B>
                        "B2" ;
                <http://sparql.xyz/facade-x/data/C>
                        "C2"
              ]
    ] .
}
```


### `spreadsheet.evaluate-formulas`

#### Description

It tells the spreadsheet triplifier to evaluate formulas of the spreadsheet.

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

**Sheet1**

| A   | B   | C        |
|-----|-----|----------|
| 1   | 2   | `=A2+B2` |
| 3   | 4   | `=A3+B3` |



Located at https://sparql-anything.cc/examples/Book2.xlsx

##### Use Case 1: Construct the dataset by evaluating the formulas.

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
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "A" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "B" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "C"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "1.0"^^<http://www.w3.org/2001/XMLSchema#double> ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "2.0"^^<http://www.w3.org/2001/XMLSchema#double> ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "3.0"^^<http://www.w3.org/2001/XMLSchema#double>
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "3.0"^^<http://www.w3.org/2001/XMLSchema#double> ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "4.0"^^<http://www.w3.org/2001/XMLSchema#double> ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "7.0"^^<http://www.w3.org/2001/XMLSchema#double>
              ]
    ] .
}
```

### `spreadsheet.composite-values`

#### Description

It tells the spreadsheet triplifier to extract from the cells hyperlinks and comments. If enabled, the cells will be triplified as containers instead of literals (see #308)

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

Located at https://sparql-anything.cc/examples/Book3.xlsx

##### Use Case 1: Use Case 1: Construct the dataset by extracting hyperlinks and comments from the cells.

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
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B2"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A1"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_1_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_1_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_1_2"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_2_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_2_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_2_2"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_3_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_3_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet1_Row_3_2"
              ]
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B1"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C2"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A2"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C1"
    ] .
}

<https://sparql-anything.cc/examples/Book3.xlsx#Sheet2> {
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C12" ;
      <http://sparql.xyz/facade-x/data/address>
              "http://www.example.org/C12" ;
      <http://sparql.xyz/facade-x/data/label>
              "C12"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B11"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A1"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A12"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B1"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C11"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "A11"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "B12" ;
      <http://sparql.xyz/facade-x/data/author>
              "tc={EE8AE53B-0251-A642-8227-573C96F44050}" ;
      <http://sparql.xyz/facade-x/data/threadedComment>
              "[Threaded comment]\n\nYour version of Excel allows you to read this threaded comment; however, any edits to it will get removed if the file is opened in a newer version of Excel. Learn more: https://go.microsoft.com/fwlink/?linkid=870924\n\nComment:\n    This is a comment\nReply:\n    This is a reply\nReply:\n    This is a second reply"
    ] .
    
    [ a       <http://sparql.xyz/facade-x/ns/root> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_1_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_1_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_1_2"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_2_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_2_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_2_2"
              ] ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
              [ <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_3_0" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_3_1" ;
                <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                        "https://sparql-anything.cc/examples/Book3.xlsx#Sheet2_Row_3_2"
              ]
    ] .
    
    [ a       <http://sparql.xyz/facade-x/data/STRING> ;
      <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
              "C1"
    ] .
}


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