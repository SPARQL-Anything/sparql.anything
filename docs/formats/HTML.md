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

```HTML
<html>
   <head>
      <title>Hello world!</title>
   </head>
   <body>
      <p class="paragraph">Hello world</p>
   </body>
</html>

```

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
| [html.selector](#htmlselector) | A CSS selector that restricts the HTML tags to consider for the triplification. | Any valid CSS selector. | `:root` |
| [html.metadata](#htmlmetadata) | It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. -- See [#164](https://github.com/SPARQL-Anything/sparql.anything/issues/164) | true/false | `false` |
| [html.browser](#htmlbrowser) | It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](https://github.com/SPARQL-Anything/sparql.anything/blob/v1.0-DEV/BROWSER.md) and [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql). | chromium|webkit|firefox | Not set |
| [html.browser.wait](#htmlbrowserwait) | When using a browser to navigate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML. -- See See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql). | Any integer | Not set |
| [html.browser.screenshot](#htmlbrowserscreenshot) | When using a browser to navigate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here. See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql). | Any valid URL | Not set |
| [html.browser.timeout](#htmlbrowsertimeout) | When using a browser to navigate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout -- See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql). | Any integer | `30000` |

---
### `html.selector`

#### Description

A CSS selector that restricts the HTML tags to consider for the triplification.

#### Valid Values

Any valid CSS selector.

#### Default Value

`:root`

#### Examples

##### Example 1

Selecting text contained in elements of the class &quot;paragraph&quot;

###### Input

```HTML
<html>
   <head>
      <title>Hello world!</title>
   </head>
   <body>
      <p class="paragraph">Hello world</p>
   </body>
</html>

```

https://sparql-anything.cc/examples/simple.html

###### Query

```
PREFIX  whatwg: <https://html.spec.whatwg.org/#>

SELECT  ?text
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.html,html.selector=.paragraph>
      { ?s  whatwg:innerText  ?text }
  }

```

###### Result

```turtle
-----------------
| text          |
=================
| "Hello world" |
-----------------

```

---
### `html.metadata`

#### Description

It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. -- See [#164](https://github.com/SPARQL-Anything/sparql.anything/issues/164)

#### Valid Values

true/false

#### Default Value

`false`

#### Examples

##### Example 1

Extract triples embedded in the web page at the following address https://sparql-anything.cc/examples/Microdata1.html

###### Input

```HTML
<!--
  ~ Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<!DOCTYPE html>
<html>

<body>

	<div itemscope itemtype="https://schema.org/Movie">
		<h1 itemprop="name">Avatar</h1>
		<span>Director: James Cameron (born August 16, 1954)</span>
	</div>

</body>

</html>
```

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

---
### `html.browser`

#### Description

It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](https://github.com/SPARQL-Anything/sparql.anything/blob/v1.0-DEV/BROWSER.md) and [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

#### Valid Values

chromium|webkit|firefox

#### Default Value

Not set


---
### `html.browser.wait`

#### Description

When using a browser to navigate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML. -- See See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

#### Valid Values

Any integer

#### Default Value

Not set


---
### `html.browser.screenshot`

#### Description

When using a browser to navigate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here. See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

#### Valid Values

Any valid URL

#### Default Value

Not set


---
### `html.browser.timeout`

#### Description

When using a browser to navigate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout -- See [justin2004&#39;s blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

#### Valid Values

Any integer

#### Default Value

`30000`






