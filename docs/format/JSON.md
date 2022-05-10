# JSON


## Default transformation

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
