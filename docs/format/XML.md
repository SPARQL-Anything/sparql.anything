# XML

The following analysis refers to the [Document Object Model (DOM) specification](https://dom.spec.whatwg.org).
XML elements (also known as tags) can be definitely considered containers, so we can reuse both the `rdf:Property` component for specifying tag attributes, and container membership properties for specifying relations to child elements in the DOM tree.
These may include text, which are expressed as RDF literals of type `xsd:string`. 
Tag names are represented as RDF types: `rdf:type`.
SPARQL Anything reuses namespaces declared within the original document to name properties and types, when available, otherwise fallbacks to the default \tt{xyz:}.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- .xml

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/xml
- application/xml

## Default Transformation

### Data

```xml
<breakfast_menu>
   <food>
      <name>Belgian Waffles</name>
      <price>$5.95</price>
      <description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>
      <calories>650</calories>
   </food>
   <food>
      <name>Strawberry Belgian Waffles</name>
      <price>$7.95</price>
      <description>Light Belgian waffles covered with strawberries and whipped cream</description>
      <calories>900</calories>
   </food>
   <food>
      <name>Berry-Berry Belgian Waffles</name>
      <price>$8.95</price>
      <description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description>
      <calories>900</calories>
   </food>
</breakfast_menu>

```

### Query

```

CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.xml>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF:

```turtle
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

[ a       xyz:breakfast_menu, fx:root ;
  rdf:_1  [ a       xyz:food ;
            rdf:_1  [ a       xyz:name ;
                      rdf:_1  "Belgian Waffles"
                    ] ;
            rdf:_2  [ a       xyz:price ;
                      rdf:_1  "$5.95"
                    ] ;
            rdf:_3  [ a       xyz:description ;
                      rdf:_1  "Two of our famous Belgian Waffles with plenty of real maple syrup"
                    ] ;
            rdf:_4  [ a       xyz:calories ;
                      rdf:_1  "650"
                    ]
          ] ;
  rdf:_2  [ a       xyz:food ;
            rdf:_1  [ a       xyz:name ;
                      rdf:_1  "Strawberry Belgian Waffles"
                    ] ;
            rdf:_2  [ a       xyz:price ;
                      rdf:_1  "$7.95"
                    ] ;
            rdf:_3  [ a       xyz:description ;
                      rdf:_1  "Light Belgian waffles covered with strawberries and whipped cream"
                    ] ;
            rdf:_4  [ a       xyz:calories ;
                      rdf:_1  "900"
                    ]
          ] ;
  rdf:_3  [ a       xyz:food ;
            rdf:_1  [ a       xyz:name ;
                      rdf:_1  "Berry-Berry Belgian Waffles"
                    ] ;
            rdf:_2  [ a       xyz:price ;
                      rdf:_1  "$8.95"
                    ] ;
            rdf:_3  [ a       xyz:description ;
                      rdf:_1  "Light Belgian waffles covered with an assortment of fresh berries and whipped cream"
                    ] ;
            rdf:_4  [ a       xyz:calories ;
                      rdf:_1  "900"
                    ]
          ]
] .
```

## Options

### Summary

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|xml.path|One or more XPath expressions as filters. E.g. `xml.path=value` or `xml.path.1`, `xml.path.2`,`...` to add multiple expressions.|Any valid XPath||

---

### `xml.path`

The following example shows the usage of the property `xml.path`.

#### Data

```xml

<items>
	<magazine issn="0959-9630">
	        <title>Amiga Computing</title>
	        <publish_date>1997-10-01</publish_date>
	</magazine>
    <book isbn="978-3-12-148410-0">
        <author>Gambardella, Matthew</author>
        <title>XML Developer's Guide</title>
        <genre>Computer</genre>
        <price>44.95</price>
        <publish_date>2000-10-01</publish_date>
        <description>An in-depth look at creating applications
            with XML.</description>
    </book>
    <book isbn="928-3-16-148410-0">
        <author>Ralls, Kim</author>
        <title>Midnight Rain</title>
        <genre>Fantasy</genre>
        <price>5.95</price>
        <publish_date>2000-12-16</publish_date>
        <description>A former architect battles corporate zombies,
            an evil sorceress, and her own childhood to become queen
            of the world.</description>
    </book>
    <book isbn="432-3-16-143110-1">
        <author>Corets, Eva</author>
        <title>Maeve Ascendant</title>
        <genre>Fantasy</genre>
        <price>5.95</price>
        <publish_date>2000-11-17</publish_date>
        <description>After the collapse of a nanotechnology
            society in England, the young survivors lay the
            foundation for a new society.</description>
    </book>
</items>

```

#### Query

```sparql

CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
	  { 
		  fx:properties fx:location "https://sparql-anything.cc/examples/simple.xml" ; 
		  		fx:xml.path "//books" fx:blank-nodes false .
		  ?s  ?p  ?o 
	  }
  }

