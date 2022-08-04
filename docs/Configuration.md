# Configuration

SPARQL Anything will act as a virtual endpoint that can be queried exactly as a remote SPARQL endpoint.

## Passing triplification options via SERVICE IRI

In order to instruct the query processor to delegate the execution to SPARQL Anything, you can use the  following IRI-schema within SERVICE clauses.

```
x-sparql-anything ':' ([option] ('=' [value])? ','?)+
```

A minimal URI that uses only the resource locator is also possible.

```
x-sparql-anything ':' URL
```

In this case SPARQL Anything guesses the data source type from the file extension.


## Passing triplification options via Basic Graph Pattern

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

## General purpose options

### Summary

| Option name                   | Description                                                                                                                                                                                                                                                                                                                                   | Valid Values                                                                                                                                                                                                                                                 | Default Value                                                                                                                                        |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| [location](#location)*        | The URL of the data source.                                                                                                                                                                                                                                                                                                                   | Any valid URL or (absolute or relative) path of the file system.                                                                                                                                                                                             | -                                                                                                                                                    |
| [content](#content)*          | The content to be transformed.                                                                                                                                                                                                                                                                                                                | Any valid literal.                                                                                                                                                                                                                                           | -                                                                                                                                                    |
| [command](#command)*          | An external command line to be executed. The output is handled according to the option 'media-type'                                                                                                                                                                                                                                           | Any valid literal.                                                                                                                                                                                                                                           | -                                                                                                                                                    |
| [from-archive](#from-archive) | The filename of the resource to be triplified within an archive.                                                                                                                                                                                                                                                                              | Any filename.                                                                                                                                                                                                                                                | No value                                                                                                                                             |
| [root](#root)                 | The IRI of generated root resource.                                                                                                                                                                                                                                                                                                           | Any valid IRI.                                                                                                                                                                                                                                               | location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set) |
| media-type                    | The media-type of the data source.                                                                                                                                                                                                                                                                                                            | Any valid [Media-Type](https://en.wikipedia.org/wiki/Media_type). Supported media-types: application/xml, image/png, text/html, application/octet-stream, application/json, image/jpeg, image/tiff, image/bmp, text/csv, image/vnd.microsoft.icon,text/plain | No value (the media-type will be guessed from the the file extension)                                                                                |
| namespace                     | The namespace prefix for the properties that will be generated.                                                                                                                                                                                                                                                                               | Any valid namespace prefix.                                                                                                                                                                                                                                  | http://sparql.xyz/facade-x/data/                                                                                                                     |
| blank-nodes                   | It tells sparql.anything to generate blank nodes or not.                                                                                                                                                                                                                                                                                      | true/false                                                                                                                                                                                                                                                   | true                                                                                                                                                 |
| trim-strings                  | Trim all string literals.                                                                                                                                                                                                                                                                                                                     | true/false                                                                                                                                                                                                                                                   | false                                                                                                                                                |
| null-string                   | Do not produce triples where the specificed string would be in the object position of the triple.                                                                                                                                                                                                                                             | any string                                                                                                                                                                                                                                                   | not set                                                                                                                                              |
| triplifier                    | It forces sparql.anything to use a specific triplifier for transforming the data source                                                                                                                                                                                                                                                       | A canonical name of a Java class                                                                                                                                                                                                                             | No value                                                                                                                                             |
| charset                       | The charset of the data source.                                                                                                                                                                                                                                                                                                               | Any charset.                                                                                                                                                                                                                                                 | UTF-8                                                                                                                                                |
| metadata                      | It tells sparql.anything to extract metadata from the data source and to store it in the named graph with URI &lt;http://sparql.xyz/facade-x/data/metadata&gt;                                                                                                                                                                                | true/false                                                                                                                                                                                                                                                   | false                                                                                                                                                |
| ondisk                        | It tells sparql.anything to use an on disk graph (instead of the default in memory graph). The string should be a path to a directory where the on disk graph will be stored. Using an on disk graph is almost always slower (than using the default in memory graph) but with it you can triplify large files without running out of memory. | a path to a directory                                                                                                                                                                                                                                        | not set                                                                                                                                              |
| ondisk.reuse                  | When using an on disk graph, it tells sparql.anything to reuse the previous on disk graph.                                                                                                                                                                                                                                                    | true                                                                                                                                                                                                                                                         | not set                                                                                                                                              |
| strategy                      | The execution strategy. 0 = in memory, all triples; 1 = in memory, only triples matching any of the triple patterns in the where clause                                                                                                                                                                                                       | 0,1                                                                                                                                                                                                                                                          | 1                                                                                                                                                    |
| slice                         | The resources is sliced and the SPARQL query executed on each one of the parts. Supported by: CSV (row by row); JSON (when array slice by item, when json object requires `json.path`); XML (requires `xml.path`)                                                                                                                             | true/false                                                                                                                                                                                                                                                   | false                                                                                                                                                |
| use-rdfs-member               | It tells SPARQL Anything to use the (super)property rdfs:member instead of container membership properties (rdf:_1, rdf:_2 ...)                                                                                                                                                                                                               | true/false                                                                                                                                                                                                                                                   | false                                                                                                                                                |

\* It is mandatory to provide either `location`, `content`, or `command`.

### location

`location` tells the SPARQL Anything engine the URL of the input resource.

#### Valid Values

Any valid URL or (absolute or relative) path of the file system.

#### Default value

It is mandatory to provide either `location`, `content`, or `command`.

#### Examples

##### UC1: Retrieving from https://sparql-anything.cc/example1.json the names of the TV Series starring Courtney Cox.

```
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

##### UC2: Retrieving from /absolute/path/to/example1.json the names of the TV Series starring Courtney Cox.

```
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>
SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:/absolute/path/to/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }
}
```

##### UC2: Retrieving from relative/path/to/example1.json the names of the TV Series starring Courtney Cox.

```
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fx: <http://sparql.xyz/facade-x/ns/>
SELECT ?seriesName
WHERE {
    SERVICE <x-sparql-anything:relative/path/to/example1.json> {
        ?tvSeries xyz:name ?seriesName .
        ?tvSeries xyz:stars ?star .
        ?star fx:anySlot "Courteney Cox" .
    }
}
```

### content

The content to be transformed.

**Note**: if the `media-type` is not provided, the content is interpreted as [plain text](formats/TXT.md). 

#### Valid Values

Any valid literal.

#### Default Value

It is mandatory to provide either `location`, `content`, or `command`.

#### Examples

##### UC1: Count the number of items in the words in "one,two,three"

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  (count(*) AS ?c)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content    "one,two,tree" ;
                  fx:txt.split  "," .
        ?s        fx:anySlot    ?o
      }
  }
```


##### UC2: Count the number of items in the JSON Array `["one","two","three"]`

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  (count(*) AS ?c)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[\"one\",\"two\",\"three\", \"four\"]" ;
                  fx:media-type  "application/json" .
        ?s        fx:anySlot     ?o
      }
  }
```

### command

An external command line to be executed. 

The output is handled according to the option 'media-type'. 
If the 'media-type' is not provided, the output is interpreted as [plain text](formats/TXT.md).

**Note**: Don't use  double quotes to delimit command arguments.

For example, if you want to execute the command ``echo "[\"one\",\"two\",\"three\", \"four\"]"`` just provide ``echo [\"one\",\"two\",\"three\", \"four\"]`` as option (see example below).

#### Valid Values 

Any valid command line.

#### Default Value

It is mandatory to provide either `location`, `content`, or `command`.

#### Examples

##### UC1: Count the number of items in the JSON Array generated by the command  `echo "[\"one\",\"two\",\"three\", \"four\"]"`

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  (COUNT(?o) AS ?nOfItems)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:command     "echo [\"one\",\"two\",\"three\", \"four\"]" ;
                  fx:media-type  "application/json" .
        ?s        fx:anySlot     ?o
      }
  }
```

### from-archive

The filename of the resource to be triplified within an archive.

#### Valid Values

Any filename

#### Default Value

No Value

#### Examples

##### UC1: Select and triplify only .csv and .txt files within the archive available at https://sparql-anything.cc/examples/example.tar

See also [Archive](formats/Archive.md)

###### Query

```
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

CONSTRUCT 
  { 
    ?s1 ?p1 ?o1 .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>
      { fx:properties
                  fx:archive.matches  ".*txt|.*csv" .
        ?s        fx:anySlot          ?file1
        SERVICE <x-sparql-anything:>
          { fx:properties
                      fx:location      ?file1 ;
                      fx:from-archive  "https://sparql-anything.cc/examples/example.tar" .
            ?s1       ?p1              ?o1
          }
      }
  }

```

### root

The IRI of generated root resource.

#### Valid Values

Any valid IRI.

#### Default Value

location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set)

#### Examples

##### UC1: Set the root of the Facade-X model generated from https://sparql-anything.cc/example1.json as http://example.org/myRoot

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:location     "https://sparql-anything.cc/example1.json" ;
                  fx:root         "http://example.org/myRoot" ;
                  fx:blank-nodes  false .
        ?s        ?p              ?o
      }
  }
