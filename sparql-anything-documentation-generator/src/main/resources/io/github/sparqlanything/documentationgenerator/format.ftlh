# ${format.name}

${format.description}

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

<#list format.extensions as extension>
- ${extension}
</#list>

## Media types

SPARQL Anything selects this transformer for the following media types:

<#list format.mediaTypes as mediaType>
- ${mediaType}
</#list>

## Default implementation

<#list format.triplifiers as triplifier>
- [${triplifier.name}](../sparql-anything-${format.name?lower_case}/src/main/java/com/github/sparqlanything/${format.name?lower_case}/${triplifier.simpleName}.java)
</#list>

## Default Transformation

### Data

Located at ${format.resourceExample}

### Query

```
${defaultTransformationQuery}
```

### Facade-X RDF

```turtle
${facadeXRdf?no_esc}
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
