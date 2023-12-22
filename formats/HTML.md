<!-- This page has been generated with sparql-anything-documentation-generator module -->

# HTML

HTML can be captured by the Document Object Model (DOM) specification. HTML elements (also known as tags) can be considered containers.

According to the Facade-X model, SPARQL Anything uses:

RDF Properties for specifying tag attributes;
Container membership properties for specifying relations to child elements in the DOM tree. These may include text, which can be expressed as RDF literals of type xsd:string.
Tag names are used to type the container. Specifically, the tag name is used to mint a URI that identifies the class of the corresponding containers.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- html

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/html

## Default implementation

- [io.github.sparqlanything.html.HTMLTriplifier](../sparql-anything-html/src/main/java/io/github/sparqlanything/html/HTMLTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/simple.html

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.html>
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

[ rdf:type          fx:root , xhtml:html ;
  rdf:_1            [ rdf:type          xhtml:head ;
                      rdf:_1            [ rdf:type          xhtml:title ;
                                          rdf:_1            "Hello world!" ;
                                          whatwg:innerHTML  "Hello world!" ;
                                          whatwg:innerText  "Hello world!"
                                        ] ;
                      whatwg:innerHTML  "<title>Hello world!</title>" ;
                      whatwg:innerText  "Hello world! Hello world!"
                    ] ;
  rdf:_2            [ rdf:type          xhtml:body ;
                      rdf:_1            [ rdf:type          xhtml:p ;
                                          rdf:_1            "Hello world" ;
                                          xhtml:class       "paragraph" ;
                                          whatwg:innerHTML  "Hello world" ;
                                          whatwg:innerText  "Hello world"
                                        ] ;
                      whatwg:innerHTML  "<p class=\"paragraph\">Hello world</p>" ;
                      whatwg:innerText  "Hello world Hello world"
                    ] ;
  whatwg:innerHTML  "<head>\n <title>Hello world!</title>\n</head>\n<body>\n <p class=\"paragraph\">Hello world</p>\n</body>" ;
  whatwg:innerText  "Hello world! Hello world Hello world! Hello world! Hello world Hello world"
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [html.metadata](#htmlmetadata) | It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. -- See #164 | true/false | false |

---
### `html.metadata`

#### Description

It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. -- See #164

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Example 1

Extract triples embedded in the web page at the following address https://sparql-anything.cc/examples/Microdata1.html

###### Input

https://sparql-anything.cc/examples/Microdata1.html

###### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Microdata1.html,html.metadata=true>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

###### Result

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

[ rdf:type          fx:root , xhtml:html ;
  rdf:_1            [ rdf:type  xhtml:head ] ;
  rdf:_2            [ rdf:type          xhtml:body ;
                      rdf:_1            [ rdf:type          xhtml:div ;
                                          rdf:_1            [ rdf:type          xhtml:h1 ;
                                                              rdf:_1            "Avatar" ;
                                                              xhtml:itemprop    "name" ;
                                                              whatwg:innerHTML  "Avatar" ;
                                                              whatwg:innerText  "Avatar"
                                                            ] ;
                                          rdf:_2            [ rdf:type          xhtml:span ;
                                                              rdf:_1            "Director: James Cameron (born August 16, 1954)" ;
                                                              whatwg:innerHTML  "Director: James Cameron (born August 16, 1954)" ;
                                                              whatwg:innerText  "Director: James Cameron (born August 16, 1954)"
                                                            ] ;
                                          xhtml:itemscope   "" ;
                                          xhtml:itemtype    "https://schema.org/Movie" ;
                                          whatwg:innerHTML  "<h1 itemprop=\"name\">Avatar</h1> <span>Director: James Cameron (born August 16, 1954)</span>" ;
                                          whatwg:innerText  "Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954)"
                                        ] ;
                      whatwg:innerHTML  "<div itemscope itemtype=\"https://schema.org/Movie\">\n <h1 itemprop=\"name\">Avatar</h1> <span>Director: James Cameron (born August 16, 1954)</span>\n</div>" ;
                      whatwg:innerText  "Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954)"
                    ] ;
  whatwg:innerHTML  "<head></head>\n<body>\n <div itemscope itemtype=\"https://schema.org/Movie\">\n  <h1 itemprop=\"name\">Avatar</h1> <span>Director: James Cameron (born August 16, 1954)</span>\n </div>\n</body>" ;
  whatwg:innerText  "Avatar Director: James Cameron (born August 16, 1954)  Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954)"
] .

<https://sparql-anything.cc/examples/Microdata1.html>
        <http://www.w3.org/1999/xhtml/microdata#item>
                [ rdf:type                   <https://schema.org/Movie> ;
                  <https://schema.org/name>  "Avatar"
                ] .

```