```

#### Output

```turtle

@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.
@prefix fx:   <http://sparql.xyz/facade-x/ns/>.
@prefix xyz:  <http://sparql.xyz/facade-x/data/>.
@prefix xsd:  <http://www.w3.org/2001/XMLSchema#>.


<http://www.example.org/document/3:book/3:genre>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Fantasy" .

<http://www.example.org/document/2:book/6:description>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "A former architect battles corporate zombies,\n            an evil sorceress, and her own childhood to become queen\n            of the world." .

<http://www.example.org/document/1:book/1:author>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Gambardella, Matthew" .

<http://www.example.org/document>
        a       <http://sparql.xyz/facade-x/ns/root> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://www.example.org/document/1:book> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://www.example.org/document/2:book> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                <http://www.example.org/document/3:book> .

<http://www.example.org/document/3:book/1:author>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Corets, Eva" .

<http://www.example.org/document/1:book/5:publish_date>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "2000-10-01" .

<http://www.example.org/document/2:book/3:genre>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Fantasy" .

<http://www.example.org/document/2:book/5:publish_date>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "2000-12-16" .

<http://www.example.org/document/3:book/4:price>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "5.95" .

<http://www.example.org/document/1:book/3:genre>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Computer" .

<http://www.example.org/document/3:book/2:title>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Maeve Ascendant" .

<http://www.example.org/document/1:book>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://www.example.org/document/1:book/1:author> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://www.example.org/document/1:book/2:title> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                <http://www.example.org/document/1:book/3:genre> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                <http://www.example.org/document/1:book/4:price> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                <http://www.example.org/document/1:book/5:publish_date> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6>
                <http://www.example.org/document/1:book/6:description> ;
        <http://sparql.xyz/facade-x/data/isbn>
                "978-3-12-148410-0" .

<http://www.example.org/document/2:book/4:price>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "5.95" .

<http://www.example.org/document/1:book/6:description>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "An in-depth look at creating applications\n            with XML." .

<http://www.example.org/document/3:book/5:publish_date>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "2000-11-17" .

<http://www.example.org/document/2:book/1:author>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Ralls, Kim" .

<http://www.example.org/document/2:book/2:title>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Midnight Rain" .

<http://www.example.org/document/1:book/4:price>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "44.95" .

<http://www.example.org/document/1:book/2:title>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "XML Developer's Guide" .

<http://www.example.org/document/3:book/6:description>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "After the collapse of a nanotechnology\n            society in England, the young survivors lay the\n            foundation for a new society." .

<http://www.example.org/document/2:book>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://www.example.org/document/2:book/1:author> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://www.example.org/document/2:book/2:title> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                <http://www.example.org/document/2:book/3:genre> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                <http://www.example.org/document/2:book/4:price> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                <http://www.example.org/document/2:book/5:publish_date> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6>
                <http://www.example.org/document/2:book/6:description> ;
        <http://sparql.xyz/facade-x/data/isbn>
                "928-3-16-148410-0" .

<http://www.example.org/document/3:book>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://www.example.org/document/3:book/1:author> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://www.example.org/document/3:book/2:title> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                <http://www.example.org/document/3:book/3:genre> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                <http://www.example.org/document/3:book/4:price> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                <http://www.example.org/document/3:book/5:publish_date> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6>
                <http://www.example.org/document/3:book/6:description> ;
        <http://sparql.xyz/facade-x/data/isbn>
                "432-3-16-143110-1" .

```