[![DOI](https://zenodo.org/badge/303967701.svg)](https://zenodo.org/badge/latestdoi/303967701)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![How to](https://img.shields.io/badge/How%20to-cite-green.svg)](#how-to-cite-our-work)
[![Java 11](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java11.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java11.yml)
[![Java 14](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java14.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java14.yml)
[![Java 14](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java17.yml/badge.svg?branch=v0.6-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/maven_Java17.yml)

# SPARQL Anything
SPARQL Anything is a system for Semantic Web re-engineering that allows users to ... query anything with SPARQL.

Main features:

- Query files in plain SPARQL 1.1, via the `SERVICE <x-sparql-anything:>` (see [configuration](#configuration)) and build knowledge graphs with `CONSTRUCT` queries
- [Supported formats](#supported-formats): XML, JSON, CSV, HTML, Excel, Text, Binary, EXIF, File System, Zip/Tar, Markdown, YAML, Bibtex, DOCx (see [configuration](#format-specific-options))
- Transforms [files, inline content, or the output of an external command](#general-purpose-options)
- Full fledged [HTTP client](#http-options) to query Web APIs (headers, authentication, all methods supported)
- [Functions library](#functions-and-magic-properties) for RDF sequences, strings, hashes, easy entity building, ...
- Combine multiple SERVICE clauses into complex data integration queries (thanks to SPARQL)
- Query templates (using [BASIL variables](#query-templates-and-variable-bindings))
- Save and reuse SPARQL `Results Sets` as input for [parametric queries](#query-templates-and-variable-bindings)
- Slice large CSV files with an iterator-like execution style (soon [JSON](https://github.com/SPARQL-Anything/sparql.anything/issues/202) and [XML](https://github.com/SPARQL-Anything/sparql.anything/issues/203))
- Supports an [on-disk option](#general-purpose-options) (with Apache Jena TDB2)

## Concept
SPARQL Anything uses a single generic abstraction for all data source formats called Facade-X.
### Facade-X
Facade-X is a simplistic meta-model used by SPARQL Anything transformers to generate RDF data from diverse data sources.
Intuitively, Facade-X uses a subset of RDF as a general approach to represent the source content *as-it-is* but in RDF.
The model combines two types of elements: containers and literals.
Facade-X always has  a single root container.
Container members are a combination of key-value pairs, where keys are either RDF properties or container membership properties.
Instead, values can be either RDF literals or other containers.
This is a generic example of a Facade-X data object (more examples below):

```turtle
@prefix fx: <http://sparql.xyz/facade-x/ns/> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
[] a fx:root ; rdf:_1 [
    xyz:someKey "some value" ;
    rdf:_1 "another value with unspecified key" ;
    rdf:_2 [
        rdf:type xyz:MyType ;
        rdf:_1 "another value"
    ]
] .
```

### Querying anything
SPARQL Anything extends the Apache Jena ARQ processors by *overloading* the SERVICE operator, as in the following example:

Suppose having this JSON file as input (also available at ``https://sparql-anything.cc/example1.json``)

```json
[
  {
    "name":"Friends",
    "genres":[
      "Comedy",
      "Romance"
    ],
    "language":"English",
    "status":"Ended",
    "premiered":"1994-09-22",
    "summary":"Follows the personal and professional lives of six twenty to thirty-something-year-old friends living in Manhattan.",
    "stars":[
      "Jennifer Aniston",
      "Courteney Cox",
      "Lisa Kudrow",
      "Matt LeBlanc",
      "Matthew Perry",
      "David Schwimmer"
    ]
  },
  {
    "name":"Cougar Town",
    "genres":[
      "Comedy",
      "Romance"
    ],
    "language":"English",
    "status":"Ended",
    "premiered":"2009-09-23",
    "summary":"Jules is a recently divorced mother who has to face the unkind realities of dating in a world obsessed with beauty and youth. As she becomes older, she starts discovering herself.",
    "stars":[
      "Courteney Cox",
      "David Arquette",
      "Bill Lawrence",
      "Linda Videtti Figueiredo",
      "Blake McCormick"
    ]
  }
]
```

With SPARQL Anything you can select the TV series starring "Courteney Cox" with the SPARQL query

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

and get this result without caring of transforming JSON to RDF.

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |



## Supported Formats
Currently, SPARQL Anything supports the following list of formats but the possibilities are limitless!
The data is interpreted as in the following examples (using default settings).

<details><summary>JSON</summary>
Data:

```json
{
    "stringArg": "stringValue",
    "intArg": 1,
    "booleanArg": true,
    "nullArg": null,
    "arr": [ 0, 1 ]
}
```

Facade-X RDF:

```turtle
@prefix xyz:    &lt;http://sparql.xyz/facade-x/data/&gt; .
@prefix fx:   &lt;http://sparql.xyz/facade-x/ns/&gt; .
@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .
[ a fx:root ; 
    xyz:arr [
        rdf:_1  "0"^^xsd:int ;
        rdf:_2  "1"^^xsd:int ] ;
    xyz:booleanArg  true ;
    xyz:intArg "1"^^xsd:int ;
    xyz:stringArg "stringValue"
] .
```
</details>



<details><summary>HTML</summary>
Data:

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

Facade-X RDF:

```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xhtml: <http://www.w3.org/1999/xhtml#> .

[ a       xhtml:html, fx:root ;
  rdf:_1  [ a       xhtml:head ;
            rdf:_1  [ a       xhtml:title ;
                      rdf:_1  "Hello world!"
                    ]
          ] ;
  rdf:_2  [ a       xhtml:body ;
            rdf:_1  [ a            xhtml:p ;
                      rdf:_1       "Hello world" ;
                      xhtml:class  "paragraph"
                    ]
          ]
] .
```

</details>

<details><summary>XML</summary>
Data:

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

Facade-X RDF:

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

</details>

<details><summary>CSV</summary>
Data:

```csv
laura@example.com,2070,Laura,Grey
craig@example.com,4081,Craig,Johnson
mary@example.com,9346,Mary,Jenkins
jamie@example.com,5079,Jamie,Smith
```

Facade-X RDF:

```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

[ a fx:root ;
  rdf:_1  [ rdf:_1  "laura@example.com" ;
            rdf:_2  "2070" ;
            rdf:_3  "Laura" ;
            rdf:_4  "Grey"
          ] ;
  rdf:_2  [ rdf:_1  "craig@example.com" ;
            rdf:_2  "4081" ;
            rdf:_3  "Craig" ;
            rdf:_4  "Johnson"
          ] ;
  rdf:_3  [ rdf:_1  "mary@example.com" ;
            rdf:_2  "9346" ;
            rdf:_3  "Mary" ;
            rdf:_4  "Jenkins"
          ] ;
  rdf:_4  [ rdf:_1  "jamie@example.com" ;
            rdf:_2  "5079" ;
            rdf:_3  "Jamie" ;
            rdf:_4  "Smith"
          ]
] .

```

</details>

<details><summary>BIN, PNG, JPEG, JPG, BMP, TIFF, TIF, ICO </summary>

Data:

[Image here](ttps://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg)

<img src="https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg"/>

Facade-X RDF:

```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
[]  a fx:root ; 
    rdf:_1  "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"^^<http://www.w3.org/2001/XMLSchema#base64Binary> 
    .
```

</details>

<details><summary>TXT</summary>
Data:

```txt
Hello World!
```

Facade-X RDF:

```turtle
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
[] a fx:root ; 
    rdf:_1 "Hello World!" .
```

</details>

<details><summary>Archive: ZIP, Tar, File System folder</summary>
Data:

```bash
archive.tar
\|__ file.csv
\|__ file.json
\|__ file.xml
```

Facade-X RDF:

```turtle
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix fx: <http://sparql.xyz/facade-x/ns/>
[ rdf:type fx:root 
 rdf:_1 "file.csv" ; 
 rdf:_2 "file.json" ;
 rdf:_3 "file.xml" ] .
```

</details>

<details><summary>Spreadsheets: XLS, XLSx</summary>
Data:

Link to file: [Book1.xslx](https://github.com/SPARQL-Anything/sparql.anything/raw/v0.6-DEV/sparql.anything.spreadsheet/src/main/resources/testResources/Book1.xlsx) 

Facade-X RDF:
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix fx: <http://sparql.xyz/facade-x/ns/>
```turtle

<https://github.com/SPARQL-Anything/sparql.anything/raw/v0.6-DEV/sparql.anything.spreadsheet/src/main/resources/testResources/Book1.xlsx#Sheet1> {
_:b0    rdf:_1 "A2" ;
        rdf:_2 "B2" ;
        rdf:_3 "C2" .

_:b1    a       fx:root ;
        rdf:_1 _:b2 ;
        rdf:_2 _:b3 ;
        rdf:_3 _:b0 .

_:b4    rdf:_1 "A12" ;
        rdf:_2 "B12" ;
        rdf:_3 "C12" .

_:b3    rdf:_1 "A1" ;
        rdf:_2 "B1" ;
        rdf:_3 "C1" .

_:b2    rdf:_1 "A" ;
        rdf:_2 "B" ;
        rdf:_3 "C" .

_:b5    rdf:_1 "A1" ;
        rdf:_2 "B1" ;
        rdf:_3 "C1" .

_:b6    rdf:_1 "A11" ;
        rdf:_2 "B11" ;
        rdf:_3 "C11" .

_:b7    a       fx:root ;
        rdf:_1 _:b5 ;
        rdf:_2 _:b6 ;
        rdf:_3 _:b4 .
}

<https://github.com/SPARQL-Anything/sparql.anything/raw/v0.6-DEV/sparql.anything.spreadsheet/src/main/resources/testResources/Book1.xlsx#Sheet2> {
    _:b7    a       fx:root ;
            rdf:_1 _:b5 ;
            rdf:_2 _:b6 ;
            rdf:_3 _:b4 .
    
    _:b4    rdf:_1 "A12" ;
            rdf:_2 "B12" ;
            rdf:_3 "C12" .
    
    _:b5    rdf:_1 "A1" ;
            rdf:_2 "B1" ;
            rdf:_3 "C1" .
    
    _:b6    rdf:_1 "A11" ;
            rdf:_2 "B11" ;
            rdf:_3 "C11" .
}

<https://github.com/SPARQL-Anything/sparql.anything/raw/v0.6-DEV/sparql.anything.spreadsheet/src/main/resources/testResources/Book1.xlsx#Sheet1> {
    _:b0    rdf:_1 "A2" ;
            rdf:_2 "B2" ;
            rdf:_3 "C2" .
    
    _:b1    a       fx:root ;
            rdf:_1 _:b2 ;
            rdf:_2 _:b3 ;
            rdf:_3 _:b0 .
    
    _:b2    rdf:_1 "A" ;
            rdf:_2 "B" ;
            rdf:_3 "C" .
    
    _:b3    rdf:_1 "A1" ;
            rdf:_2 "B1" ;
            rdf:_3 "C1" .
}
```

</details>

<details><summary>Documents: DOCx</summary>
Data:
[Input File](https://github.com/SPARQL-Anything/sparql.anything/raw/v0.5-DEV/sparql-anything-docs/src/test/resources/Doc1.docx)
Facade-X RDF:

```
<http://www.example.org/document>
        a       <http://sparql.xyz/facade-x/data/Document> , <http://sparql.xyz/facade-x/ns/root> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://sparql.xyz/facade-x/data/Heading1/1> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://sparql.xyz/facade-x/data/paragraph/2> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                <http://sparql.xyz/facade-x/data/paragraph/3> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                <http://sparql.xyz/facade-x/data/Heading2/4> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                <http://sparql.xyz/facade-x/data/paragraph/5> .

<http://sparql.xyz/facade-x/data/Heading2/4>
        a       <http://sparql.xyz/facade-x/data/Heading2> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Title 2" .

<http://sparql.xyz/facade-x/data/Heading1/1>
        a       <http://sparql.xyz/facade-x/data/Heading1> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Title 1" .

<http://sparql.xyz/facade-x/data/paragraph/2>
        a       <http://sparql.xyz/facade-x/data/Paragraph> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Paragraph1" .

<http://sparql.xyz/facade-x/data/paragraph/5>
        a       <http://sparql.xyz/facade-x/data/Paragraph> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Paragraph3" .

<http://sparql.xyz/facade-x/data/paragraph/3>
        a       <http://sparql.xyz/facade-x/data/Paragraph> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Paragraph2" .
```

</details>

<details><summary>Image Metadata</summary>
Data:
![Image example](https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg)

Facade-X RDF:

```
  <https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg>
        <http://sparql.xyz/facade-x/data/Aperture Value>
                "f/7.0" ;
        <http://sparql.xyz/facade-x/data/Blue Colorant>
                "(0.1431, 0.0606, 0.7141)" ;
        <http://sparql.xyz/facade-x/data/Blue TRC>      "0.0, 0.0000763, 0.0001526, 0.0002289, 0.0003052, 0.0003815, 0.0004578, 0.0005341, 0.0006104, 0.0006867, 0.000763, 0.0008392, 0.0009003, 0.0009766, 0.0010529, 0.0011292, 0.0012055, 0.0012818, 0.0013581, 0.0014343, 0.0015106, 0.0015869, 0.0016632, 0.0017395, 0.0018158, 0.0018921, 0.0019684, 0.0020447, 0.002121, 0.0021973, 0.0022736, 0.0023499, 0.0024262, 0.0025025, 0.0025788, 0.0026551, 0.0027161, 0.0027924, 0.0028687, 0.002945, 0.0030213, 0.0030976, 0.0031739, 0.0032502, 0.0033417, 0.003418, 0.0034943, 0.0035859, 0.0036622, 0.0037537, 0.00383, 0.0039216, 0.0040131, 0.0041047, 0.0041962, 0.0042878, 0.0043793, 0.0044709, 0.0045624, 0.0046693, 0.0047608, 0.0048524, 0.0049592, 0.005066, 0.0051575, 0.0052644, 0.0053712, 0.005478, 0.0055848, 0.0056916, 0.0057984, 0.0059052, 0.0060273, 0.0061341, 0.0062562, 0.006363, 0.0064851, 0.0066072, 0.0067292, 0.0068513, 0.0069734, 0.0070954, 0.0072175, 0.0073396, 0.0074617, 0.007599, 0.0077211, 0.0078584, 0.0079957, 0.0081178, 0.0082551, 0.0083925, 0.0085298, 0.0086671, 0.0088045, 0.008957, 0.0090944, 0.0092317, 0.0093843, 0.0095369, 0.0096742, 0.0098268, 0.0099794, 0.010132, 0.0102846, 0.0104372, 0.0105898, 0.0107576, 0.0109102, 0.0110628, 0.0112306, 0.0113985, 0.0115511, 0.0117189, 0.0118868, 0.0120546, 0.0122225, 0.0124056, 0.0125734, 0.0127413, 0.0129244, 0.0130922, 0.0132753, 0.0134585, 0.0136416, 0.0138247, 0.0140078, 0.0141909, 0.014374, 0.0145571, 0.0147555, 0.0149386, 0.0151369, 0.0153201, 0.0155184, 0.0157168, 0.0159152, 0.0161135, 0.0163119, 0.0165255, 0.0167239, 0.0169223, 0.0171359, 0.0173495, 0.0175479, 0.0177615, 0.0179751, 0.0181888, 0.0184024, 0.018616, 0.0188449, 0.0190585, 0.0192874, 0.019501, 0.0197299, 0.0199588, 0.0201877, 0.0204166, 0.0206455, 0.0208743, 0.0211032, 0.0213474, 0.0215763, 0.0218204, 0.0220645, 0.0222934, 0.0225376, 0.0227817, 0.0230259, 0.0232853, 0.0235294, 0.0237736, 0.024033, 0.0242771, 0.0245365, 0.0247959, 0.0250553, 0.0253147, 0.0255741, 0.0258335, 0.0261082, 0.0263676, 0.026627, 0.0269017, 0.0271763, 0.027451, 0.0277256, 0.0280003, 0.028275, 0.0285496, 0.0288243, 0.0291142, 0.0293889, 0.0296788, 0.0299687, 0.0302586, 0.0305486, 0.0308385, 0.0311284, 0.0314183, 0.0317235, 0.0320134, 0.0323186, 0.0326238, 0.032929, 0.0332341, 0.0335393, 0.0338445, 0.0341497, 0.0344549, 0.0347753, 0.0350805, 0.0354009, 0.0357214, 0.0360418, 0.0363622, 0.0366827, 0.0370031, 0.0373388, 0.0376593, 0.037995, 0.0383154, 0.0386511, 0.0389868, 0.0393225, 0.0396582, 0.0399939, 0.0403449, 0.0406806, 0.0410315, 0.0413825, 0.0417182, 0.0420691, 0.0424201, 0.042771, 0.0431373, 0.0434882, 0.0438392, 0.0442054, 0.0445716, 0.0449226, 0.0452888, 0.045655, 0.0460212, 0.0464027, 0.0467689, 0.0471504, 0.0475166, 0.0478981, 0.0482795, 0.048661, 0.0490425, 0.049424, 0.0498054, 0.0501869, 0.0505837, 0.0509804, 0.0513619, 0.0517586, 0.0521553, 0.0525521, 0.0529488, 0.0533608, 0.0537575, 0.0541695, 0.0545663, 0.0549783, 0.0553902, 0.0558022, 0.0562142, 0.0566262, 0.0570535, 0.0574655, 0.0578927, 0.05832, 0.058732, 0.0591592, 0.0595865, 0.060029, 0.0604562, 0.0608835, 0.061326, 0.0617533, 0.0621958, 0.0626383, 0.0630808, 0.0635233, 0.0639811, 0.0644236, 0.0648661, 0.0653239, 0.0657816, 0.0662394, 0.0666972, 0.067155, 0.0676127, 0.0680705, 0.0685435, 0.0690013, 0.0694743, 0.0699474, 0.0704204, 0.0708934, 0.0713664, 0.0718395, 0.0723278, 0.0728008, 0.0732891, 0.0737774, 0.0742657, 0.0747539, 0.0752422, 0.0757305, 0.0762188, 0.0767224, 0.0772259, 0.0777142, 0.0782177, 0.0787213, 0.0792401, 0.0797436, 0.0802472, 0.080766, 0.0812696, 0.0817884, 0.0823072, 0.082826, 0.0833448, 0.0838636, 0.0843977, 0.0849165, 0.0854505, 0.0859846, 0.0865187, 0.0870527, 0.0875868, 0.0881209, 0.0886549, 0.0892042, 0.0897536, 0.0902876, 0.090837, 0.0913863, 0.0919356, 0.0925002, 0.0930495, 0.0936141, 0.0941634, 0.094728, 0.0952926, 0.0958572, 0.0964218, 0.0970016, 0.0975662, 0.098146, 0.0987106, 0.0992905, 0.0998703, 0.1004501, 0.10103, 0.1016251, 0.1022049, 0.1028, 0.1033799, 0.103975, 0.1045701, 0.1051652, 0.1057755, 0.1063706, 0.106981, 0.1075761, 0.1081865, 0.1087968, 0.1094072, 0.1100175, 0.1106279, 0.1112535, 0.1118639, 0.1124895, 0.1131151, 0.1137407, 0.1143664, 0.114992, 0.1156176, 0.1162585, 0.1168841, 0.117525, 0.1181659, 0.1188067, 0.1194476, 0.1200885, 0.1207446, 0.1213855, 0.1220417, 0.1226978, 0.1233539, 0.1240101, 0.1246662, 0.1253223, 0.1259937, 0.1266499, 0.1273213, 0.1279927, 0.1286641, 0.1293355, 0.1300069, 0.1306935, 0.1313649, 0.1320516, 0.1327382, 0.1334096, 0.1341115, 0.1347982, 0.1354849, 0.1361868, 0.1368734, 0.1375753, 0.1382773, 0.1389792, 0.1396811, 0.140383, 0.1411002, 0.1418021, 0.1425193, 0.1432364, 0.1439536, 0.1446708, 0.145388, 0.1461204, 0.1468376, 0.14757, 0.1483024, 0.1490349, 0.1497673, 0.1504997, 0.1512322, 0.1519799, 0.1527123, 0.15346, 0.1542077, 0.1549554, 0.1557031, 0.1564508, 0.1572137, 0.1579767, 0.1587243, 0.1594873, 0.1602502, 0.1610132, 0.1617914, 0.1625544, 0.1633326, 0.1640955, 0.1648737, 0.1656519, 0.1664302, 0.1672236, 0.1680018, 0.1687953, 0.1695735, 0.170367, 0.1711604, 0.1719539, 0.1727474, 0.1735561, 0.1743496, 0.1751583, 0.175967, 0.1767758, 0.1775845, 0.1783932, 0.1792172, 0.1800259, 0.1808499, 0.1816739, 0.1824826, 0.1833219, 0.1841459, 0.1849699, 0.1858091, 0.1866331, 0.1874723, 0.1883116, 0.1891508, 0.1900053, 0.1908446, 0.1916838, 0.1925383, 0.1933928, 0.1942473, 0.1951019, 0.1959564, 0.1968261, 0.1976806, 0.1985504, 0.1994202, 0.2002899, 0.2011597, 0.2020294, 0.2028992, 0.2037842, 0.2046693, 0.205539, 0.206424, 0.2073243, 0.2082094, 0.2090944, 0.2099947, 0.2108949, 0.21178, 0.2126802, 0.2135958, 0.2144961, 0.2153964, 0.2163119, 0.2172274, 0.2181277, 0.2190585, 0.2199741, 0.2208896, 0.2218051, 0.2227359, 0.2236667, 0.2245975, 0.2255283, 0.2264591, 0.2273899, 0.228336, 0.2292821, 0.2302129, 0.2311589, 0.232105, 0.2330663, 0.2340124, 0.2349737, 0.2359197, 0.2368811, 0.2378424, 0.2388037, 0.239765, 0.2407416, 0.2417029, 0.2426795, 0.2436561, 0.2446326, 0.2456092, 0.2466011, 0.2475776, 0.2485695, 0.249546, 0.2505379, 0.2515297, 0.2525368, 0.2535286, 0.2545357, 0.2555276, 0.2565347, 0.2575418, 0.2585489, 0.259556, 0.2605783, 0.2615854, 0.2626078, 0.2636301, 0.2646525, 0.2656748, 0.2667124, 0.2677348, 0.2687724, 0.26981, 0.2708324, 0.2718853, 0.2729229, 0.2739605, 0.2750134, 0.276051, 0.2771038, 0.2781567, 0.2792248, 0.2802777, 0.2813306, 0.2823987, 0.2834668, 0.284535, 0.2856031, 0.2866712, 0.2877394, 0.2888228, 0.2899062, 0.2909743, 0.2920577, 0.2931563, 0.2942397, 0.2953231, 0.2964218, 0.2975204, 0.2986191, 0.2997177, 0.3008164, 0.301915, 0.3030289, 0.3041428, 0.3052567, 0.3063706, 0.3074846, 0.3085985, 0.3097124, 0.3108415, 0.3119707, 0.3130999, 0.314229, 0.3153582, 0.3165026, 0.3176318, 0.3187762, 0.3199207, 0.3210651, 0.3222095, 0.3233539, 0.3245136, 0.3256733, 0.3268177, 0.3279774, 0.3291371, 0.330312, 0.3314717, 0.3326467, 0.3338216, 0.3349966, 0.3361715, 0.3373465, 0.3385214, 0.3397116, 0.3408865, 0.3420768, 0.343267, 0.3444724, 0.3456626, 0.3468528, 0.3480583, 0.3492638, 0.3504692, 0.3516747, 0.3528801, 0.3541009, 0.3553063, 0.356527, 0.3577478, 0.3589685, 0.3601892, 0.3614252, 0.3626459, 0.3638819, 0.3651179, 0.3663539, 0.3675898, 0.3688411, 0.3700771, 0.3713283, 0.3725795, 0.3738308, 0.375082, 0.3763333, 0.3775998, 0.378851, 0.3801175, 0.381384, 0.3826505, 0.3839322, 0.3851987, 0.3864805, 0.387747, 0.3890288, 0.3903105, 0.3916075, 0.3928893, 0.3941863, 0.3954681, 0.3967651, 0.3980621, 0.3993744, 0.4006714, 0.4019837, 0.4032807, 0.404593, 0.4059052, 0.4072175, 0.4085451, 0.4098573, 0.4111849, 0.4125124, 0.4138399, 0.4151675, 0.416495, 0.4178378, 0.4191806, 0.4205234, 0.4218662, 0.423209, 0.4245518, 0.4259098, 0.4272526, 0.4286107, 0.4299687, 0.4313268, 0.4326848, 0.4340581, 0.4354314, 0.4367895, 0.4381628, 0.4395514, 0.4409247, 0.442298, 0.4436866, 0.4450752, 0.4464637, 0.4478523, 0.4492409, 0.4506447, 0.4520333, 0.4534371, 0.4548409, 0.4562448, 0.4576486, 0.4590677, 0.4604715, 0.4618906, 0.4633097, 0.4647288, 0.4661631, 0.4675822, 0.4690166, 0.4704356, 0.47187, 0.4733043, 0.4747539, 0.4761883, 0.4776379, 0.4790875, 0.4805371, 0.4819867, 0.4834363, 0.4848859, 0.4863508, 0.4878157, 0.4892805, 0.4907454, 0.4922103, 0.4936904, 0.4951553, 0.4966354, 0.4981155, 0.4995956, 0.501091, 0.5025711, 0.5040665, 0.5055467, 0.507042, 0.5085527, 0.5100481, 0.5115435, 0.5130541, 0.5145647, 0.5160754, 0.517586, 0.5190967, 0.5206226, 0.5221485, 0.5236591, 0.525185, 0.5267262, 0.5282521, 0.529778, 0.5313191, 0.5328603, 0.5344015, 0.5359426, 0.537499, 0.5390402, 0.5405966, 0.542153, 0.5437095, 0.5452659, 0.5468223, 0.548394, 0.5499657, 0.5515373, 0.553109, 0.5546807, 0.5562524, 0.5578393, 0.5594263, 0.5610132, 0.5626001, 0.5641871, 0.565774, 0.5673762, 0.5689784, 0.5705806, 0.5721828, 0.573785, 0.5754025, 0.5770047, 0.5786221, 0.5802396, 0.581857, 0.5834897, 0.5851072, 0.5867399, 0.5883726, 0.5900053, 0.5916381, 0.5932708, 0.5949187, 0.5965667, 0.5982147, 0.5998627, 0.6015106, 0.6031586, 0.6048219, 0.6064851, 0.6081483, 0.6098116, 0.6114748, 0.6131533, 0.6148165, 0.616495, 0.6181735, 0.619852, 0.6215457, 0.6232242, 0.624918, 0.6266117, 0.6283055, 0.6299992, 0.631693, 0.633402, 0.635111, 0.63682, 0.638529, 0.640238, 0.6419471, 0.6436713, 0.6453956, 0.6471199, 0.6488441, 0.6505684, 0.6523079, 0.6540322, 0.6557717, 0.6575113, 0.6592508, 0.6610056, 0.6627451, 0.6644999, 0.6662547, 0.6680095, 0.6697642, 0.6715343, 0.6732891, 0.6750591, 0.6768292, 0.6785992, 0.6803845, 0.6821546, 0.6839399, 0.6857252, 0.6875105, 0.6892958, 0.6910811, 0.6928817, 0.6946822, 0.6964675, 0.6982834, 0.7000839, 0.7018845, 0.7037003, 0.7055161, 0.707332, 0.7091478, 0.7109636, 0.7127947, 0.7146105, 0.7164416, 0.7182727, 0.720119, 0.7219501, 0.7237964, 0.7256275, 0.7274739, 0.7293355, 0.7311818, 0.7330282, 0.7348898, 0.7367514, 0.738613, 0.7404746, 0.7423514, 0.744213, 0.7460899, 0.7479667, 0.7498436, 0.7517205, 0.7536126, 0.7554894, 0.7573816, 0.7592737, 0.7611658, 0.7630732, 0.7649653, 0.7668727, 0.76878, 0.7706874, 0.7725948, 0.7745174, 0.7764248, 0.7783474, 0.7802701, 0.7821927, 0.7841306, 0.7860533, 0.7879911, 0.789929, 0.7918669, 0.7938048, 0.795758, 0.7976959, 0.799649, 0.8016022, 0.8035554, 0.8055238, 0.8074769, 0.8094453, 0.8114137, 0.8133822, 0.8153506, 0.8173342, 0.8193179, 0.8212863, 0.82327, 0.8252689, 0.8272526, 0.8292515, 0.8312352, 0.8332341, 0.8352331, 0.8372473, 0.8392462, 0.8412604, 0.8432746, 0.8452888, 0.847303, 0.8493172, 0.8513466, 0.8533761, 0.8554055, 0.857435, 0.8594644, 0.8614939, 0.8635386, 0.8655833, 0.867628, 0.8696727, 0.8717327, 0.8737774, 0.8758373, 0.8778973, 0.8799573, 0.8820325, 0.8840925, 0.8861677, 0.8882429, 0.8903182, 0.8923934, 0.8944839, 0.8965591, 0.8986496, 0.9007401, 0.9028305, 0.9049363, 0.9070268, 0.9091325, 0.9112383, 0.913344, 0.915465, 0.9175708, 0.9196918, 0.9218128, 0.9239338, 0.9260548, 0.9281758, 0.930312, 0.9324483, 0.9345846, 0.9367208, 0.9388571, 0.9410086, 0.9431601, 0.9453117, 0.9474632, 0.9496147, 0.9517815, 0.953933, 0.9560998, 0.9582666, 0.9604334, 0.9626154, 0.9647822, 0.9669642, 0.9691463, 0.9713283, 0.9735256, 0.9757076, 0.9779049, 0.9801022, 0.9822995, 0.9844968, 0.9867094, 0.988922, 0.9911345, 0.9933471, 0.9955596, 0.9977722, 1.0" ;
        <http://sparql.xyz/facade-x/data/CMM Type>      "Lino" ;
        <http://sparql.xyz/facade-x/data/Class>         "Display Device" ;
        <http://sparql.xyz/facade-x/data/Color Space>   "sRGB" ;
        <http://sparql.xyz/facade-x/data/Color space>   "RGB " ;
        <http://sparql.xyz/facade-x/data/Component 1>   "Y component: Quantization table 0, Sampling factors 1 horiz/1 vert" ;
        <http://sparql.xyz/facade-x/data/Component 2>   "Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert" ;
        <http://sparql.xyz/facade-x/data/Component 3>   "Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert" ;
        <http://sparql.xyz/facade-x/data/Components Configuration>
                "YCbCr" ;
        <http://sparql.xyz/facade-x/data/Compression>   "JPEG (old-style)" ;
        <http://sparql.xyz/facade-x/data/Compression Type>
                "Baseline" ;
        <http://sparql.xyz/facade-x/data/Custom Rendered>
                "Normal process" ;
        <http://sparql.xyz/facade-x/data/Data Precision>
                "8 bits" ;
        <http://sparql.xyz/facade-x/data/Date/Time>     "2008:07:31 10:38:11" ;
        <http://sparql.xyz/facade-x/data/Date/Time Digitized>
                "2008:05:30 15:56:01" ;
        <http://sparql.xyz/facade-x/data/Date/Time Original>
                "2008:05:30 15:56:01" ;
        <http://sparql.xyz/facade-x/data/Detected File Type Long Name>
                "Joint Photographic Experts Group" ;
        <http://sparql.xyz/facade-x/data/Detected File Type Name>
                "JPEG" ;
        <http://sparql.xyz/facade-x/data/Detected MIME Type>
                "image/jpeg" ;
        <http://sparql.xyz/facade-x/data/Device Mfg Description>
                "IEC http://www.iec.ch" ;
        <http://sparql.xyz/facade-x/data/Device Model Description>
                "IEC 61966-2.1 Default RGB colour space - sRGB" ;
        <http://sparql.xyz/facade-x/data/Device manufacturer>
                "IEC " ;
        <http://sparql.xyz/facade-x/data/Device model>  "sRGB" ;
        <http://sparql.xyz/facade-x/data/Exif Image Height>
                "68 pixels" ;
        <http://sparql.xyz/facade-x/data/Exif Image Width>
                "100 pixels" ;
        <http://sparql.xyz/facade-x/data/Exif Version>  "2.21" ;
        <http://sparql.xyz/facade-x/data/Expected File Name Extension>
                "jpg" ;
        <http://sparql.xyz/facade-x/data/Exposure Bias Value>
                "0 EV" ;
        <http://sparql.xyz/facade-x/data/Exposure Mode>
                "Manual exposure" ;
        <http://sparql.xyz/facade-x/data/Exposure Program>
                "Manual control" ;
        <http://sparql.xyz/facade-x/data/Exposure Time>
                "1/160 sec" ;
        <http://sparql.xyz/facade-x/data/F-Number>      "f/7.1" ;
        <http://sparql.xyz/facade-x/data/File Modified Date>
                "Sun Dec 20 12:40:39 +01:00 2020" ;
        <http://sparql.xyz/facade-x/data/File Name>     "Canon_40D.jpg" ;
        <http://sparql.xyz/facade-x/data/File Size>     "7958 bytes" ;
        <http://sparql.xyz/facade-x/data/Flash>         "Flash fired" ;
        <http://sparql.xyz/facade-x/data/FlashPix Version>
                "1.00" ;
        <http://sparql.xyz/facade-x/data/Focal Length>  "135 mm" ;
        <http://sparql.xyz/facade-x/data/Focal Plane Resolution Unit>
                "Inches" ;
        <http://sparql.xyz/facade-x/data/Focal Plane X Resolution>
                "73/324000 inches" ;
        <http://sparql.xyz/facade-x/data/Focal Plane Y Resolution>
                "583/2592000 inches" ;
        <http://sparql.xyz/facade-x/data/GPS Version ID>
                "2.200" ;
        <http://sparql.xyz/facade-x/data/Green Colorant>
                "(0.3851, 0.7169, 0.0971)" ;
        <http://sparql.xyz/facade-x/data/Green TRC>     "0.0, 0.0000763, 0.0001526, 0.0002289, 0.0003052, 0.0003815, 0.0004578, 0.0005341, 0.0006104, 0.0006867, 0.000763, 0.0008392, 0.0009003, 0.0009766, 0.0010529, 0.0011292, 0.0012055, 0.0012818, 0.0013581, 0.0014343, 0.0015106, 0.0015869, 0.0016632, 0.0017395, 0.0018158, 0.0018921, 0.0019684, 0.0020447, 0.002121, 0.0021973, 0.0022736, 0.0023499, 0.0024262, 0.0025025, 0.0025788, 0.0026551, 0.0027161, 0.0027924, 0.0028687, 0.002945, 0.0030213, 0.0030976, 0.0031739, 0.0032502, 0.0033417, 0.003418, 0.0034943, 0.0035859, 0.0036622, 0.0037537, 0.00383, 0.0039216, 0.0040131, 0.0041047, 0.0041962, 0.0042878, 0.0043793, 0.0044709, 0.0045624, 0.0046693, 0.0047608, 0.0048524, 0.0049592, 0.005066, 0.0051575, 0.0052644, 0.0053712, 0.005478, 0.0055848, 0.0056916, 0.0057984, 0.0059052, 0.0060273, 0.0061341, 0.0062562, 0.006363, 0.0064851, 0.0066072, 0.0067292, 0.0068513, 0.0069734, 0.0070954, 0.0072175, 0.0073396, 0.0074617, 0.007599, 0.0077211, 0.0078584, 0.0079957, 0.0081178, 0.0082551, 0.0083925, 0.0085298, 0.0086671, 0.0088045, 0.008957, 0.0090944, 0.0092317, 0.0093843, 0.0095369, 0.0096742, 0.0098268, 0.0099794, 0.010132, 0.0102846, 0.0104372, 0.0105898, 0.0107576, 0.0109102, 0.0110628, 0.0112306, 0.0113985, 0.0115511, 0.0117189, 0.0118868, 0.0120546, 0.0122225, 0.0124056, 0.0125734, 0.0127413, 0.0129244, 0.0130922, 0.0132753, 0.0134585, 0.0136416, 0.0138247, 0.0140078, 0.0141909, 0.014374, 0.0145571, 0.0147555, 0.0149386, 0.0151369, 0.0153201, 0.0155184, 0.0157168, 0.0159152, 0.0161135, 0.0163119, 0.0165255, 0.0167239, 0.0169223, 0.0171359, 0.0173495, 0.0175479, 0.0177615, 0.0179751, 0.0181888, 0.0184024, 0.018616, 0.0188449, 0.0190585, 0.0192874, 0.019501, 0.0197299, 0.0199588, 0.0201877, 0.0204166, 0.0206455, 0.0208743, 0.0211032, 0.0213474, 0.0215763, 0.0218204, 0.0220645, 0.0222934, 0.0225376, 0.0227817, 0.0230259, 0.0232853, 0.0235294, 0.0237736, 0.024033, 0.0242771, 0.0245365, 0.0247959, 0.0250553, 0.0253147, 0.0255741, 0.0258335, 0.0261082, 0.0263676, 0.026627, 0.0269017, 0.0271763, 0.027451, 0.0277256, 0.0280003, 0.028275, 0.0285496, 0.0288243, 0.0291142, 0.0293889, 0.0296788, 0.0299687, 0.0302586, 0.0305486, 0.0308385, 0.0311284, 0.0314183, 0.0317235, 0.0320134, 0.0323186, 0.0326238, 0.032929, 0.0332341, 0.0335393, 0.0338445, 0.0341497, 0.0344549, 0.0347753, 0.0350805, 0.0354009, 0.0357214, 0.0360418, 0.0363622, 0.0366827, 0.0370031, 0.0373388, 0.0376593, 0.037995, 0.0383154, 0.0386511, 0.0389868, 0.0393225, 0.0396582, 0.0399939, 0.0403449, 0.0406806, 0.0410315, 0.0413825, 0.0417182, 0.0420691, 0.0424201, 0.042771, 0.0431373, 0.0434882, 0.0438392, 0.0442054, 0.0445716, 0.0449226, 0.0452888, 0.045655, 0.0460212, 0.0464027, 0.0467689, 0.0471504, 0.0475166, 0.0478981, 0.0482795, 0.048661, 0.0490425, 0.049424, 0.0498054, 0.0501869, 0.0505837, 0.0509804, 0.0513619, 0.0517586, 0.0521553, 0.0525521, 0.0529488, 0.0533608, 0.0537575, 0.0541695, 0.0545663, 0.0549783, 0.0553902, 0.0558022, 0.0562142, 0.0566262, 0.0570535, 0.0574655, 0.0578927, 0.05832, 0.058732, 0.0591592, 0.0595865, 0.060029, 0.0604562, 0.0608835, 0.061326, 0.0617533, 0.0621958, 0.0626383, 0.0630808, 0.0635233, 0.0639811, 0.0644236, 0.0648661, 0.0653239, 0.0657816, 0.0662394, 0.0666972, 0.067155, 0.0676127, 0.0680705, 0.0685435, 0.0690013, 0.0694743, 0.0699474, 0.0704204, 0.0708934, 0.0713664, 0.0718395, 0.0723278, 0.0728008, 0.0732891, 0.0737774, 0.0742657, 0.0747539, 0.0752422, 0.0757305, 0.0762188, 0.0767224, 0.0772259, 0.0777142, 0.0782177, 0.0787213, 0.0792401, 0.0797436, 0.0802472, 0.080766, 0.0812696, 0.0817884, 0.0823072, 0.082826, 0.0833448, 0.0838636, 0.0843977, 0.0849165, 0.0854505, 0.0859846, 0.0865187, 0.0870527, 0.0875868, 0.0881209, 0.0886549, 0.0892042, 0.0897536, 0.0902876, 0.090837, 0.0913863, 0.0919356, 0.0925002, 0.0930495, 0.0936141, 0.0941634, 0.094728, 0.0952926, 0.0958572, 0.0964218, 0.0970016, 0.0975662, 0.098146, 0.0987106, 0.0992905, 0.0998703, 0.1004501, 0.10103, 0.1016251, 0.1022049, 0.1028, 0.1033799, 0.103975, 0.1045701, 0.1051652, 0.1057755, 0.1063706, 0.106981, 0.1075761, 0.1081865, 0.1087968, 0.1094072, 0.1100175, 0.1106279, 0.1112535, 0.1118639, 0.1124895, 0.1131151, 0.1137407, 0.1143664, 0.114992, 0.1156176, 0.1162585, 0.1168841, 0.117525, 0.1181659, 0.1188067, 0.1194476, 0.1200885, 0.1207446, 0.1213855, 0.1220417, 0.1226978, 0.1233539, 0.1240101, 0.1246662, 0.1253223, 0.1259937, 0.1266499, 0.1273213, 0.1279927, 0.1286641, 0.1293355, 0.1300069, 0.1306935, 0.1313649, 0.1320516, 0.1327382, 0.1334096, 0.1341115, 0.1347982, 0.1354849, 0.1361868, 0.1368734, 0.1375753, 0.1382773, 0.1389792, 0.1396811, 0.140383, 0.1411002, 0.1418021, 0.1425193, 0.1432364, 0.1439536, 0.1446708, 0.145388, 0.1461204, 0.1468376, 0.14757, 0.1483024, 0.1490349, 0.1497673, 0.1504997, 0.1512322, 0.1519799, 0.1527123, 0.15346, 0.1542077, 0.1549554, 0.1557031, 0.1564508, 0.1572137, 0.1579767, 0.1587243, 0.1594873, 0.1602502, 0.1610132, 0.1617914, 0.1625544, 0.1633326, 0.1640955, 0.1648737, 0.1656519, 0.1664302, 0.1672236, 0.1680018, 0.1687953, 0.1695735, 0.170367, 0.1711604, 0.1719539, 0.1727474, 0.1735561, 0.1743496, 0.1751583, 0.175967, 0.1767758, 0.1775845, 0.1783932, 0.1792172, 0.1800259, 0.1808499, 0.1816739, 0.1824826, 0.1833219, 0.1841459, 0.1849699, 0.1858091, 0.1866331, 0.1874723, 0.1883116, 0.1891508, 0.1900053, 0.1908446, 0.1916838, 0.1925383, 0.1933928, 0.1942473, 0.1951019, 0.1959564, 0.1968261, 0.1976806, 0.1985504, 0.1994202, 0.2002899, 0.2011597, 0.2020294, 0.2028992, 0.2037842, 0.2046693, 0.205539, 0.206424, 0.2073243, 0.2082094, 0.2090944, 0.2099947, 0.2108949, 0.21178, 0.2126802, 0.2135958, 0.2144961, 0.2153964, 0.2163119, 0.2172274, 0.2181277, 0.2190585, 0.2199741, 0.2208896, 0.2218051, 0.2227359, 0.2236667, 0.2245975, 0.2255283, 0.2264591, 0.2273899, 0.228336, 0.2292821, 0.2302129, 0.2311589, 0.232105, 0.2330663, 0.2340124, 0.2349737, 0.2359197, 0.2368811, 0.2378424, 0.2388037, 0.239765, 0.2407416, 0.2417029, 0.2426795, 0.2436561, 0.2446326, 0.2456092, 0.2466011, 0.2475776, 0.2485695, 0.249546, 0.2505379, 0.2515297, 0.2525368, 0.2535286, 0.2545357, 0.2555276, 0.2565347, 0.2575418, 0.2585489, 0.259556, 0.2605783, 0.2615854, 0.2626078, 0.2636301, 0.2646525, 0.2656748, 0.2667124, 0.2677348, 0.2687724, 0.26981, 0.2708324, 0.2718853, 0.2729229, 0.2739605, 0.2750134, 0.276051, 0.2771038, 0.2781567, 0.2792248, 0.2802777, 0.2813306, 0.2823987, 0.2834668, 0.284535, 0.2856031, 0.2866712, 0.2877394, 0.2888228, 0.2899062, 0.2909743, 0.2920577, 0.2931563, 0.2942397, 0.2953231, 0.2964218, 0.2975204, 0.2986191, 0.2997177, 0.3008164, 0.301915, 0.3030289, 0.3041428, 0.3052567, 0.3063706, 0.3074846, 0.3085985, 0.3097124, 0.3108415, 0.3119707, 0.3130999, 0.314229, 0.3153582, 0.3165026, 0.3176318, 0.3187762, 0.3199207, 0.3210651, 0.3222095, 0.3233539, 0.3245136, 0.3256733, 0.3268177, 0.3279774, 0.3291371, 0.330312, 0.3314717, 0.3326467, 0.3338216, 0.3349966, 0.3361715, 0.3373465, 0.3385214, 0.3397116, 0.3408865, 0.3420768, 0.343267, 0.3444724, 0.3456626, 0.3468528, 0.3480583, 0.3492638, 0.3504692, 0.3516747, 0.3528801, 0.3541009, 0.3553063, 0.356527, 0.3577478, 0.3589685, 0.3601892, 0.3614252, 0.3626459, 0.3638819, 0.3651179, 0.3663539, 0.3675898, 0.3688411, 0.3700771, 0.3713283, 0.3725795, 0.3738308, 0.375082, 0.3763333, 0.3775998, 0.378851, 0.3801175, 0.381384, 0.3826505, 0.3839322, 0.3851987, 0.3864805, 0.387747, 0.3890288, 0.3903105, 0.3916075, 0.3928893, 0.3941863, 0.3954681, 0.3967651, 0.3980621, 0.3993744, 0.4006714, 0.4019837, 0.4032807, 0.404593, 0.4059052, 0.4072175, 0.4085451, 0.4098573, 0.4111849, 0.4125124, 0.4138399, 0.4151675, 0.416495, 0.4178378, 0.4191806, 0.4205234, 0.4218662, 0.423209, 0.4245518, 0.4259098, 0.4272526, 0.4286107, 0.4299687, 0.4313268, 0.4326848, 0.4340581, 0.4354314, 0.4367895, 0.4381628, 0.4395514, 0.4409247, 0.442298, 0.4436866, 0.4450752, 0.4464637, 0.4478523, 0.4492409, 0.4506447, 0.4520333, 0.4534371, 0.4548409, 0.4562448, 0.4576486, 0.4590677, 0.4604715, 0.4618906, 0.4633097, 0.4647288, 0.4661631, 0.4675822, 0.4690166, 0.4704356, 0.47187, 0.4733043, 0.4747539, 0.4761883, 0.4776379, 0.4790875, 0.4805371, 0.4819867, 0.4834363, 0.4848859, 0.4863508, 0.4878157, 0.4892805, 0.4907454, 0.4922103, 0.4936904, 0.4951553, 0.4966354, 0.4981155, 0.4995956, 0.501091, 0.5025711, 0.5040665, 0.5055467, 0.507042, 0.5085527, 0.5100481, 0.5115435, 0.5130541, 0.5145647, 0.5160754, 0.517586, 0.5190967, 0.5206226, 0.5221485, 0.5236591, 0.525185, 0.5267262, 0.5282521, 0.529778, 0.5313191, 0.5328603, 0.5344015, 0.5359426, 0.537499, 0.5390402, 0.5405966, 0.542153, 0.5437095, 0.5452659, 0.5468223, 0.548394, 0.5499657, 0.5515373, 0.553109, 0.5546807, 0.5562524, 0.5578393, 0.5594263, 0.5610132, 0.5626001, 0.5641871, 0.565774, 0.5673762, 0.5689784, 0.5705806, 0.5721828, 0.573785, 0.5754025, 0.5770047, 0.5786221, 0.5802396, 0.581857, 0.5834897, 0.5851072, 0.5867399, 0.5883726, 0.5900053, 0.5916381, 0.5932708, 0.5949187, 0.5965667, 0.5982147, 0.5998627, 0.6015106, 0.6031586, 0.6048219, 0.6064851, 0.6081483, 0.6098116, 0.6114748, 0.6131533, 0.6148165, 0.616495, 0.6181735, 0.619852, 0.6215457, 0.6232242, 0.624918, 0.6266117, 0.6283055, 0.6299992, 0.631693, 0.633402, 0.635111, 0.63682, 0.638529, 0.640238, 0.6419471, 0.6436713, 0.6453956, 0.6471199, 0.6488441, 0.6505684, 0.6523079, 0.6540322, 0.6557717, 0.6575113, 0.6592508, 0.6610056, 0.6627451, 0.6644999, 0.6662547, 0.6680095, 0.6697642, 0.6715343, 0.6732891, 0.6750591, 0.6768292, 0.6785992, 0.6803845, 0.6821546, 0.6839399, 0.6857252, 0.6875105, 0.6892958, 0.6910811, 0.6928817, 0.6946822, 0.6964675, 0.6982834, 0.7000839, 0.7018845, 0.7037003, 0.7055161, 0.707332, 0.7091478, 0.7109636, 0.7127947, 0.7146105, 0.7164416, 0.7182727, 0.720119, 0.7219501, 0.7237964, 0.7256275, 0.7274739, 0.7293355, 0.7311818, 0.7330282, 0.7348898, 0.7367514, 0.738613, 0.7404746, 0.7423514, 0.744213, 0.7460899, 0.7479667, 0.7498436, 0.7517205, 0.7536126, 0.7554894, 0.7573816, 0.7592737, 0.7611658, 0.7630732, 0.7649653, 0.7668727, 0.76878, 0.7706874, 0.7725948, 0.7745174, 0.7764248, 0.7783474, 0.7802701, 0.7821927, 0.7841306, 0.7860533, 0.7879911, 0.789929, 0.7918669, 0.7938048, 0.795758, 0.7976959, 0.799649, 0.8016022, 0.8035554, 0.8055238, 0.8074769, 0.8094453, 0.8114137, 0.8133822, 0.8153506, 0.8173342, 0.8193179, 0.8212863, 0.82327, 0.8252689, 0.8272526, 0.8292515, 0.8312352, 0.8332341, 0.8352331, 0.8372473, 0.8392462, 0.8412604, 0.8432746, 0.8452888, 0.847303, 0.8493172, 0.8513466, 0.8533761, 0.8554055, 0.857435, 0.8594644, 0.8614939, 0.8635386, 0.8655833, 0.867628, 0.8696727, 0.8717327, 0.8737774, 0.8758373, 0.8778973, 0.8799573, 0.8820325, 0.8840925, 0.8861677, 0.8882429, 0.8903182, 0.8923934, 0.8944839, 0.8965591, 0.8986496, 0.9007401, 0.9028305, 0.9049363, 0.9070268, 0.9091325, 0.9112383, 0.913344, 0.915465, 0.9175708, 0.9196918, 0.9218128, 0.9239338, 0.9260548, 0.9281758, 0.930312, 0.9324483, 0.9345846, 0.9367208, 0.9388571, 0.9410086, 0.9431601, 0.9453117, 0.9474632, 0.9496147, 0.9517815, 0.953933, 0.9560998, 0.9582666, 0.9604334, 0.9626154, 0.9647822, 0.9669642, 0.9691463, 0.9713283, 0.9735256, 0.9757076, 0.9779049, 0.9801022, 0.9822995, 0.9844968, 0.9867094, 0.988922, 0.9911345, 0.9933471, 0.9955596, 0.9977722, 1.0" ;
        <http://sparql.xyz/facade-x/data/ISO Speed Ratings>
                "100" ;
        <http://sparql.xyz/facade-x/data/Image Height>  "68 pixels" ;
        <http://sparql.xyz/facade-x/data/Image Width>   "100 pixels" ;
        <http://sparql.xyz/facade-x/data/Interoperability Index>
                "Recommended Exif Interoperability Rules (ExifR98)" ;
        <http://sparql.xyz/facade-x/data/Interoperability Version>
                "1.00" ;
        <http://sparql.xyz/facade-x/data/Luminance>     "(76.0365, 80, 87.1246)" ;
        <http://sparql.xyz/facade-x/data/Make>          "Canon" ;
        <http://sparql.xyz/facade-x/data/Measurement>   "1931 2° Observer, Backing (0, 0, 0), Geometry Unknown, Flare 1%, Illuminant D65" ;
        <http://sparql.xyz/facade-x/data/Media Black Point>
                "(0, 0, 0)" ;
        <http://sparql.xyz/facade-x/data/Media White Point>
                "(0.9505, 1, 1.0891)" ;
        <http://sparql.xyz/facade-x/data/Metering Mode>
                "Multi-segment" ;
        <http://sparql.xyz/facade-x/data/Model>         "Canon EOS 40D" ;
        <http://sparql.xyz/facade-x/data/Number of Components>
                "3" ;
        <http://sparql.xyz/facade-x/data/Number of Tables>
                "4 Huffman tables" ;
        <http://sparql.xyz/facade-x/data/Orientation>   "Top, left side (Horizontal / normal)" ;
        <http://sparql.xyz/facade-x/data/Primary Platform>
                "Microsoft Corporation" ;
        <http://sparql.xyz/facade-x/data/Profile Connection Space>
                "XYZ " ;
        <http://sparql.xyz/facade-x/data/Profile Copyright>
                "Copyright (c) 1998 Hewlett-Packard Company" ;
        <http://sparql.xyz/facade-x/data/Profile Date/Time>
                "1998:02:09 06:49:00" ;
        <http://sparql.xyz/facade-x/data/Profile Description>
                "sRGB IEC61966-2.1" ;
        <http://sparql.xyz/facade-x/data/Profile Size>  "3144" ;
        <http://sparql.xyz/facade-x/data/Red Colorant>  "(0.4361, 0.2225, 0.0139)" ;
        <http://sparql.xyz/facade-x/data/Red TRC>       "0.0, 0.0000763, 0.0001526, 0.0002289, 0.0003052, 0.0003815, 0.0004578, 0.0005341, 0.0006104, 0.0006867, 0.000763, 0.0008392, 0.0009003, 0.0009766, 0.0010529, 0.0011292, 0.0012055, 0.0012818, 0.0013581, 0.0014343, 0.0015106, 0.0015869, 0.0016632, 0.0017395, 0.0018158, 0.0018921, 0.0019684, 0.0020447, 0.002121, 0.0021973, 0.0022736, 0.0023499, 0.0024262, 0.0025025, 0.0025788, 0.0026551, 0.0027161, 0.0027924, 0.0028687, 0.002945, 0.0030213, 0.0030976, 0.0031739, 0.0032502, 0.0033417, 0.003418, 0.0034943, 0.0035859, 0.0036622, 0.0037537, 0.00383, 0.0039216, 0.0040131, 0.0041047, 0.0041962, 0.0042878, 0.0043793, 0.0044709, 0.0045624, 0.0046693, 0.0047608, 0.0048524, 0.0049592, 0.005066, 0.0051575, 0.0052644, 0.0053712, 0.005478, 0.0055848, 0.0056916, 0.0057984, 0.0059052, 0.0060273, 0.0061341, 0.0062562, 0.006363, 0.0064851, 0.0066072, 0.0067292, 0.0068513, 0.0069734, 0.0070954, 0.0072175, 0.0073396, 0.0074617, 0.007599, 0.0077211, 0.0078584, 0.0079957, 0.0081178, 0.0082551, 0.0083925, 0.0085298, 0.0086671, 0.0088045, 0.008957, 0.0090944, 0.0092317, 0.0093843, 0.0095369, 0.0096742, 0.0098268, 0.0099794, 0.010132, 0.0102846, 0.0104372, 0.0105898, 0.0107576, 0.0109102, 0.0110628, 0.0112306, 0.0113985, 0.0115511, 0.0117189, 0.0118868, 0.0120546, 0.0122225, 0.0124056, 0.0125734, 0.0127413, 0.0129244, 0.0130922, 0.0132753, 0.0134585, 0.0136416, 0.0138247, 0.0140078, 0.0141909, 0.014374, 0.0145571, 0.0147555, 0.0149386, 0.0151369, 0.0153201, 0.0155184, 0.0157168, 0.0159152, 0.0161135, 0.0163119, 0.0165255, 0.0167239, 0.0169223, 0.0171359, 0.0173495, 0.0175479, 0.0177615, 0.0179751, 0.0181888, 0.0184024, 0.018616, 0.0188449, 0.0190585, 0.0192874, 0.019501, 0.0197299, 0.0199588, 0.0201877, 0.0204166, 0.0206455, 0.0208743, 0.0211032, 0.0213474, 0.0215763, 0.0218204, 0.0220645, 0.0222934, 0.0225376, 0.0227817, 0.0230259, 0.0232853, 0.0235294, 0.0237736, 0.024033, 0.0242771, 0.0245365, 0.0247959, 0.0250553, 0.0253147, 0.0255741, 0.0258335, 0.0261082, 0.0263676, 0.026627, 0.0269017, 0.0271763, 0.027451, 0.0277256, 0.0280003, 0.028275, 0.0285496, 0.0288243, 0.0291142, 0.0293889, 0.0296788, 0.0299687, 0.0302586, 0.0305486, 0.0308385, 0.0311284, 0.0314183, 0.0317235, 0.0320134, 0.0323186, 0.0326238, 0.032929, 0.0332341, 0.0335393, 0.0338445, 0.0341497, 0.0344549, 0.0347753, 0.0350805, 0.0354009, 0.0357214, 0.0360418, 0.0363622, 0.0366827, 0.0370031, 0.0373388, 0.0376593, 0.037995, 0.0383154, 0.0386511, 0.0389868, 0.0393225, 0.0396582, 0.0399939, 0.0403449, 0.0406806, 0.0410315, 0.0413825, 0.0417182, 0.0420691, 0.0424201, 0.042771, 0.0431373, 0.0434882, 0.0438392, 0.0442054, 0.0445716, 0.0449226, 0.0452888, 0.045655, 0.0460212, 0.0464027, 0.0467689, 0.0471504, 0.0475166, 0.0478981, 0.0482795, 0.048661, 0.0490425, 0.049424, 0.0498054, 0.0501869, 0.0505837, 0.0509804, 0.0513619, 0.0517586, 0.0521553, 0.0525521, 0.0529488, 0.0533608, 0.0537575, 0.0541695, 0.0545663, 0.0549783, 0.0553902, 0.0558022, 0.0562142, 0.0566262, 0.0570535, 0.0574655, 0.0578927, 0.05832, 0.058732, 0.0591592, 0.0595865, 0.060029, 0.0604562, 0.0608835, 0.061326, 0.0617533, 0.0621958, 0.0626383, 0.0630808, 0.0635233, 0.0639811, 0.0644236, 0.0648661, 0.0653239, 0.0657816, 0.0662394, 0.0666972, 0.067155, 0.0676127, 0.0680705, 0.0685435, 0.0690013, 0.0694743, 0.0699474, 0.0704204, 0.0708934, 0.0713664, 0.0718395, 0.0723278, 0.0728008, 0.0732891, 0.0737774, 0.0742657, 0.0747539, 0.0752422, 0.0757305, 0.0762188, 0.0767224, 0.0772259, 0.0777142, 0.0782177, 0.0787213, 0.0792401, 0.0797436, 0.0802472, 0.080766, 0.0812696, 0.0817884, 0.0823072, 0.082826, 0.0833448, 0.0838636, 0.0843977, 0.0849165, 0.0854505, 0.0859846, 0.0865187, 0.0870527, 0.0875868, 0.0881209, 0.0886549, 0.0892042, 0.0897536, 0.0902876, 0.090837, 0.0913863, 0.0919356, 0.0925002, 0.0930495, 0.0936141, 0.0941634, 0.094728, 0.0952926, 0.0958572, 0.0964218, 0.0970016, 0.0975662, 0.098146, 0.0987106, 0.0992905, 0.0998703, 0.1004501, 0.10103, 0.1016251, 0.1022049, 0.1028, 0.1033799, 0.103975, 0.1045701, 0.1051652, 0.1057755, 0.1063706, 0.106981, 0.1075761, 0.1081865, 0.1087968, 0.1094072, 0.1100175, 0.1106279, 0.1112535, 0.1118639, 0.1124895, 0.1131151, 0.1137407, 0.1143664, 0.114992, 0.1156176, 0.1162585, 0.1168841, 0.117525, 0.1181659, 0.1188067, 0.1194476, 0.1200885, 0.1207446, 0.1213855, 0.1220417, 0.1226978, 0.1233539, 0.1240101, 0.1246662, 0.1253223, 0.1259937, 0.1266499, 0.1273213, 0.1279927, 0.1286641, 0.1293355, 0.1300069, 0.1306935, 0.1313649, 0.1320516, 0.1327382, 0.1334096, 0.1341115, 0.1347982, 0.1354849, 0.1361868, 0.1368734, 0.1375753, 0.1382773, 0.1389792, 0.1396811, 0.140383, 0.1411002, 0.1418021, 0.1425193, 0.1432364, 0.1439536, 0.1446708, 0.145388, 0.1461204, 0.1468376, 0.14757, 0.1483024, 0.1490349, 0.1497673, 0.1504997, 0.1512322, 0.1519799, 0.1527123, 0.15346, 0.1542077, 0.1549554, 0.1557031, 0.1564508, 0.1572137, 0.1579767, 0.1587243, 0.1594873, 0.1602502, 0.1610132, 0.1617914, 0.1625544, 0.1633326, 0.1640955, 0.1648737, 0.1656519, 0.1664302, 0.1672236, 0.1680018, 0.1687953, 0.1695735, 0.170367, 0.1711604, 0.1719539, 0.1727474, 0.1735561, 0.1743496, 0.1751583, 0.175967, 0.1767758, 0.1775845, 0.1783932, 0.1792172, 0.1800259, 0.1808499, 0.1816739, 0.1824826, 0.1833219, 0.1841459, 0.1849699, 0.1858091, 0.1866331, 0.1874723, 0.1883116, 0.1891508, 0.1900053, 0.1908446, 0.1916838, 0.1925383, 0.1933928, 0.1942473, 0.1951019, 0.1959564, 0.1968261, 0.1976806, 0.1985504, 0.1994202, 0.2002899, 0.2011597, 0.2020294, 0.2028992, 0.2037842, 0.2046693, 0.205539, 0.206424, 0.2073243, 0.2082094, 0.2090944, 0.2099947, 0.2108949, 0.21178, 0.2126802, 0.2135958, 0.2144961, 0.2153964, 0.2163119, 0.2172274, 0.2181277, 0.2190585, 0.2199741, 0.2208896, 0.2218051, 0.2227359, 0.2236667, 0.2245975, 0.2255283, 0.2264591, 0.2273899, 0.228336, 0.2292821, 0.2302129, 0.2311589, 0.232105, 0.2330663, 0.2340124, 0.2349737, 0.2359197, 0.2368811, 0.2378424, 0.2388037, 0.239765, 0.2407416, 0.2417029, 0.2426795, 0.2436561, 0.2446326, 0.2456092, 0.2466011, 0.2475776, 0.2485695, 0.249546, 0.2505379, 0.2515297, 0.2525368, 0.2535286, 0.2545357, 0.2555276, 0.2565347, 0.2575418, 0.2585489, 0.259556, 0.2605783, 0.2615854, 0.2626078, 0.2636301, 0.2646525, 0.2656748, 0.2667124, 0.2677348, 0.2687724, 0.26981, 0.2708324, 0.2718853, 0.2729229, 0.2739605, 0.2750134, 0.276051, 0.2771038, 0.2781567, 0.2792248, 0.2802777, 0.2813306, 0.2823987, 0.2834668, 0.284535, 0.2856031, 0.2866712, 0.2877394, 0.2888228, 0.2899062, 0.2909743, 0.2920577, 0.2931563, 0.2942397, 0.2953231, 0.2964218, 0.2975204, 0.2986191, 0.2997177, 0.3008164, 0.301915, 0.3030289, 0.3041428, 0.3052567, 0.3063706, 0.3074846, 0.3085985, 0.3097124, 0.3108415, 0.3119707, 0.3130999, 0.314229, 0.3153582, 0.3165026, 0.3176318, 0.3187762, 0.3199207, 0.3210651, 0.3222095, 0.3233539, 0.3245136, 0.3256733, 0.3268177, 0.3279774, 0.3291371, 0.330312, 0.3314717, 0.3326467, 0.3338216, 0.3349966, 0.3361715, 0.3373465, 0.3385214, 0.3397116, 0.3408865, 0.3420768, 0.343267, 0.3444724, 0.3456626, 0.3468528, 0.3480583, 0.3492638, 0.3504692, 0.3516747, 0.3528801, 0.3541009, 0.3553063, 0.356527, 0.3577478, 0.3589685, 0.3601892, 0.3614252, 0.3626459, 0.3638819, 0.3651179, 0.3663539, 0.3675898, 0.3688411, 0.3700771, 0.3713283, 0.3725795, 0.3738308, 0.375082, 0.3763333, 0.3775998, 0.378851, 0.3801175, 0.381384, 0.3826505, 0.3839322, 0.3851987, 0.3864805, 0.387747, 0.3890288, 0.3903105, 0.3916075, 0.3928893, 0.3941863, 0.3954681, 0.3967651, 0.3980621, 0.3993744, 0.4006714, 0.4019837, 0.4032807, 0.404593, 0.4059052, 0.4072175, 0.4085451, 0.4098573, 0.4111849, 0.4125124, 0.4138399, 0.4151675, 0.416495, 0.4178378, 0.4191806, 0.4205234, 0.4218662, 0.423209, 0.4245518, 0.4259098, 0.4272526, 0.4286107, 0.4299687, 0.4313268, 0.4326848, 0.4340581, 0.4354314, 0.4367895, 0.4381628, 0.4395514, 0.4409247, 0.442298, 0.4436866, 0.4450752, 0.4464637, 0.4478523, 0.4492409, 0.4506447, 0.4520333, 0.4534371, 0.4548409, 0.4562448, 0.4576486, 0.4590677, 0.4604715, 0.4618906, 0.4633097, 0.4647288, 0.4661631, 0.4675822, 0.4690166, 0.4704356, 0.47187, 0.4733043, 0.4747539, 0.4761883, 0.4776379, 0.4790875, 0.4805371, 0.4819867, 0.4834363, 0.4848859, 0.4863508, 0.4878157, 0.4892805, 0.4907454, 0.4922103, 0.4936904, 0.4951553, 0.4966354, 0.4981155, 0.4995956, 0.501091, 0.5025711, 0.5040665, 0.5055467, 0.507042, 0.5085527, 0.5100481, 0.5115435, 0.5130541, 0.5145647, 0.5160754, 0.517586, 0.5190967, 0.5206226, 0.5221485, 0.5236591, 0.525185, 0.5267262, 0.5282521, 0.529778, 0.5313191, 0.5328603, 0.5344015, 0.5359426, 0.537499, 0.5390402, 0.5405966, 0.542153, 0.5437095, 0.5452659, 0.5468223, 0.548394, 0.5499657, 0.5515373, 0.553109, 0.5546807, 0.5562524, 0.5578393, 0.5594263, 0.5610132, 0.5626001, 0.5641871, 0.565774, 0.5673762, 0.5689784, 0.5705806, 0.5721828, 0.573785, 0.5754025, 0.5770047, 0.5786221, 0.5802396, 0.581857, 0.5834897, 0.5851072, 0.5867399, 0.5883726, 0.5900053, 0.5916381, 0.5932708, 0.5949187, 0.5965667, 0.5982147, 0.5998627, 0.6015106, 0.6031586, 0.6048219, 0.6064851, 0.6081483, 0.6098116, 0.6114748, 0.6131533, 0.6148165, 0.616495, 0.6181735, 0.619852, 0.6215457, 0.6232242, 0.624918, 0.6266117, 0.6283055, 0.6299992, 0.631693, 0.633402, 0.635111, 0.63682, 0.638529, 0.640238, 0.6419471, 0.6436713, 0.6453956, 0.6471199, 0.6488441, 0.6505684, 0.6523079, 0.6540322, 0.6557717, 0.6575113, 0.6592508, 0.6610056, 0.6627451, 0.6644999, 0.6662547, 0.6680095, 0.6697642, 0.6715343, 0.6732891, 0.6750591, 0.6768292, 0.6785992, 0.6803845, 0.6821546, 0.6839399, 0.6857252, 0.6875105, 0.6892958, 0.6910811, 0.6928817, 0.6946822, 0.6964675, 0.6982834, 0.7000839, 0.7018845, 0.7037003, 0.7055161, 0.707332, 0.7091478, 0.7109636, 0.7127947, 0.7146105, 0.7164416, 0.7182727, 0.720119, 0.7219501, 0.7237964, 0.7256275, 0.7274739, 0.7293355, 0.7311818, 0.7330282, 0.7348898, 0.7367514, 0.738613, 0.7404746, 0.7423514, 0.744213, 0.7460899, 0.7479667, 0.7498436, 0.7517205, 0.7536126, 0.7554894, 0.7573816, 0.7592737, 0.7611658, 0.7630732, 0.7649653, 0.7668727, 0.76878, 0.7706874, 0.7725948, 0.7745174, 0.7764248, 0.7783474, 0.7802701, 0.7821927, 0.7841306, 0.7860533, 0.7879911, 0.789929, 0.7918669, 0.7938048, 0.795758, 0.7976959, 0.799649, 0.8016022, 0.8035554, 0.8055238, 0.8074769, 0.8094453, 0.8114137, 0.8133822, 0.8153506, 0.8173342, 0.8193179, 0.8212863, 0.82327, 0.8252689, 0.8272526, 0.8292515, 0.8312352, 0.8332341, 0.8352331, 0.8372473, 0.8392462, 0.8412604, 0.8432746, 0.8452888, 0.847303, 0.8493172, 0.8513466, 0.8533761, 0.8554055, 0.857435, 0.8594644, 0.8614939, 0.8635386, 0.8655833, 0.867628, 0.8696727, 0.8717327, 0.8737774, 0.8758373, 0.8778973, 0.8799573, 0.8820325, 0.8840925, 0.8861677, 0.8882429, 0.8903182, 0.8923934, 0.8944839, 0.8965591, 0.8986496, 0.9007401, 0.9028305, 0.9049363, 0.9070268, 0.9091325, 0.9112383, 0.913344, 0.915465, 0.9175708, 0.9196918, 0.9218128, 0.9239338, 0.9260548, 0.9281758, 0.930312, 0.9324483, 0.9345846, 0.9367208, 0.9388571, 0.9410086, 0.9431601, 0.9453117, 0.9474632, 0.9496147, 0.9517815, 0.953933, 0.9560998, 0.9582666, 0.9604334, 0.9626154, 0.9647822, 0.9669642, 0.9691463, 0.9713283, 0.9735256, 0.9757076, 0.9779049, 0.9801022, 0.9822995, 0.9844968, 0.9867094, 0.988922, 0.9911345, 0.9933471, 0.9955596, 0.9977722, 1.0" ;
        <http://sparql.xyz/facade-x/data/Resolution Unit>
                "Inch" ;
        <http://sparql.xyz/facade-x/data/Resolution Units>
                "inch" ;
        <http://sparql.xyz/facade-x/data/Scene Capture Type>
                "Standard" ;
        <http://sparql.xyz/facade-x/data/Shutter Speed Value>
                "1/165 sec" ;
        <http://sparql.xyz/facade-x/data/Signature>     "acsp" ;
        <http://sparql.xyz/facade-x/data/Software>      "GIMP 2.4.5" ;
        <http://sparql.xyz/facade-x/data/Sub-Sec Time>  "00" ;
        <http://sparql.xyz/facade-x/data/Sub-Sec Time Digitized>
                "00" ;
        <http://sparql.xyz/facade-x/data/Sub-Sec Time Original>
                "00" ;
        <http://sparql.xyz/facade-x/data/Tag Count>     "17" ;
        <http://sparql.xyz/facade-x/data/Technology>    "CRT " ;
        <http://sparql.xyz/facade-x/data/Thumbnail Height Pixels>
                "0" ;
        <http://sparql.xyz/facade-x/data/Thumbnail Length>
                "1378 bytes" ;
        <http://sparql.xyz/facade-x/data/Thumbnail Offset>
                "1090 bytes" ;
        <http://sparql.xyz/facade-x/data/Thumbnail Width Pixels>
                "0" ;
        <http://sparql.xyz/facade-x/data/User Comment>  "" ;
        <http://sparql.xyz/facade-x/data/Version>       "2.1.0" , "1.1" ;
        <http://sparql.xyz/facade-x/data/Viewing Conditions>
                "view (0x76696577): 36 bytes" ;
        <http://sparql.xyz/facade-x/data/Viewing Conditions Description>
                "Reference Viewing Condition in IEC61966-2.1" ;
        <http://sparql.xyz/facade-x/data/White Balance Mode>
                "Auto white balance" ;
        <http://sparql.xyz/facade-x/data/X Resolution>  "72 dots" , "72 dots per inch" ;
        <http://sparql.xyz/facade-x/data/XYZ values>    "0.964 1 0.825" ;
        <http://sparql.xyz/facade-x/data/Y Resolution>  "72 dots per inch" , "72 dots" ;
        <http://sparql.xyz/facade-x/data/YCbCr Positioning>
                "Datum point" ;
        <http://sparql.xyz/facade-x/data/size>          7958 . 
```

</details>

<details><summary>Bibtex</summary>
Data:

```
@article{Knuth1984,
  title={Literate Programming},
  author={Donald E. Knuth},
  journal={The Computer Journal},
  volume={27},
  number={2},
  pages={97--111},
  year={1984},
  publisher={Oxford University Press}
}
```

Facade-X RDF:

```
[ a       <http://sparql.xyz/facade-x/ns/root> ;
  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
          [ a       <http://sparql.xyz/facade-x/data/article> ;
            <http://sparql.xyz/facade-x/data/author>
                    "Donald E. Knuth" ;
            <http://sparql.xyz/facade-x/data/journal>
                    "The Computer Journal" ;
            <http://sparql.xyz/facade-x/data/number>
                    "2" ;
            <http://sparql.xyz/facade-x/data/pages>
                    "97--111" ;
            <http://sparql.xyz/facade-x/data/publisher>
                    "Oxford University Press" ;
            <http://sparql.xyz/facade-x/data/title>
                    "Literate Programming" ;
            <http://sparql.xyz/facade-x/data/volume>
                    "27" ;
            <http://sparql.xyz/facade-x/data/year>
                    "1984"
          ]
] .

```

</details>

<details><summary>Markdown</summary>
Data:

```
# Title<br>The following list of issues:<br><br>- first issue<br>- second issue<br><br>---<br>Footer paragraph.
```

Facade-X RDF:

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .<br>@prefix xyz: <http://sparql.xyz/facade-x/data/> .<br>@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .<br>@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.<br><br>[] a fx:root, xyz:Document ;<br> rdf:_1 [<br>    a xyz:Heading ;<br>    rdf:_1 "Title"^^xsd:string ;<br>    xyz:level "1"^^xsd:int<br> ] ;<br> rdf:_2 [<br>    a xyz:Paragraph ;<br>    rdf:_1 "The following list of issues:"^^xsd:string<br> ] ;<br> rdf:_3 [<br>    a xyz:BulletList ;<br>    rdf:_1 [<br>        a xyz:ListItem ;<br>        rdf:_1 [<br>            a xyz:Paragraph ;<br>            rdf:_1 "first issue"^^xsd:string<br>        ]<br>    ] ;<br>    rdf:_2 [<br>        a xyz:ListItem ;<br>        rdf:_1 [<br>            a xyz:Paragraph ;<br>            rdf:_1 "second issue"^^xsd:string<br>        ]<br>    ]<br> ] ;<br> rdf:_4 [<br>    a xyz:ThematicBreak<br> ] ;<br> rdf:_5 [<br>     a xyz:Paragraph ;<br>     rdf:_1 "Footer paragraph."^^xsd:string<br>  ] .<br>
```

</details>

<details><summary>YAML</summary>
Data:

```
foo: bar<br>pleh: help<br>stuff:<br>  foo: bar<br>  bar: foo
```

Facade-X RDF:

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .<br>@prefix xyz: <http://sparql.xyz/facade-x/data/> .<br>@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .<br>@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.<br><br>[ a fx:root ;<br>    xyz:foo "bar"^^xsd:string ;<br>    xyz:pleh "help"^^xsd:string ;<br>    xyz:stuff [<br>        xyz:foo "bar"^^xsd:string ;<br>        xyz:bar "foo"^^xsd:string<br>    ]<br>]
```

</details>


## Configuration

SPARQL Anything will act as a virtual endpoint that can be queried exactly as a remote SPARQL endpoint.

### Passing triplification options via SERVICE IRI

In order to instruct the query processor to delegate the execution to SPARQL Anything, you can use the  following IRI-schema within SERVICE clauses.

```
x-sparql-anything ':' ([option] ('=' [value])? ','?)+
```

A minimal URI that uses only the resource locator is also possible.

```
x-sparql-anything ':' URL
```

In this case SPARQL Anything guesses the data source type from the file extension.


### Passing triplification options via Basic Graph Pattern

Alternatively, options can be provided as basic graph pattern inside the SERVICE clause as follows

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:> {
        fx:properties fx:location "https://sparql-anything.cc/example1.json" .
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

Note that

1. The SERVICE IRI scheme must be ``x-sparql-anything:``.
2. Each triplification option to pass to the engine corresponds to a triple of the Basic Graph Pattern inside the SERVICE clause.
3. Such triples must have ``fx:properties`` as subject, ``fx:[OPTION-NAME]`` as predicate, and a literal or a variable as object.

You can also mix the two modalities as follows.

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {

    SERVICE <x-sparql-anything:blank-nodes=false> {
        fx:properties fx:location "https://sparql-anything.cc/example1.json" .
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }

}
```

### General purpose options

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|location*|The URL of the data source.|Any valid URL.|-|
|content*|The content to be transformed.|Any valid literal.|-|
|command*|An external command line to be executed. The output is handled according to the option 'media-type'|Any valid literal.|-|
|root|The IRI of generated root resource.|Any valid IRI.|location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set) |
|media-type|The media-type of the data source.|Any valid [Media-Type](https://en.wikipedia.org/wiki/Media_type). Supported media-types: application/xml, image/png, text/html, application/octet-stream, application/json, image/jpeg, image/tiff, image/bmp, text/csv, image/vnd.microsoft.icon,text/plain |No value (the media-type will be guessed from the the file extension)|
|namespace|The namespace prefix for the properties that will be generated.|Any valid namespace prefix.|http://sparql.xyz/facade-x/data/|
|blank-nodes|It tells sparql.anything to generate blank nodes or not.|true/false|true|
|trim-strings|Trim all string literals.|true/false|false|
|null-string|Do not produce triples where the specificed string would be in the object position of the triple.|any string|not set|
|triplifier|It forces sparql.anything to use a specific triplifier for transforming the data source|A canonical name of a Java class|No value|
|charset|The charset of the data source.|Any charset.|UTF-8|
|metadata|It tells sparql.anything to extract metadata from the data source and to store it in the named graph with URI &lt;http://sparql.xyz/facade-x/data/metadata&gt;  |true/false|false|
|ondisk|It tells sparql.anything to use an on disk graph (instead of the default in memory graph). The string should be a path to a directory where the on disk graph will be stored. Using an on disk graph is almost always slower (than using the default in memory graph) but with it you can triplify large files without running out of memory.|a path to a directory|not set|
|ondisk.reuse|When using an on disk graph, it tells sparql.anything to reuse the previous on disk graph.|true|not set|
|strategy|The execution strategy. 0 = in memory, all triples; 1 = in memory, only triples matching any of the triple patterns in the where clause|0,1|1|
|slice|The resources is sliced and the SPARQL query executed on each one of the parts. Supported by: CSV (row by row); JSON (when array slice by item, when json object requires `json.path`); XML (requires `xml.path`) |true/false|false|

\* It is mandatory to provide either `location`, `content`, or `command`.

### Format specific options

#### HTML

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|html.selector|A CSS selector that restricts the HTML tags to consider for the triplification.|Any valid CSS selector.|No Value|
|html.browser|It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](BROWSER.md).|chromium\|webkit\|firefox|No Value|
|html.browser.timeout|When using a browser to nagivate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout. |any integer|30000|
|html.browser.wait|When using a browser to nagivate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML.|any integer|No Value|
|html.browser.screenshot|When using a browser to nagivate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here.|a file URI e.g. "file:///tmp/screenshot.png" |No Value|
|html.metadata|It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. (cf. [issue 164](https://github.com/SPARQL-Anything/sparql.anything/issues/164))|true/false|false|

#### CSV

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|csv.format|The format of the input CSV file.|Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache's commons CSV library|Default|
|csv.headers|It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples.|true/false|false|
|csv.delimiter|The column delimiter, usually `,`,`;`,`\t`, ...|any single char|`,`|
|csv.null-string|It tells the CSV triplifier to not produce triples where the specificed string would be in the object position of the triple.|any string|not set|

#### JSON

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|json.path|One or more JsonPath expressions as filters. E.g. `json.path=value` or `json.path.1`, `json.path.2`, `...` to add multiple expressions.|Any valid JsonPath (see [JsonSurfer implementation](https://github.com/jsurfer/JsonSurfer)))||

#### XML

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|xml.path|One or more XPath expressions as filters. E.g. `xml.path=value` or `xml.path.1`, `xml.path.2`,`...` to add multiple expressions.|Any valid XPath||


#### BIN, PNG, JPEG, JPG, BMP, TIFF, TIF, ICO

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|bin.encoding|The encoding to use for generating the representation of the file.|BASE64|BASE64|

#### TXT

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|txt.regex|It tells sparql.anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex.|Any valid regular expression|No value|
|txt.group|It tells sparql.anything to generate slots by using a specific group of the regular expression.|Any integer|No value|
|txt.split|It tells sparql.anything to split the input around the matches of the give regular expression.|Any valid regular expression|No value|

#### File system and archives (Zip, Tar)

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|archive.matches|It tells sparql.anything to evaluate a regular expression on the filenames within the archives. In this case the slots will be filled with the files that match the regex only.|Any valid regular expression|.*|

#### Spreadsheet: XLS, XLSx

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|spreadsheet.headers|It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.|true/false|false|

#### Document: DOCx

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|docs.table-headers|It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples.|true/false|false|
|docs.merge-paragraphs|It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved)|true/false|false|


### HTTP options
SPARQL Anything relies on Apache Commons HTTP for HTTP connections.

|Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|http.client.*|Calls methods on the HTTPClient Java object. E.g. `http.client.useSystemProperties=false` means to avoid inheriting Java system properties (Default 'yes')|
|http.client.useSystemProperties|Use Java System Properties to configure the HTTP Client.|true/false|true|
|http.header.*|To add headers to the HTTP request. E.g. `http.header.accept=application/json`|||
|http.query.*|To add parameters to the query string. E.g. `http.query.var=value` or `http.query.var.1=value` to add more variable of the same name|||
|http.form.*|To add parameters to the POST content. E.g. `http.form.var=value` or `http.form.var.1=value` to add more variable of the same name|||
|http.method|HTTP Method|GET,POST,...|GET|
|http.payload| Sets the payload of the request|||
|http.protocol|Protocol|0.9,1.0,1.1|1.1|
|http.auth.user|Authentication: user name|||
|http.auth.password|Authentication: password|||
|http.redirect|Follow redirect?|true,false|true|

## Query static RDF files

The SPARQL Anything engine can load static RDF files in memory and perform the query against it, alongside any `x-sparql-anything` service clause.
RDF files produced by previous SPARQL Anything processes can joined with data coming from additional resources.
This feature is enabled with the command line argument `-l|--load` that accepts a file or a directory.
In the second case, all RDF files in the folder are loaded in memory before execution.

## Query templates and variable bindings

SPARQL Anything uses the [BASIL convention for variable names in queries](https://github.com/basilapi/basil/wiki/SPARQL-variable-name-convention-for-WEB-API-parameters-mapping). 

The syntax is based on the underscore character: '_', and can be easily learned by examples:

- `?_name` The variable specifies the API mandatory parameter _name_. The value is incorporated in the query as plain literal.
- `?__name` The parameter _name_ is optional.
- `?_name_iri` The variable is substituted with the parameter value as a IRI.
- `?_name_en` The parameter value is considered as literal with the language 'en' (e.g., en,it,es, etc.).
- `?_name_integer` The parameter value is considered as literal and the XSD datatype 'integer' is added during substitution.
- `?_name_prefix_datatype` The parameter value is considered as literal and the datatype 'prefix:datatype' is added during substitution. The prefix must be specified according to the SPARQL syntax.

Variable bindings can be passed in two ways:

- Inline arguments, using the option `-v|--values`
- Passing an SPARQL Result Set file, using the option `-i|--input`

In the first case, the engine computes the cardinal product of all the variables bindings included and execute the query for each one of the resulting set of bindings.

In the second case, the query is executed for each set of bindings in the result set.

## Functions and magic properties
SPARQL Anything is built on Apache Jena, see a list of supported functions on the [Apache Jena documentation](https://jena.apache.org/documentation/query/library-function.html).
### Working with sequences
#### The `fx:anySlot` magic property
The execution engine is sensible to the magic property 

``<http://sparql.xyz/facade-x/ns/anySlot>`` 

This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).

#### Functions on container membership properties
The system supports the following functions on container membership properties (See [issue 78](https://github.com/SPARQL-Anything/sparql.anything/issues/78)):

- `fx:before(?a, ?b)` returns `true` if ?a and ?b are container membership properties and ?a is lower than ?b, false otherwise
- `fx:after(?a, ?b)`  returns `true` if ?a and ?b are container membership properties and ?a is higher than ?b, false otherwise
- `fx:previous(?a)` returns the container membership property that preceeds ?a (rdf:\_2 -> rdf:\_1)
- `fx:next(?b)` returns the container membership property that succeedes ?b (rdf:\_1 -> rdf:\_2)
- `fx:forward(?a, ?b)` returns the container membership property that follows ?a of ?b steps (rdf:\_2, 5 -> rdf:\_7)
- `fx:backward(?a, ?b)` returns the container membership property that preceeds ?a of ?b steps (rdf:\_24, 4 -> rdf:\_20)
### Working with strings
The system supports the following functions for string manipulation (See [issue 104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):

- `fx:String.startsWith` wraps [`java.lang.String.startsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.endsWith` wraps [`java.lang.String.endsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.indexOf` wraps [`java.lang.String.indexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.substring` wraps [`java.lang.String.substring`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.toLowerCase` wraps [`java.lang.String.toLowerCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.toUpperCase` wraps [`java.lang.String.toUpperCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- `fx:String.trim` wraps [`java.lang.String.trim`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

The system supports the following functions to manipulate words (See [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):
 
- `WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))
- `WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))
- `WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))
- `WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))
- `WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))

### Hash functions
The system supports the following functions for computing hash digest from strings (See issues [104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):
- `fx:DigestUtils.md2Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md2Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md2Hex-java.lang.String-)
- `fx:DigestUtils.md5Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md5Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md5Hex-java.lang.String-)
- `fx:DigestUtils.sha1Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha1Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha1Hex-java.lang.String-)
- `fx:DigestUtils.sha256Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha256Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha256Hex-java.lang.String-)
- `fx:DigestUtils.sha384Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha384Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha384Hex-java.lang.String-)
- `fx:DigestUtils.sha512Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha512Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha512Hex-java.lang.String-)

### Functions on URLs
The system supports the following functions operating on strings that are URLs (See [issue 176](https://github.com/SPARQL-Anything/sparql.anything/issues/)):
- `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html#encode(java.lang.String,java.lang.String))
- `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))

### The function `fx:serial`:
The function `fx:serial (?a ... ?n)` generates an incremental number using the arguments as reference counters. For example, calling `fx:serial("x")` two times will generate `1` and then `2`. Instead, calling `fx:serial(?x)` multiple times will generate sequential numbers for each value of `?x`.

### The function `fx:entity`
The function `fx:entity (?a ... ?n)` accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (`rdf:_1`,`rdf:_2`,...) are cast to numbers and then to strings (`"1","2"`).
```
BIND ( fx:entity ( myns:, "dummy-entity", 1) AS ?myentity) 
# is equivalent to
BIND ( IRI( CONCAT ( STR (myns:), "dummy-entity", STR(1) ) AS ?myentity )
```
See also [issue 106](https://github.com/SPARQL-Anything/sparql.anything/issues/106)

### The function `fx:literal`
The function `fx:literal( ?a , ?b )` builds a literal from the string representation of `?a`, using `?b` either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).

## Usage
### Command line interface

An executable JAR can be obtained from the [Releases](https://github.com/spice-h2020/sparql.anything/releases) page.

The jar can be executed as follows:

```
usage: java -jar sparql.anything-<version> -q query [-f format] [-i
            filepath]  [-l path] [-o filepath]
 -f,--format <string>                  OPTIONAL -  Format of the output
                                       file. Supported values: JSON, XML,
                                       CSV, TEXT, TTL, NT, NQ. [Default:
                                       TEXT or TTL]
 -i,--input <input>                    OPTIONAL - The path to a SPARQL
                                       result set file to be used as
                                       input. When present, the query is
                                       pre-processed by substituting
                                       variable names with values from the
                                       bindings provided. The query is
                                       repeated for each set of bindings
                                       in the input result set.
 -l,--load <load>                      OPTIONAL - The path to one RDF file
                                       or a folder including a set of
                                       files to be loaded. When present,
                                       the data is loaded in memory and
                                       the query executed against it.
 -o,--output <file>                    OPTIONAL - The path to the output
                                       file. [Default: STDOUT]
 -p,--output-pattern <outputPattern>   OPTIONAL - Output filename pattern,
                                       e.g. 'myfile-?friendName.json'.
                                       Variables should start with '?' and
                                       refer to bindings from the input
                                       file. This option can only be used
                                       in combination with 'input' and is
                                       ignored otherwise. This option
                                       overrides 'output'.
 -q,--query <query>                    The path to the file storing the
                                       query to execute or the query
                                       itself.
 -s,--strategy <strategy>              OPTIONAL - Strategy for query
                                       evaluation. Possible values: '1' -
                                       triple filtering (default), '0' -
                                       triplify all data. The system
                                       fallbacks to '0' when the strategy
                                       is not implemented yet for the
                                       given resource type.
 -v,--values <values>                  OPTIONAL - Values passed as input
                                       to a query template. When present,
                                       the query is pre-processed by
                                       substituting variable names with
                                       the values provided. The passed
                                       argument must follow the syntax:
                                       var_name=var_value. Multiple
                                       arguments are allowed. The query is
                                       repeated for each set of values.

```
Logging can be configured adding the following option (SLF4J):
```
-Dorg.slf4j.simpleLogger.defaultLogLevel=trace
```

### Fuseki

An executable JAR of a SPARQL-Anything-powered Fuseki endpoint can be obtained from the [Releases](https://github.com/spice-h2020/sparql.anything/releases) page.

The jar can be executed as follows:

```
usage: java -jar sparql-anything-server-<version>.jar [-p port] [-e
            sparql-endpoint-path] [-g endpoint-gui-path]
 -e,--path <path>   The path where the server will be running on (Default
                    /sparql.anything).
 -g,--gui <gui>     The path of the SPARQL endpoint GUI (Default /sparql).
 -p,--port <port>   The port where the server will be running on (Default
                    3000 ).
```

Also a docker image can be used by following the instructions [here](BROWSER.md).

## Licence

SPARQL Anything is distributed under [Apache 2.0 License](LICENSE)

## How to cite our work
Daga, Enrico; Asprino, Luigi; Mulholland, Paul and Gangemi, Aldo (2021). Facade-X: An Opinionated Approach to SPARQL Anything. In: Alam, Mehwish; Groth, Paul; de Boer, Victor; Pellegrini, Tassilo and Pandit, Harshvardhan J. eds. Volume 53: Further with Knowledge Graphs, Volume 53. IOS Press, pp. 58–73.

DOI: https://doi.org/10.3233/ssw210035 | [PDF](http://oro.open.ac.uk/78973/1/78973.pdf)
```bibtex
@incollection{oro78973,
          volume = {53},
           month = {August},
          author = {Enrico Daga and Luigi Asprino and Paul Mulholland and Aldo Gangemi},
       booktitle = {Volume 53: Further with Knowledge Graphs},
          editor = {Mehwish Alam and Paul Groth and Victor de Boer and Tassilo Pellegrini and Harshvardhan J. Pandit},
           title = {Facade-X: An Opinionated Approach to SPARQL Anything},
       publisher = {IOS Press},
            year = {2021},
         journal = {Studies on the Semantic Web},
           pages = {58--73},
        keywords = {SPARQL; meta-model; re-engineering},
             url = {http://oro.open.ac.uk/78973/},
        abstract = {The Semantic Web research community understood since its beginning how crucial it is to equip practitioners with methods to transform non-RDF resources into RDF. Proposals focus on either engineering content transformations or accessing non-RDF resources with SPARQL. Existing solutions require users to learn specific mapping languages (e.g. RML), to know how to query and manipulate a variety of source formats (e.g. XPATH, JSON-Path), or to combine multiple languages (e.g. SPARQL Generate). In this paper, we explore an alternative solution and contribute a general-purpose meta-model for converting non-RDF resources into RDF: {\ensuremath{<}}i{\ensuremath{>}}Facade-X{\ensuremath{<}}/i{\ensuremath{>}}. Our approach can be implemented by overriding the SERVICE operator and does not require to extend the SPARQL syntax. We compare our approach with the state of art methods RML and SPARQL Generate and show how our solution has lower learning demands and cognitive complexity, and it is cheaper to implement and maintain, while having comparable extensibility and efficiency.}
}
```
