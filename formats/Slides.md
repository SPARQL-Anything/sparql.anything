# Slides

A slide is a single page of a presentation. Collectively, a group of slides may be known as a slide deck.
We can interpret a slide deck as a sequence of slides where each slide is a sequence of blocks (e.g. title, text boxes etc.).
A slide deck can be represented as a list of typed containers (of type `Slide`).
Each slide contains other *typed blocks*, where the type denotes the kind of block (e.g. title, subtitle etc.).

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- pptx

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/vnd.openxmlformats-officedocument.presentationml.presentation

## Default implementation

- [io.github.sparqlanything.slides.PptxTriplifier](../sparql-anything-slides/src/main/java/com/github/sparqlanything/slides/PptxTriplifier.java)

## Default Transformation


### Data

Located at https://sparql-anything.cc/examples/Presentation1.pptx

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Presentation1.pptx>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }
```

### Facade-X RDF

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root , xyz:Presentation ;
  rdf:_1    [ rdf:type  xyz:Slide ;
              rdf:_1    [ rdf:type  xyz:CENTERED_TITLE ;
                          rdf:_1    "This is a test presentation"
                        ] ;
              rdf:_2    [ rdf:type  xyz:SUBTITLE ;
                          rdf:_1    "This is the subtitle"
                        ]
            ] ;
  rdf:_2    [ rdf:type  xyz:Slide ;
              rdf:_1    [ rdf:type  xyz:TITLE ;
                          rdf:_1    "First slide"
                        ] ;
              rdf:_2    [ rdf:type  xyz:CONTENT ;
                          rdf:_1    "Bullet 1\nBullet 2"
                        ]
            ] ;
  rdf:_3    [ rdf:type  xyz:Slide ;
              rdf:_1    [ rdf:type  xyz:TITLE ;
                          rdf:_1    "Second slide"
                        ] ;
              rdf:_2    [ rdf:type  xyz:CONTENT ;
                          rdf:_1    "Bullet 1\nBullet 2"
                        ]
            ]
] .
```

<!--
## Options

### Summary

|Option name|Description|Valid Values| Default Value  |
|-----------|-----------|------------|----------------|
|docs.table-headers|It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples.|true/false| false          |
|docs.merge-paragraphs|It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved)|true/false| false          |


### `docs.table-headers`

#### Description

It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

Located at https://sparql-anything.cc/examples/Doc1.docx

##### Use Case 1: Construct the dataset by using the headers of the columns of the tables to mint the property URIs.

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.table-headers=true>
      { ?s  ?p  ?o }
  }
```

###### Result

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  xyz:Document , fx:root ;
  rdf:_1    [ rdf:type  xyz:Heading1 ;
              rdf:_1    "Title 1"
            ] ;
  rdf:_2    [ rdf:type  xyz:Paragraph ;
              rdf:_1    "Paragraph1"
            ] ;
  rdf:_3    [ rdf:type  xyz:Paragraph ;
              rdf:_1    "Paragraph2"
            ] ;
  rdf:_4    [ rdf:type  xyz:Heading2 ;
              rdf:_1    "Title 2"
            ] ;
  rdf:_5    [ rdf:type  xyz:Paragraph ;
              rdf:_1    "Paragraph3"
            ] ;
  rdf:_6    [ rdf:type  xyz:Paragraph ;
              rdf:_1    ""
            ] ;
  rdf:_7    [ rdf:_1  [ xyz:A   "A1" ;
                        xyz:B   "B1" ;
                        xyz:C   "C1"
                      ] ;
              rdf:_2  [ xyz:A   "A2" ;
                        xyz:B   "B2" ;
                        xyz:C   "C2"
                      ]
            ]
] .
```

### `docs.merge-paragraphs`

#### Description

It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved)

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

Located at https://sparql-anything.cc/examples/Doc1.docx

##### Use Case 1: Use Case 1: Construct the graph by merging multiple consecutive paragraphs into single a single slot.

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.merge-paragraphs=true>
      { ?s  ?p  ?o }
  }
```

###### Result

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  xyz:Document , fx:root ;
  rdf:_1    "Title 1\nParagraph1\nParagraph2\nTitle 2\nParagraph3\n\n" ;
  rdf:_2    [ rdf:_1  [ rdf:_1  "A" ;
                        rdf:_2  "B" ;
                        rdf:_3  "C"
                      ] ;
              rdf:_2  [ rdf:_1  "A1" ;
                        rdf:_2  "B1" ;
                        rdf:_3  "C1"
                      ] ;
              rdf:_3  [ rdf:_1  "A2" ;
                        rdf:_2  "B2" ;
                        rdf:_3  "C2"
                      ]
            ]
] .
```

-->

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