```

Result

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .

<http://example.org/myRoot/_2/stars>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Courteney Cox" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                "David Arquette" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                "Bill Lawrence" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                "Linda Videtti Figueiredo" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                "Blake McCormick" .

<http://example.org/myRoot>
        a       fx:root ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                <http://example.org/myRoot/_1> ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                <http://example.org/myRoot/_2> .

<http://example.org/myRoot/_2/genres>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Comedy" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                "Romance" .

<http://example.org/myRoot/_1>
        <http://sparql.xyz/facade-x/data/genres>
                <http://example.org/myRoot/_1/genres> ;
        <http://sparql.xyz/facade-x/data/language>
                "English" ;
        <http://sparql.xyz/facade-x/data/name>
                "Friends" ;
        <http://sparql.xyz/facade-x/data/premiered>
                "1994-09-22" ;
        <http://sparql.xyz/facade-x/data/stars>
                <http://example.org/myRoot/_1/stars> ;
        <http://sparql.xyz/facade-x/data/status>
                "Ended" ;
        <http://sparql.xyz/facade-x/data/summary>
                "Follows the personal and professional lives of six twenty to thirty-something-year-old friends living in Manhattan." .

<http://example.org/myRoot/_1/genres>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Comedy" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                "Romance" .

<http://example.org/myRoot/_1/stars>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1>
                "Jennifer Aniston" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2>
                "Courteney Cox" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3>
                "Lisa Kudrow" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4>
                "Matt LeBlanc" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5>
                "Matthew Perry" ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6>
                "David Schwimmer" .

<http://example.org/myRoot/_2>
        <http://sparql.xyz/facade-x/data/genres>
                <http://example.org/myRoot/_2/genres> ;
        <http://sparql.xyz/facade-x/data/language>
                "English" ;
        <http://sparql.xyz/facade-x/data/name>
                "Cougar Town" ;
        <http://sparql.xyz/facade-x/data/premiered>
                "2009-09-23" ;
        <http://sparql.xyz/facade-x/data/stars>
                <http://example.org/myRoot/_2/stars> ;
        <http://sparql.xyz/facade-x/data/status>
                "Ended" ;
        <http://sparql.xyz/facade-x/data/summary>
                "Jules is a recently divorced mother who has to face the unkind realities of dating in a world obsessed with beauty and youth. As she becomes older, she starts discovering herself." .
```

