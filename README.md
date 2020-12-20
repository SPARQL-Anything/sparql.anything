# Welcome
sparql.anything is a system for Semantic Web re-engineering that allows users to ... query anything with SPARQL.

## Facade-X
sparql.anything uses a single generic abstraction for all data source formats called Facade-X.
Facade-X is a simplistic meta-model used by sparql.anything transformers to generate RDF data from diverse data sources.
Intuitively, Facade-X uses a subset of RDF as a general approach to represent the source content *as-it-is* but in RDF.
The model combines two type of elements: containers and literals.
Facade-X has always a single root container. 
Container members are a combination of key-value pairs, where keys are either RDF properties or container membership properties.
Instead, values can be either RDF literals or other containers.
This is a generic example of a Facade-X data object (more examples below):

```
@prefix fx: <urn:facade-x:ns#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
[] rdf:_1 [
    fx:someKey "some value" ;
    rdf:_1 "another value with unspecified key" ;
    rdf:_2 [
        rdf:type fx:MyType
        rdf:_1 "another value" 
    ]
```

## Querying anything
sparql.anything extends the Apache Jena ARQ processors by *overloading* the SERVICE operator, as in the following example:

Suppose having this JSON file as input (also available at ``https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/examples/example1.json``)

```
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

With sparql.anything you can select the TV series starring "Courteney Cox" with the SPARQL query

```
PREFIX fx: <urn:facade-x:ns#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT ?seriesName
WHERE {

    SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/examples/example1.json> {
        ?tvSeries fx:name ?seriesName .
        ?tvSeries fx:stars ?star .
        ?star ?li "Courteney Cox" .
    }

}
```

and get this result 

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

without caring of transforming JSON to RDF. 

## Supported Formats
Currently, sparql.anything supports the following formats: "json", "html", "xml", "csv", "bin", "png","jpeg","jpg","bmp","tiff","tif", "ico", "txt" ... but the possibilities are limitless!


