# Configuration

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
|from-archive|The filename of the resource to be triplified within an archive.|Any filename.|-|
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
|use-rdfs-member|It tells SPARQL Anything to use the (super)property rdfs:member instead of container membership properties (rdf:_1, rdf:_2 ...) |true/false|false|

\* It is mandatory to provide either `location`, `content`, or `command`.


## HTTP options
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