##### UC2: Set the root of the Facade-X model generated from the string "Hello World!" as http://example.org/myRoot

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content      "Hello World!" ;
                  fx:root         "http://example.org/myRoot" ;
                  fx:blank-nodes  false .
        ?s        ?p              ?o
      }
  }
```

Result

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .

<http://example.org/myRoot> a fx:root ;
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> "Hello World!" .
```

**Note**: blank-nodes=false is needed for generating named entities instead of blank nodes.

<!--
### 


#### Valid Values 


#### Default Value


#### Examples

#### UC1:

```

```
-->



## HTTP options
SPARQL Anything relies on Apache Commons HTTP for HTTP connections.

| Option name                     | Description                                                                                                                                                | Valid Values | Default Value |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------|---------------|
| http.client.*                   | Calls methods on the HTTPClient Java object. E.g. `http.client.useSystemProperties=false` means to avoid inheriting Java system properties (Default 'yes') |
| http.client.useSystemProperties | Use Java System Properties to configure the HTTP Client.                                                                                                   | true/false   | true          |
| http.header.*                   | To add headers to the HTTP request. E.g. `http.header.accept=application/json`                                                                             |||
| http.query.*                    | To add parameters to the query string. E.g. `http.query.var=value` or `http.query.var.1=value` to add more variable of the same name                       |||
| http.form.*                     | To add parameters to the POST content. E.g. `http.form.var=value` or `http.form.var.1=value` to add more variable of the same name                         |||
| http.method                     | HTTP Method                                                                                                                                                | GET,POST,... | GET           |
| http.payload                    | Sets the payload of the request                                                                                                                            |||
| http.protocol                   | Protocol                                                                                                                                                   | 0.9,1.0,1.1  | 1.1           |
| http.auth.user                  | Authentication: user name                                                                                                                                  |||
| http.auth.password              | Authentication: password                                                                                                                                   |||
| http.redirect                   | Follow redirect?                                                                                                                                           | true,false   | true          |
