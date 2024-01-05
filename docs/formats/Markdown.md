<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Markdown

Markdown is a lightweight markup language for writing formatted documents inspired to conventions of web posting. We can interpret a Markdown document as a sequence of blocks (e.g. paragraphs, lists, headings, code blocks). Some blocks (e.g. list items) contain other blocks, whereas others contain inline contents (e.g. links, images etc.). In SPARQL Anything, a document is represented as a list of typed containers. Where the type denotes the kind of block (e.g. heading, paragraph, emphasised text, link, image etc.); lists are needed for specifying the sequence of the blocks. Additional attributes such as the depth of the header or the type of list (bullets, numbers, etc...) can be also supported, relying on the key-value structure.

SPARQL Anything relies on the [CommonMark](https://github.com/commonmark/commonmark-java) Java implementation of Commons Markdown.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- md

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/markdown
- text/x-markdown

## Default implementation

- [io.github.sparqlanything.markdown.MARKDOWNTriplifier](../sparql-anything-markdown/src/main/java/io/github/sparqlanything/markdown/MARKDOWNTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/simple.md

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.md>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

### Facade-X RDF

```turtle
@prefix dc:     <http://purl.org/dc/elements/1.1/> .
@prefix eg:     <http://www.example.org/> .
@prefix fx:     <http://sparql.xyz/facade-x/ns/> .
@prefix ja:     <http://jena.hpl.hp.com/2005/11/Assembler#> .
@prefix owl:    <http://www.w3.org/2002/07/owl#> .
@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .
@prefix rss:    <http://purl.org/rss/1.0/> .
@prefix vcard:  <http://www.w3.org/2001/vcard-rdf/3.0#> .
@prefix whatwg: <https://html.spec.whatwg.org/#> .
@prefix xhtml:  <http://www.w3.org/1999/xhtml#> .
@prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .

[ rdf:type  xyz:Document , fx:root ;
  rdf:_1    [ rdf:type   xyz:Heading ;
              rdf:_1     "Title" ;
              xyz:level  "1"^^xsd:int
            ] ;
  rdf:_2    [ rdf:type  xyz:Paragraph ;
              rdf:_1    "The following list of issues:"
            ] ;
  rdf:_3    [ rdf:type  xyz:BulletList ;
              rdf:_1    [ rdf:type  xyz:ListItem ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    "first issue"
                                    ]
                        ] ;
              rdf:_2    [ rdf:type  xyz:ListItem ;
                          rdf:_1    [ rdf:type  xyz:Paragraph ;
                                      rdf:_1    "second issue"
                                    ]
                        ]
            ] ;
  rdf:_4    [ rdf:type  xyz:ThematicBreak ] ;
  rdf:_5    [ rdf:type  xyz:Paragraph ;
              rdf:_1    "Footer paragraph."
            ]
] .

```





