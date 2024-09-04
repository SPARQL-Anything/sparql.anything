<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Slides

A slide is a single page of a presentation. Collectively, a group of slides may be known as a slide deck.
We can interpret a slide deck as a sequence of slides where each slide is a sequence of blocks (e.g. title, text boxes etc.), called shapes.
Each shape may have multiple paragraphs, where each paragraph can be seen as a sequence of text runs (i.e. pieces of text).
Each piece of text is a container for the text and possibly other annotations on the text (e.g. hyperlinks).


## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- pptx

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/vnd.openxmlformats-officedocument.presentationml.presentation

## Default implementation

- [io.github.sparqlanything.slides.PptxTriplifier](../sparql-anything-slides/src/main/java/io/github/sparqlanything/slides/PptxTriplifier.java)

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

[ rdf:type  fx:root , xyz:Presentation;
  rdf:_1    [ rdf:type  xyz:Slide;
              rdf:_1    [ rdf:type  xyz:CENTERED_TITLE;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "This is a test presentation"
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:SUBTITLE;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "This is the subtitle"
                                                            ]
                                                ]
                                    ]
                        ]
            ];
  rdf:_2    [ rdf:type  xyz:Slide;
              rdf:_1    [ rdf:type  xyz:TITLE;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "First slide"
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:CONTENT;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "Link"
                                                            ];
                                                  rdf:_2    [ rdf:type  xyz:Hyperlink;
                                                              rdf:_1    "https://sparql-anything.cc/"
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "Bullet 2"
                                                            ]
                                                ]
                                    ]
                        ]
            ];
  rdf:_3    [ rdf:type  xyz:Slide;
              rdf:_1    [ rdf:type  xyz:TITLE;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "Second "
                                                            ]
                                                ];
                                      rdf:_2    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "slide"
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:CONTENT;
                          rdf:_1    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
                                                              rdf:_1    "Bullet 1"
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:Paragraph;
                                      rdf:_1    [ rdf:type  xyz:TextRun;
                                                  rdf:_1    [ rdf:type  xyz:Text;
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

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [slides.extract-sections](#slidesextract-sections) | It tells the document triplifier to extract the sections of the presentation (see [#435](https://github.com/SPARQL-Anything/sparql.anything/issues/435)) | true/false | `false` |

---
### `slides.extract-sections`

#### Description

It tells the document triplifier to extract the sections of the presentation (see [#435](https://github.com/SPARQL-Anything/sparql.anything/issues/435))

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1



###### Input


https://sparql-anything.cc/examples/Presentation2.pptx

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

[ rdf:type  xyz:Presentation , fx:root;
  rdf:_1    [ rdf:type  xyz:Section;
              rdf:_1    [ rdf:type  xyz:Slide;
                          rdf:_1    [ rdf:type  xyz:CENTERED_TITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "This is a test presentation"
                                                                        ]
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:SUBTITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "This is the subtitle"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:SectionName;
                          rdf:_1    "Default Section"
                        ]
            ];
  rdf:_2    [ rdf:type  xyz:Section;
              rdf:_1    [ rdf:type  xyz:Slide;
                          rdf:_1    [ rdf:type  xyz:TITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "First slide – first section"
                                                                        ]
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:CONTENT;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ];
                                      rdf:_2    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:Slide;
                          rdf:_1    [ rdf:type  xyz:TITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Second slide - first section"
                                                                        ]
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:CONTENT;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ];
                                      rdf:_2    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_3    [ rdf:type  xyz:SectionName;
                          rdf:_1    "First section"
                        ]
            ];
  rdf:_3    [ rdf:type  xyz:Section;
              rdf:_1    [ rdf:type  xyz:Slide;
                          rdf:_1    [ rdf:type  xyz:TITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "First slide – second section"
                                                                        ]
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:CONTENT;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ];
                                      rdf:_2    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_2    [ rdf:type  xyz:Slide;
                          rdf:_1    [ rdf:type  xyz:TITLE;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Second slide "
                                                                        ]
                                                            ];
                                                  rdf:_2    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "- second "
                                                                        ]
                                                            ];
                                                  rdf:_3    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "section"
                                                                        ]
                                                            ]
                                                ]
                                    ];
                          rdf:_2    [ rdf:type  xyz:CONTENT;
                                      rdf:_1    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 1"
                                                                        ]
                                                            ]
                                                ];
                                      rdf:_2    [ rdf:type  xyz:Paragraph;
                                                  rdf:_1    [ rdf:type  xyz:TextRun;
                                                              rdf:_1    [ rdf:type  xyz:Text;
                                                                          rdf:_1    "Bullet 2"
                                                                        ]
                                                            ]
                                                ]
                                    ]
                        ];
              rdf:_3    [ rdf:type  xyz:SectionName;
                          rdf:_1    "Second Section"
                        ]
            ]
] .

```





