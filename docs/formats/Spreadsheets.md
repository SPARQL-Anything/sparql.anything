# Spreadsheet documents

A spreadsheet is a computer application for computation, organization, analysis and storage of data in tabular form. 
Each cell may contain either numeric or text data, or the results of formulas that automatically calculate and display a value based on the contents of other cells.   


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- xls
- xlsx

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/vnd.ms-excel
- application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

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

|Option name|Description|Valid Values| Default Value |
|-----------|-----------|------------|---------------|
|spreadsheet.headers|It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.|true/false| false         |


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