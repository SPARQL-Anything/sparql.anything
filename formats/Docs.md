<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Docs

A word processing document is any text-based document compiled using word processor software. We can interpret a document (compiled with a Word processor) as a sequence of blocks (e.g. paragraphs, lists, headings, code blocks). Some blocks (e.g. list items) contain other blocks, whereas others contain inline contents (e.g. links, images etc.). A document can be represented as a list of typed containers. In fact, blocks can be specified as typed containers, where the type denotes the kind of block (e.g. heading, paragraph, emphasised text, link, image etc.); lists are needed for specifying the sequence of the blocks. Additional attributes such as the depth of the header or the type of list (bullets, numbers, etc...) can be also supported, relying on the key-value structure. Comments are interpreted as containers with three slots containing the id, the author and the text of the comment. Comment Containers are attached to the paragraph the comment refers to.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- docx

## Media types

SPARQL Anything selects this transformer for the following media types:

- application/vnd.openxmlformats-officedocument.wordprocessingml.document

## Default implementation

- [io.github.sparqlanything.docs.DocxTriplifier](../sparql-anything-docs/src/main/java/io/github/sparqlanything/docs/DocxTriplifier.java)

## Default Transformation

### Data


Located at https://sparql-anything.cc/examples/Doc1.docx

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx>
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

[ rdf:type  fx:root , xyz:Document;
  rdf:_1    [ rdf:type  xyz:Heading1;
              rdf:_1    "Title 1"
            ];
  rdf:_2    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph1"
            ];
  rdf:_3    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph2"
            ];
  rdf:_4    [ rdf:type  xyz:Heading2;
              rdf:_1    "Title 2"
            ];
  rdf:_5    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph3"
            ];
  rdf:_6    [ rdf:type  xyz:Paragraph;
              rdf:_1    ""
            ];
  rdf:_7    [ rdf:_1  [ rdf:_1  "A";
                        rdf:_2  "B";
                        rdf:_3  "C"
                      ];
              rdf:_2  [ rdf:_1  "A1";
                        rdf:_2  "B1";
                        rdf:_3  "C1"
                      ];
              rdf:_3  [ rdf:_1  "A2";
                        rdf:_2  "B2";
                        rdf:_3  "C2"
                      ]
            ]
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [docs.merge-paragraphs](#docsmerge-paragraphs) | It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved) | true/false | `false` |
| [docs.table-headers](#docstable-headers) | It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples. | true/false | `false` |

---
### `docs.merge-paragraphs`

#### Description

It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved)

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Construct the graph by merging multiple consecutive paragraphs into single a single slot.

###### Input


https://sparql-anything.cc/examples/Doc1.docx

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

[ rdf:type  xyz:Document , fx:root;
  rdf:_1    "Title 1\nParagraph1\nParagraph2\nTitle 2\nParagraph3\n\n";
  rdf:_2    [ rdf:_1  [ rdf:_1  "A";
                        rdf:_2  "B";
                        rdf:_3  "C"
                      ];
              rdf:_2  [ rdf:_1  "A1";
                        rdf:_2  "B1";
                        rdf:_3  "C1"
                      ];
              rdf:_3  [ rdf:_1  "A2";
                        rdf:_2  "B2";
                        rdf:_3  "C2"
                      ]
            ]
] .

```

---
### `docs.table-headers`

#### Description

It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples.

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Construct the dataset by using the headers of the columns of the tables to mint the property URIs.

###### Input


https://sparql-anything.cc/examples/Doc1.docx

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

[ rdf:type  xyz:Document , fx:root;
  rdf:_1    [ rdf:type  xyz:Heading1;
              rdf:_1    "Title 1"
            ];
  rdf:_2    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph1"
            ];
  rdf:_3    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph2"
            ];
  rdf:_4    [ rdf:type  xyz:Heading2;
              rdf:_1    "Title 2"
            ];
  rdf:_5    [ rdf:type  xyz:Paragraph;
              rdf:_1    "Paragraph3"
            ];
  rdf:_6    [ rdf:type  xyz:Paragraph;
              rdf:_1    ""
            ];
  rdf:_7    [ rdf:_1  [ xyz:A   "A1";
                        xyz:B   "B1";
                        xyz:C   "C1"
                      ];
              rdf:_2  [ xyz:A   "A2";
                        xyz:B   "B2";
                        xyz:C   "C2"
                      ]
            ]
] .

```





