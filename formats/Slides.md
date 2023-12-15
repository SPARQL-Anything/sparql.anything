# Slides

A slide is a single page of a presentation. Collectively, a group of slides may be known as a slide deck.
We can interpret a slide deck as a sequence of slides where each slide is a sequence of blocks (e.g. title, text boxes etc.), called shapes.
Each shape may have multiple paragraphs, where each paragraph can be seen as a sequence of text runs (i.e. pieces of text).
Each piece of text is a container for the text and possibly other annotations on the text (e.g. hyperlinks).
<!-- 
A slide deck can be represented as a list of typed containers (of type `Slide`).
Each slide contains other *typed blocks*, where the type denotes the kind of block (e.g. title, subtitle etc.)
-->

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

Located at https://sparql-anything.cc/examples/Presentation3.pptx

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Presentation3.pptx>
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
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "This is a test presentation"
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:SUBTITLE ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "This is the subtitle"
                                                            ]
                                                ]
                                    ]
                        ]
            ] ;
  rdf:_2    [ rdf:type  xyz:Slide ;
              rdf:_1    [ rdf:type  xyz:TITLE ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "First slide"
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:CONTENT ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "Link"
                                                            ] ;
                                                  rdf:_2    [ rdf:type  xyz:Hyperlink ;
                                                              rdf:_1    "https://sparql-anything.cc/"
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "Bullet 2"
                                                            ]
                                                ]
                                    ]
                        ]
            ] ;
  rdf:_3    [ rdf:type  xyz:Slide ;
              rdf:_1    [ rdf:type  xyz:TITLE ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "Second "
                                                            ]
                                                ] ;
                                      rdf:_2    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "slide"
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:CONTENT ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "Bullet 1"
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    [ rdf:type  xyz:TextRun ;
                                                  rdf:_1    [ rdf:type  xyz:Text ;
                                                              rdf:_1    "Bullet 2"
                                                            ]
                                                ]
                                    ]
                        ]
            ]
] .
```


## Options

### Summary

| Option name             | Description                                                                             | Valid Values | Default Value |
|-------------------------|-----------------------------------------------------------------------------------------|--------------|---------------|
| slides.extract-sections | It tells the document triplifier to extract the sections of the presentation (see #435) | true/false   | false         |


### `slides.extract-sections`

#### Description

It tells the document triplifier to extract the sections of the presentation (see #435)

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

### Data

Located at https://sparql-anything.cc/examples/Presentation2.pptx

##### Use Case 1: Construct the dataset by extracting the 

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Presentation2.pptx,slides.extract-sections=true>
      { ?s  ?p  ?o }
  }
```

###### Result

```turtle
@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root , xyz:Presentation ;
  rdf:_1    [ rdf:type  xyz:Section ;
              rdf:_1    [ rdf:type  xyz:Slide ;
                          rdf:_1    [ rdf:type  xyz:CENTERED_TITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "This is a test presentation"
                                                                        ]
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:SUBTITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "This is the subtitle"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:SectionName ;
                          rdf:_1    "Default Section"
                        ]
            ] ;
  rdf:_2    [ rdf:type  xyz:Section ;
              rdf:_1    [ rdf:type  xyz:Slide ;
                          rdf:_1    [ rdf:type  xyz:TITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "First slide – first section"
                                                                        ]
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:CONTENT ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ] ;
                                      rdf:_2    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:Slide ;
                          rdf:_1    [ rdf:type  xyz:TITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Second slide - first section"
                                                                        ]
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:CONTENT ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ] ;
                                      rdf:_2    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_3    [ rdf:type  xyz:SectionName ;
                          rdf:_1    "First section"
                        ]
            ] ;
  rdf:_3    [ rdf:type  xyz:Section ;
              rdf:_1    [ rdf:type  xyz:Slide ;
                          rdf:_1    [ rdf:type  xyz:TITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "First slide – second section"
                                                                        ]
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:CONTENT ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ] ;
                                      rdf:_2    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:Slide ;
                          rdf:_1    [ rdf:type  xyz:TITLE ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Second slide "
                                                                        ]
                                                            ] ;
                                                  rdf:_2    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "- second "
                                                                        ]
                                                            ] ;
                                                  rdf:_3    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "section"
                                                                        ]
                                                            ]
                                                ]
                                    ] ;
                          rdf:_2    [ rdf:type  xyz:CONTENT ;
                                      rdf:_1    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ] ;
                                      rdf:_2    [ rdf:type  xyz:Paragraph ;
                                                  rdf:_1    [ rdf:type  xyz:TextRun ;
                                                              rdf:_1    [ rdf:type  xyz:Text ;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ] ;
              rdf:_3    [ rdf:type  xyz:SectionName ;
                          rdf:_1    "Second Section"
                        ]
            ]
] .

```
