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
fx:root ;
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
fx:root ;
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
[ a       fx:root , xyz:PLAYERS ;
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
