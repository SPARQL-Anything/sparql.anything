# A gentle introduction to SPARQL Anything
Today, I’m going to introduce you to SPARQL Anything, a tool that allows you to query any file of any format using SPARQL.

First, I’m going to present Facade-X, a simplistic meta-model used for generating RDF data from heterogeneous data sources.
Then, I will show you how SPARQL Anything triplifies data and how you can use the SPARQL Anything server to query heterogeneous data sources.
Finally, I’m going to show you how to construct a knowledge graph using SPARQL Anything.

SPARQL Anything uses a single generic abstraction for all data formats called Facade-X, a simplistic meta-model used for generating RDF data from heterogeneous data sources.
Intuitively, Facade-X uses few RDF primitives for porting the content of an input file to RDF.
The model combines two types of elements: containers and literals.
Containers are linked to their items either via RDF container membership properties (like lists) or by means of  RDF properties (like a Key-Value map).  Values can be literals or other containers.
Each container may have a type which can be expressed with the property rdf:type.
And that’s it, no other RDF primitives are needed for porting content to RDF.

## Triplifying collections
Now, I’m going to show you some simple examples of how things are triplified and then how to query the triplification from SPARQL.

Let’s start with lists.
Here you can see a list of strings, serialized in JSON format.

```json
  ["apple", "banana", "orange"]
```

SPARQL Anything will first create a container for representing the collection let’s call it _:fruits and then uses RDF container membership properties to connect the container with its slots

```
    _:fruits rdf:_1 "apple"  .
    _:fruits rdf:_2 "banana" .
    _:fruits rdf:_3 "orange" .
```

By default, SPARQL Anything uses blank nodes for expressing containers, but it can be also configured to mint URIs.

We now see an example of how maps are triplified.
Suppose having this map (serialised in JSON format)

```json
  { "name": "Micheal", "surname": "Jordan" }
```

Similarly to the previous example SPARQL Anything will create a container which identifies the collection (let’s call it _:jordan) and then associates this container with the value "Micheal" by means of the property xyz:name and the value "Jordan" by means of the property xyz:surname (as one would expect).

```
    _:jordan xyz:name "Micheal" .
    _:jordan xyz:surname "Jordan" .
```

Let’s analyze this example.
The name of the fields in the input map (name and surname) is concatenated with the prefix xyz: and the result is used for minting the properties of the container.
Of course, SPARQL Anything allows you to set a custom prefix for minting properties (we’ll see how in a minute).

Most file formats allow you to nest collections.
Let’s stick with JSON as an example.
Suppose having an array where each element is a JSON Object storing name and surname of a player

```json 
[
    {
        "name": "Micheal",
        "surname": "Jordan"
    },
    {
        "name":"Scottie",
        "surname": "Pippen"
    }
]
```

Let’s start from the inner container. We know how to triplify the first element of the array, and the second is similar to the first one.

```
    _:jordan xyz:name "Micheal" .
    _:jordan xyz:surname "Jordan" .
    
    _:pippen xyz:name "Scottie" .
    _:pippen xyz:surname "Pippen" .
```

Ok, great. Now let’s focus on the outer container.
SPARQL Anything creates a blank node for the outer container. Let’s call it _:players and associates it with the member of the array (_:jordan and _:pippen) by means of the container membership properties rdf:_1 and rdf:_2

```
    _:players rdf:_1 _:jordan .
    _:players rdf:_2 _:pippen .
```

SPARQL Anything also marks the outer container as root so you can distinguish it from the other containers.

```
    _:players rdf:type fx:root .
```

In particular cases a container can also have other types, but we will see such examples later on.

Not much to say. With these primitives we can port into RDF any file format. In fact, it can be proved that all the formats can be interpreted as a combination of these primitives.

## Triplifying any file format
Now, I am going to show you how common file formats are triplified. At the moment SPARQL Anything allows you to query: JSON, HTML, XML, CSV, YAML, Binary formats (e.g. BIN, PNG, JPEG), Text files (txt, docx and markdown), spreadsheets (XLS and XLSX) and Bibtex.

Once clear how lists and maps are triplified, understanding how formats are represented in RDF is straightforward. In fact, you can interpret any file as a composition of nested collections. I’m going to give you some examples.

In the previous section, we addressed JSON. In fact, it is easy to see that,  from a structural standpoint a JSON file can be seen as a composition of lists and maps.

Let’s look at CSV. We can see a CSV file as an ordered sequence of records where each record is an ordered sequence of values. Ordered sequences can be expressed as lists. Therefore a CSV is a list of lists.
Here you can see a simple example.

```
    Year,Make,Model
    1997,Ford,E350
    2000,Mercury,Cougar
```

