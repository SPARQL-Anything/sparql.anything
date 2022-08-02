# Bibtex

BibTeX is a text format used (typically together with LaTeX) to specify a list of references in a database file with the aim of separating bibliographic information from its presentation.
A BibTeX database file is formed by a list of bibliographic entries where each entry consists of the type (e.g. article, inproceedings etc.), a citation key, and key-value pairs for the other characteristics of an entry.
Each BibTeX entry can be represented as a  typed container that holds a set of key-value pairs.


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- bib
- bibtex

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/x-bibtex

## Default Transformation


### Data

```
@article{Knuth1984,
  title={Literate Programming},
  author={Donald E. Knuth},
  journal={The Computer Journal},
  volume={27},
  number={2},
  pages={97--111},
  year={1984},
  publisher={Oxford University Press}
}


@article{Berners2001,
  title={The semantic web},
  author={Berners-Lee, Tim and Hendler, James and Lassila, Ora},
  journal={Scientific american},
  volume={284},
  number={5},
  pages={34--43},
  year={2001}
}

```

Located at https://sparql-anything.cc/examples/example.bib

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.bib>
      { ?s  ?p  ?o }
  }
```

### Facade-X RDF

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root ;
  rdf:_1    [ rdf:type       xyz:article ;
              xyz:author     "Donald E. Knuth" ;
              xyz:journal    "The Computer Journal" ;
              xyz:number     "2" ;
              xyz:pages      "97--111" ;
              xyz:publisher  "Oxford University Press" ;
              xyz:title      "Literate Programming" ;
              xyz:volume     "27" ;
              xyz:year       "1984"
            ] ;
  rdf:_2    [ rdf:type     xyz:article ;
              xyz:author   "Berners-Lee, Tim and Hendler, James and Lassila, Ora" ;
              xyz:journal  "Scientific american" ;
              xyz:number   "5" ;
              xyz:pages    "34--43" ;
              xyz:title    "The semantic web" ;
              xyz:volume   "284" ;
              xyz:year     "2001"
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