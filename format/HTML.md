# HTML

HTML can be captured by the Document Object Model (DOM) specification.
HTML elements (also known as tags) can be considered containers.

According to the Facade-X model, SPARQL Anything uses:
- RDF Properties for specifying tag attributes;
- Container membership properties for specifying relations to child elements in the DOM tree.  These may include text, which can be expressed as RDF literals of type *xsd:string*.
- Tag names are used to type the container. Specifically, the tag name is used to mint a URI that identifies the class of the corresponding containers.

## Supported file extensions

.html

## Supported media types

text/html

## Default Transformation

### Data

```html
<html>
   <head>
      <title>Hello world!</title>
   </head>
   <body>
      <p class="paragraph">Hello world</p>
   </body>
</html>
```


### Query

```
CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.html>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF


```
@prefix fx:     <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix whatwg: <https://html.spec.whatwg.org/#> .
@prefix xhtml:  <http://www.w3.org/1999/xhtml#> .
@prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .

[ rdf:type          xhtml:html , fx:root ;
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
  whatwg:innerHTML  "<head> \n <title>Hello world!</title> \n</head> \n<body> \n <p class=\"paragraph\">Hello world</p>  \n</body>" ;
  whatwg:innerText  "Hello world! Hello world Hello world! Hello world! Hello world Hello world"
] .
```

## Options

### Summary

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|html.selector|A CSS selector that restricts the HTML tags to consider for the triplification.|Any valid CSS selector.|No Value|
|html.browser|It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](../BROWSER.md).|chromium\|webkit\|firefox|No Value|
|html.browser.timeout|When using a browser to nagivate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout. |any integer|30000|
|html.browser.wait|When using a browser to nagivate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML.|any integer|No Value|
|html.browser.screenshot|When using a browser to nagivate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here.|a file URI e.g. "file:///tmp/screenshot.png" |No Value|
|html.metadata|It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. (cf. [issue 164](https://github.com/SPARQL-Anything/sparql.anything/issues/164))|true/false|false|


### html.selector

#### Description

A CSS selector that restricts the HTML tags to consider for the triplification.

#### Valid Values

Any valid CSS selector.

#### Default Value

No value

#### Examples

##### Input

```html
<html>
   <head>
      <title>Hello world!</title>
   </head>
   <body>
      <p class="paragraph">Hello world</p>
   </body>
</html>
```

Located at [https://sparql-anything.cc/examples/simple.html](https://sparql-anything.cc/examples/simple.html)

##### Use Case 1: Selecting text contained in elements of the class "paragraph"

###### Query

```
SELECT  ?text
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.html,html.selector=.paragraph>
      { ?s  whatwg:innerText  ?text }
  }
```

###### Result

```
-----------------
| text          |
=================
| "Hello world" |
-----------------
```

### html.browser

#### Description

It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](../BROWSER.md).

#### Valid Values

chromium\|webkit\|firefox

#### Default Value

No value

#### Examples

Please refer to the [justin2004](https://github.com/justin2004)'s [blogpost on Scraping Webpages with SPARQL](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

### html.browser.timeout

#### Description

When using a browser to nagivate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout.

#### Valid Values

any integer

#### Default Value

30000

#### Examples

Please refer to the [justin2004](https://github.com/justin2004)'s [blogpost on Scraping Webpages with SPARQL](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).


### html.browser.wait

#### Description

When using a browser to nagivate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML.

#### Valid Values

any integer

#### Default Value

No Value

#### Examples

Please refer to the [justin2004](https://github.com/justin2004)'s [blogpost on Scraping Webpages with SPARQL](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).

### html.browser.screenshot

#### Description

When using a browser to navigate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here.

#### Valid Values

a file URI e.g. "file:///tmp/screenshot.png"

#### Default Value

No Value

#### Examples

Please refer to the [justin2004](https://github.com/justin2004)'s [blogpost on Scraping Webpages with SPARQL](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).


### html.metadata

#### Description

It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. (cf. [issue 164](https://github.com/SPARQL-Anything/sparql.anything/issues/164))

#### Valid Values

true/false

#### Default Value

false

#### Examples

##### Input

```html

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

Located at https://sparql-anything.cc/examples/Microdata1.html

##### UC1: Extract triples embedded in the web page at the following address https://sparql-anything.cc/examples/Microdata1.html

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

```

@prefix fx:     <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
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
                      whatwg:innerHTML  "<div itemscope itemtype=\"https://schema.org/Movie\"> \n <h1 itemprop=\"name\">Avatar</h1> <span>Director: James Cameron (born August 16, 1954)</span> \n</div>" ;
                      whatwg:innerText  "Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954)"
                    ] ;
  whatwg:innerHTML  "<head></head>\n<body> \n <div itemscope itemtype=\"https://schema.org/Movie\"> \n  <h1 itemprop=\"name\">Avatar</h1> <span>Director: James Cameron (born August 16, 1954)</span> \n </div>  \n</body>" ;
  whatwg:innerText  "Avatar Director: James Cameron (born August 16, 1954)  Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954) Avatar Director: James Cameron (born August 16, 1954)"
] .

<https://sparql-anything.cc/examples/Microdata1.html>
        <http://www.w3.org/1999/xhtml/microdata#item>
                [ rdf:type                   <https://schema.org/Movie> ;
                  <https://schema.org/name>  "Avatar"
                ] .

```