```
[
    rdf:type fx:root ;
    rdf:_1  [
        rdf:_1 "Year" ;
        rdf:_2 "Make" ;
        rdf:_3 "Model" ;
    ],
    rdf:_2  [
        rdf:_1 "1997" ;
        rdf:_2 "Ford" ;
        rdf:_3 "E350" ;
    ],
    rdf:_3  [
        rdf:_1 "2000" ;
        rdf:_2 "Mercury" ;
        rdf:_3 "Cougar" ;
    ]
]
```

It is quite usual to interpret the first row of a CSV as headers for the following records. To do so you can set the parameter csv.headers=true and obtain the following result

```
[
    rdf:type fx:root ;
    rdf:_1  [
        xyz:Year "1997" ;
        xyz:Make "Ford" ;
        xyz:Model "E350" ;
    ],
    rdf:_2  [
        xyz:Year "2000" ;
        xyz:Make "Mercury" ;
        xyz:Model "Cougar" ;
    ]
]
```

Let’s look at XML and HTML. Although having crucial differences (e.g. case-sensitivity, optional closing tags etc.), XML and HTML can be classified as markup languages that organise data according to a hierarchical structure.
XML and HTML can be represented by combining: maps for associating attributes with the corresponding values; lists for specifying sequences of children of an element;
type for associating an element with the name of the tag.
Here you can see a simple example.

```
<PLAYERS>
	<PLAYER name="Micheal" surname="Jordan"/>
	<PLAYER name="Scottie" surname="Pippen"/>
</PLAYERS>
```



```
[ 
    a       fx:root , xyz:PLAYERS ;
    rdf:_1  [ a            xyz:PLAYER ;
        xyz:name     "Micheal" ;
        xyz:surname  "Jordan"
    ] ;
    rdf:_2  [ a            xyz:PLAYER ;
        xyz:name     "Scottie" ;
        xyz:surname  "Pippen"
    ]
] .
```

Here we can see another primitive used by SPARQL Anything for triplying file contents. Containers coming from XML documents can be typed by a URI which is the result of the concatenation of the prefix xyz: with the name of the tag the container corresponds to.

As I was saying there are plenty of triplifiers for other file formats but we don’t have time to dive into the details of those.  You can find additional information about the online documentation.

## Querying anything
Now that is clear how files are triplifiers, we can show you how to use SPARQL Anything to query any file.

### Installation
Before going further, to use SPARQL Anything you need to download and run the tool. Before running the tool please make sure that Java 11+ is correctly installed and configured on your machine.
Go to the release page https://github.com/SPARQL-Anything/sparql.anything/releases
- Download `sparql-anything-server-<version>.jar`
- And run it from your terminal. `java -jar sparql-anything-server-<version>.jar`
This command will start the SPARQL Anything server which is basically a Fuseki server enhanced with SPARQL Anything capabilities.
The GUI of the server can be accessed from the following link http://localhost:3000/sparql

### Overloading SERVICE clause

SPARQL Anything extends the SPARQL processors by overloading the SERVICE operator, as in the following example. 
Suppose having this JSON file as input (also available at https://sparql-anything.cc/example1.json) which contains the description of two famous tv series; and you want to select all tv series starring Courtey Cox.
This can be done with the following query

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star ?li "Courteney Cox" .
    }
}
```

Let’s focus on the service clause.

```
SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json>
```

This tells the SPARQL Anything server to triplify data available at the location https://sparql-anything.cc/example1.json (according to the triplification strategies we have seen before) and evaluate the basic graph pattern

```
?tvSeries xyz:name ?seriesName .
?tvSeries xyz:stars ?star .
?star ?li "Courteney Cox" .
```

over the triplified version of the JSON file.

In order to instruct the query processor to delegate the execution to SPARQL Anything, you can use the following URI-schema within SERVICE clauses.

```x-sparql-anything ':' ([option] ('=' [value])? ','?)+```

where x-sparql-anything is the URI scheme that must be present in order to delegate the processing to the SPARQL Anything engine. ([option] ('=' [value])? ','?)+ are a list of key value pairs used for passing options to triplifiers. 
The list of available options are reported [here](README.md#general-purpose-options)

For example, we instruct the SPARQL Anything triplifier to use a custom namespace for generated data.

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX myns: <http://example.org/mynamespace/>

SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:location=https://sparql-anything.cc/example1.json,namespace=http://example.org/mynamespace/> {
        ?tvSeries myns:name ?seriesName .
        ?tvSeries myns:stars ?star .
        ?star ?li "Courteney Cox" .
    }
}
```

A minimal URI that uses only the resource locator is also possible.

`x-sparql-anything ':' URL`

In this case the SPARQL Anything engine applies the default configurations to triplify data.
In a completely equivalent way, SPARQL Anything options can be also provided as basic graph pattern inside the SERVICE clause as follows

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>

SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:> {
        fx:properties fx:location "https://sparql-anything.cc/example1.json"
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star ?li "Courteney Cox" .
    }
}
```

Note that
The SERVICE URI scheme must be `x-sparql-anything:`.
Each triplification option to pass to the engine corresponds to a triple of the Basic Graph Pattern inside the SERVICE clause.
Such triples must have fx:properties as subject, fx:[OPTION-NAME] as predicate, and a literal or a variable as object.

## Constructing Knowledge Graphs
Now, I’m going to show you how to construct RDF knowledge graphs using SPARQL Anything. Suppose that you want to structure a TV Series Knowledge Graph by extracting data from the JSON we have seen before and this KG has to comply with schema.org ontology. With SPARQL Anything you can address this task in a single construct query.

Let us assume that the target KG should look like the following one

```
@prefix schema: <https://schema.org/> .
@prefix myns: <http://example.org/myns/> .

myns:Friends a schema:TVSeries ;
schema:name "Friends" ;
schema:genre myns:Comedy, myns:Romance ;
schema:inLanguage "English" ;
schema:creativeWorkStatus "Ended";
schema:startDate "1994-09-22" ;
schema:description "Follows the personal and professional lives of six twenty to thirty-something-year-old friends living in Manhattan ;" ;
schema:actor myns:Jennifer_Aniston , myns:Courtney_Cox, myns:Lisa_Kudrow, myns:Matt_LeBlanc, myns:Matthew_Perry, myns:David_Schwimmer .

myns:Cougar_Town a schema:TVSeries ;
schema:name "Cougar Town" ;
schema:genre myns:Comedy, myns:Romance ;
schema:inLanguage "English" ;
schema:creativeWorkStatus "Ended";
schema:startDate "2009-09-23" ;
schema:description "Jules is a recently divorced mother who has to face the unkind realities of dating in a world obsessed with beauty and youth. As she becomes older, she starts discovering herself." ;
schema:actor  myns:Courtney_Cox, myns:David_Arquette, myns:Bill_Lawrence, myns:Linda_Videtti_Figueiredo, myns:Blake_McCormick .

myns:David_Arquette a schema:Person ;
schema:name "David Arquette" .

myns:Bill_Lawrence a schema:Person ;
schema:name "Bill Lawrence" .

myns:Linda_Videtti_Figueiredo a schema:Person ;
schema:name "Linda Videtti Figueiredo" .

myns:Blake_McCormick a schema:Person ;
schema:name "Blake McCormick" .

myns:Jennifer_Aniston a schema:Person ;
schema:name "Jennifer Aniston" .

myns:Courtney_Cox a schema:Person ;
schema:name "Courtney Cox" .

myns:Lisa_Kudrow a schema:Person ;
schema:name "Lisa Kudrow" .

myns:Matt_LeBlanc a schema:Person ;
schema:name "Matt LeBlanc" .

myns:Matthew_Perry a schema:Person ;
schema:name "Matthew Perry" .

myns:David_Schwimmer a schema:Person ;
schema:name "David Schwimmer" .
```

This can be generated by the following query

```
PREFIX fx: <http://sparql.xyz/facade-x/ns/>
PREFIX schema: <http://schema.org/>
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX myns: <http://example.org/myns/>

CONSTRUCT {
    ?tvSeriesMyNs a schema:TVSeries .
    ?tvSeriesMyNs schema:name ?seriesName .
    ?tvSeriesMyNs schema:genre ?genreMyNs .
    ?tvSeriesMyNs schema:inLanguage ?language .
    ?tvSeriesMyNs schema:startDate ?premiered .
    ?tvSeriesMyNs schema:creativeWorkStatus ?status .
    ?tvSeriesMyNs schema:description ?summary .
    ?tvSeriesMyNs schema:actor ?starMyNs .
    ?starMyNs a schema:Person .
    ?starMyNs schema:name ?star .
} WHERE {
    SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {
    
        ?tvSeries xyz:name ?seriesName .
        BIND (IRI(CONCAT("http://example.org/myns/", REPLACE(?seriesName, " ", "_", "i"))) AS ?tvSeriesMyNs)
        
        ?tvSeries xyz:genres ?genreContainer .
        ?genreContainer fx:anySlot ?genre . 
        BIND (IRI(CONCAT("http://example.org/myns/", REPLACE(?genre, " ", "_", "i"))) AS ?genreMyNs)
        
        ?tvSeries xyz:language ?language .
        ?tvSeries xyz:premiered ?premiered .
        ?tvSeries xyz:status ?status .
        ?tvSeries xyz:summary ?summary .
        
        ?tvSeries xyz:stars ?starContainer .
        ?starContainer fx:anySlot ?star .
        BIND (IRI(CONCAT("http://example.org/myns/", REPLACE(?star, " ", "_", "i"))) AS ?starMyNs)
    }
}
```
