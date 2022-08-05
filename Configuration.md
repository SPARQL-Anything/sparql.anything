# Configuration

SPARQL Anything behaves as a standard SPARQL query engine. 
For example, the SPARQL Anything server will act as a virtual endpoint that can be queried exactly as a remote SPARQL endpoint.
In addition, SPARQL Anything provides a rich Command Line Interface (CLI). 
For information for how to run SPARQL Anything, please see the [quickstart](README.md#Quickstart) and [usage](README.md#usage) sections of the documentation.

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

| Option name                   | Description                                                                                                                                                                                                                                                                                                                                   | Valid Values                                                                                                                                                                          | Default Value                                                                                                                                        |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| [location](#location)*        | The URL of the data source.                                                                                                                                                                                                                                                                                                                   | Any valid URL or (absolute or relative) path of the file system.                                                                                                                      | \*                                                                                                                                                   |
| [content](#content)*          | The content to be transformed.                                                                                                                                                                                                                                                                                                                | Any valid literal.                                                                                                                                                                    | \*                                                                                                                                                   |
| [command](#command)*          | An external command line to be executed. The output is handled according to the option 'media-type'                                                                                                                                                                                                                                           | Any valid literal.                                                                                                                                                                    | \*                                                                                                                                                   |
| [from-archive](#from-archive) | The filename of the resource to be triplified within an archive.                                                                                                                                                                                                                                                                              | Any filename.                                                                                                                                                                         | No value                                                                                                                                             |
| [root](#root)                 | The IRI of generated root resource.                                                                                                                                                                                                                                                                                                           | Any valid IRI.                                                                                                                                                                        | location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set) |
| [media-type](#media-type)     | The media-type of the data source.                                                                                                                                                                                                                                                                                                            | Any valid [Media-Type](https://en.wikipedia.org/wiki/Media_type).  Supported media types are specified in the [pages dedicated to the supported formats](README.md#supported-formats) | No value (the media-type will be guessed from the the file extension)                                                                                |
| [namespace](#namespace)       | The namespace prefix for the properties that will be generated.                                                                                                                                                                                                                                                                               | Any valid namespace prefix.                                                                                                                                                           | http://sparql.xyz/facade-x/data/                                                                                                                     |
| blank-nodes                   | It tells sparql.anything to generate blank nodes or not.                                                                                                                                                                                                                                                                                      | true/false                                                                                                                                                                            | true                                                                                                                                                 |
| trim-strings                  | Trim all string literals.                                                                                                                                                                                                                                                                                                                     | true/false                                                                                                                                                                            | false                                                                                                                                                |
| null-string                   | Do not produce triples where the specificed string would be in the object position of the triple.                                                                                                                                                                                                                                             | any string                                                                                                                                                                            | not set                                                                                                                                              |
| triplifier                    | It forces sparql.anything to use a specific triplifier for transforming the data source                                                                                                                                                                                                                                                       | A canonical name of a Java class                                                                                                                                                      | No value                                                                                                                                             |
| charset                       | The charset of the data source.                                                                                                                                                                                                                                                                                                               | Any charset.                                                                                                                                                                          | UTF-8                                                                                                                                                |
| metadata                      | It tells sparql.anything to extract metadata from the data source and to store it in the named graph with URI &lt;http://sparql.xyz/facade-x/data/metadata&gt;                                                                                                                                                                                | true/false                                                                                                                                                                            | false                                                                                                                                                |
| ondisk                        | It tells sparql.anything to use an on disk graph (instead of the default in memory graph). The string should be a path to a directory where the on disk graph will be stored. Using an on disk graph is almost always slower (than using the default in memory graph) but with it you can triplify large files without running out of memory. | a path to a directory                                                                                                                                                                 | not set                                                                                                                                              |
| ondisk.reuse                  | When using an on disk graph, it tells sparql.anything to reuse the previous on disk graph.                                                                                                                                                                                                                                                    | true                                                                                                                                                                                  | not set                                                                                                                                              |
| strategy                      | The execution strategy. 0 = in memory, all triples; 1 = in memory, only triples matching any of the triple patterns in the where clause                                                                                                                                                                                                       | 0,1                                                                                                                                                                                   | 1                                                                                                                                                    |
| slice                         | The resources is sliced and the SPARQL query executed on each one of the parts. Supported by: CSV (row by row); JSON (when array slice by item, when json object requires `json.path`); XML (requires `xml.path`)                                                                                                                             | true/false                                                                                                                                                                            | false                                                                                                                                                |
| use-rdfs-member               | It tells SPARQL Anything to use the (super)property rdfs:member instead of container membership properties (rdf:_1, rdf:_2 ...)                                                                                                                                                                                                               | true/false                                                                                                                                                                            | false                                                                                                                                                |

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

Result

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

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

Result

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

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

Result

| seriesName    |
|---------------|
| "Cougar Town" |
| "Friends"     |

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

Result

```
-----
| c |
=====
| 4 |
-----
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

Result

```
-----
| c |
=====
| 4 |
-----
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

Result

```
------------
| nOfItems |
============
|    4     |
------------
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

Result

```turtle

@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root ;
  rdf:_1    [ rdf:_1  "Year" ;
              rdf:_2  "Make" ;
              rdf:_3  "Model" ;
              rdf:_4  "Description" ;
              rdf:_5  "Price"
            ] ;
  rdf:_2    [ rdf:_1  "1997" ;
              rdf:_2  "Ford" ;
              rdf:_3  "E350" ;
              rdf:_4  "ac, abs, moon" ;
              rdf:_5  "3000.00"
            ] ;
  rdf:_3    [ rdf:_1  "1999" ;
              rdf:_2  "Chevy" ;
              rdf:_3  "Venture \"Extended Edition\"" ;
              rdf:_4  "" ;
              rdf:_5  "4900.00"
            ] ;
  rdf:_4    [ rdf:_1  "1999" ;
              rdf:_2  "Chevy" ;
              rdf:_3  "Venture \"Extended Edition, Very Large\"" ;
              rdf:_4  "" ;
              rdf:_5  "5000.00"
            ] ;
  rdf:_5    [ rdf:_1  "1996" ;
              rdf:_2  "Jeep" ;
              rdf:_3  "Grand Cherokee" ;
              rdf:_4  "MUST SELL!\nair, moon roof, loaded" ;
              rdf:_5  "4799.00"
            ]
] .

[ rdf:type  fx:root ;
  rdf:_1    "this is a test"
] .



```

### root

The IRI of generated root resource.

#### Valid Values

Any valid IRI.

#### Default Value

location + '#' (in case of location argument is set) or 'http://sparql.xyz/facade-x/data/' + md5Hex(content) + '#' (in case of content argument set)

#### Examples

##### UC1: Set the root of the Facade-X model generated from the JSON Object {"name":"Vincent", "surname": "Vega"} as http://example.org/myRoot

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content      "{\"name\":\"Vincent\", \"surname\": \"Vega\"}" ;
                  fx:media-type   "application/json" ;
                  fx:root         "http://example.org/myRoot" ;
                  fx:blank-nodes  false .
        ?s        ?p              ?o
      }
  }
```

Result

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .

<http://example.org/myRoot>
        a       fx:root ;
        <http://sparql.xyz/facade-x/data/name>
                "Vincent" ;
        <http://sparql.xyz/facade-x/data/surname>
                "Vega" .
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

###  media-type

The media-type of the data source.

#### Valid Values

Any valid [Media-Type](https://en.wikipedia.org/wiki/Media_type).  Supported media types are specified in the [pages dedicated to the supported formats](README.md#supported-formats)

#### Default Value

No value (the media-type will be guessed from the file extension).

#### Examples

##### UC1: Enforcing media-type for content string to count the number of elements in the JSON Array ["one", "two", "three", "four"]


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

Result

```
-----
| c |
=====
| 4 |
-----
```

### namespace

The namespace prefix for the properties that will be generated.

#### Valid Values

Any valid namespace prefix.

#### Default Value

http://sparql.xyz/facade-x/data/

#### Examples

##### UC1: Set the namespace prefix to http://example.org/myNamespace/ for the properties generated from the JSON Object {"name":"Vincent", "surname": "Vega"}

```

PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "{\"name\":\"Vincent\", \"surname\": \"Vega\"}" ;
                  fx:media-type  "application/json" ;
                  fx:namespace   "http://example.org/myNamespace/" .
        ?s        ?p             ?o
      }
  }

```

Result

```
@prefix fx: <http://sparql.xyz/facade-x/ns/> .

[ a       fx:root ;
  <http://example.org/myNamespace/name>
          "Vincent" ;
  <http://example.org/myNamespace/surname>
          "Vega"
] .
```

### blank-nodes

It tells SPARQL Anything to generate blank nodes or not.

#### Valid Values

`true/false`

#### Default Value

true

#### Examples

##### UC1: Transform the JSON Object {"name":"Vincent", "surname": "Vega"} into RDF without using blank nodes

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content      "{\"name\":\"Vincent\", \"surname\": \"Vega\"}" ;
                  fx:media-type   "application/json" ;
                  fx:blank-nodes  false .
        ?s        ?p              ?o
      }
  }
```

Result

```

@prefix fx: <http://sparql.xyz/facade-x/ns/> .

<http://sparql.xyz/facade-x/data/6ab8ac25064e6d5ab7d8d7b855fddfa1#>
        a       fx:root ;
        <http://sparql.xyz/facade-x/data/name>
                "Vincent" ;
        <http://sparql.xyz/facade-x/data/surname>
                "Vega" .
```

<!--


#### Valid Values 


#### Default Value


#### Examples

##### UC1:

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
