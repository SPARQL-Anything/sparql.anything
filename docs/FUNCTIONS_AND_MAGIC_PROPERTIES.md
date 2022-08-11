# Functions and magic properties

SPARQL Anything provides a number of magical functions and properties to facilitate the users in querying the sources and constructing knowledge graphs.



**NOTE**: SPARQL Anything is built on Apache Jena, see a list of supported functions on the [Apache Jena documentation](https://jena.apache.org/documentation/query/library-function.html).


| Name                                                                 | Function/Magic Property | Input                         | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|----------------------------------------------------------------------|-------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [fx:anySlot](#fxanySlot)                                             | Magic Property          | -                             | This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).                                                                                                                                                                                                                                                                                                                                                                              | 
| [fx:cardinal(?a)](#fxcardinal)                                       | Function                | Container membership property | `fx:cardinal(?a)` returns the corresponding cardinal integer from `?a` (`rdf:_24` -> `24`)                                                                                                                                                                                                                                                                                                                                                                                    |
| [fx:before(?a, ?b)](#fxbefore)                                       | Function                | Container membership property | `fx:before(?a, ?b)` returns `true` if `?a` and `?b` are container membership properties and `?a` is lower than `?b`, `false` otherwise                                                                                                                                                                                                                                                                                                                                        |
| [fx:after(?a, ?b)](#fxafter)                                         | Function                | Container membership property | `fx:after(?a, ?b)`  returns `true` if `?a` and `?b` are container membership properties and `?a` is higher than `?b`, `false` otherwise                                                                                                                                                                                                                                                                                                                                       |
| [fx:previous(?a)](#fxprevious)                                       | Function                | Container membership property | `fx:previous(?a)` returns the container membership property that preceeds `?a` (`rdf:_2` -> `rdf:_1`)                                                                                                                                                                                                                                                                                                                                                                         |
| [fx:next(?b)](#fxnext)                                               | Function                | Container membership property | `fx:next(?b)` returns the container membership property that succeedes `?b` (`rdf:_1` -> `rdf:_2`)                                                                                                                                                                                                                                                                                                                                                                            |
| [fx:forward(?a, ?b)](#fxforward)                                     | Function                | Container membership property | `fx:forward(?a, ?b)` returns the container membership property that follows `?a` of `?b` steps (`rdf:_2, 5` -> `rdf:_7`)                                                                                                                                                                                                                                                                                                                                                      |
| [fx:backward(?a, ?b)](#fxbackward)                                   | Function                | Container membership property | `fx:backward(?a, ?b)` returns the container membership property that preceeds `?a` of `?b` steps (`rdf:_24, 4` -> `rdf:_20`)                                                                                                                                                                                                                                                                                                                                                  |
| [fx:String.startsWith(?string)](#fxString.startsWith)                | Function                | String                        | `fx:String.startsWith` wraps [`java.lang.String.startsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                              |
| [fx:String.endsWith(?string)](#fxString.endsWith)                    | Function                | String                        | `fx:String.endsWith` wraps [`java.lang.String.endsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                  |
| [fx:String.indexOf(?string)](#fxString.indexOf)                      | Function                | String                        | `fx:String.indexOf` wraps [`java.lang.String.indexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                    |
| [fx:String.substring(?string)](#fxString.substring)                  | Function                | String                        | `fx:String.substring` wraps [`java.lang.String.substring`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                |
| [fx:String.toLowerCase(?string)](#fxString.toLowerCase)              | Function                | String                        | `fx:String.toLowerCase` wraps [`java.lang.String.toLowerCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                            |
| [fx:String.toUpperCase(?string)](#fxString.toUpperCase)              | Function                | String                        | `fx:String.toUpperCase` wraps [`java.lang.String.toUpperCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                            |
| [fx:String.trim(?string)](#fxString.trim)                            | Function                | String                        | `fx:String.trim` wraps [`java.lang.String.trim`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                          |
| [fxWordUtils.capitalize(?string)](#fxWordUtils.capitalize)           | Function                | String                        | `WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))                                                                                                                                                                                                                                                        |
| [fxWordUtils.capitalizeFully(?string)](#fxWordUtils.capitalizeFully) | Function                | String                        | `fx:WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))                                                                                                                                                                                                                                      |
| [fx:WordUtils.initials(?string)](#fxWordUtils.initials)              | Function                | String                        | `fx:WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))                                                                                                                                                                                                                                                           |
| [fx:WordUtils.swapCase(?string)](#fxWordUtils.swapCase)              | Function                | String                        | `fx:WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))                                                                                                                                                                                                                                                           |
| [fx:WordUtils.uncapitalize(?string)](#fxWordUtils.uncapitalize)      | Function                | String                        | `fx:WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))                                                                                                                                                                                                                                               |
| [fx:DigestUtils.md2Hex(?string)](#fxDigestUtils.md2Hex)              | Function                | String                        | `fx:DigestUtils.md2Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md2Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md2Hex-java.lang.String-)                                                                                                                                                                                                                                                   |
| [fx:DigestUtils.md5Hex(?string)](#fxDigestUtils.md5Hex)              | Function                | String                        | `fx:DigestUtils.md5Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md5Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md5Hex-java.lang.String-)                                                                                                                                                                                                                                                   |
| [fx:DigestUtils.sha1Hex(?string)](#fxDigestUtils.sha1Hex)            | Function                | String                        | `fx:DigestUtils.sha1Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha1Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha1Hex-java.lang.String-)                                                                                                                                                                                                                                                |
| [fx:DigestUtils.sha256Hex(?string)](#fxDigestUtils.sha256Hex)        | Function                | String                        | `fx:DigestUtils.sha256Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha256Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha256Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:DigestUtils.sha384Hex(?string)](#fxDigestUtils.sha384Hex)        | Function                | String                        | `fx:DigestUtils.sha384Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha384Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha384Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:DigestUtils.sha512Hex(?string)](#fxDigestUtils.sha512Hex)        | Function                | String                        | `fx:DigestUtils.sha512Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha512Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha512Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:URLEncoder.encode(?string)](#fxURLEncoder.encode)                | Function                | String                        | `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html#encode(java.lang.String,java.lang.String))                                                                                                                                                                                                                                                                                  |
| [fx:URLEncoder.decode(?string)](#fxURLEncoder.decode)                | Function                | String                        | `fx:URLEncoder.decode` wraps [`java.net.URLEncoder.decode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))                                                                                                                                                                                                                                                                                  |
| [fx:serial(?a ... ?n)](#fxserial)                                    | Function                | Any node                      | The function `fx:serial (?a ... ?n)` generates an incremental number using the arguments as reference counters. For example, calling `fx:serial("x")` two times will generate `1` and then `2`. Instead, calling `fx:serial(?x)` multiple times will generate sequential numbers for each value of `?x`.                                                                                                                                                                      |
| [fx:entity(?a ... ?n)](#fxentity)                                    | Function                | Any node                      | The function `fx:entity (?a ... ?n)` accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (`rdf:_1`,`rdf:_2`,...) are cast to numbers and then to strings (`"1","2"`).                                                                                                                                                                                                                                     |
| [fx:literal(?a, ?b)](#fxliteral)                                     | Function                | Any node                      | The function `fx:literal( ?a , ?b )` builds a literal from the string representation of `?a`, using `?b` either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).                                                                                                                                                                                                                                                                |
| [fx:bnode(?a)](#fxbnode)                                             | Function                | Any node                      | The function `fx:bnode( ?a) ` builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph. Apparently, the standard function `BNODE` does generate a new node for each query solution (see issue [#273](https://github.com/SPARQL-Anything/sparql.anything/issues/273) for an explanatory case). |


## Working with sequences

### fx:anySlot

The execution engine is sensible to the magic property ``fx:anySlot``.

This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).

#### Example

Query 

```
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,3]" ;
                  fx:media-type  "application/json" .
        ?s        fx:anySlot     ?slot
      }
  }
```

Result

```
-----------------------------------------------
| slot                                        |
===============================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> |
-----------------------------------------------
```



## Functions on container membership properties
The system supports the following functions on container membership properties (See [issue 78](https://github.com/SPARQL-Anything/sparql.anything/issues/78)):

### fx:cardinal

`fx:cardinal(?a)` returns the corresponding cardinal integer from `?a` (`rdf:_24` -> `24`)

#### Input

Container membership property

#### Example

```
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot (fx:cardinal(?p) AS ?cardinal)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,3]" ;
                  fx:media-type  "application/json" .
        ?s        ?p             ?slot
        FILTER ( ?p != rdf:type )
      }
  }
```

Result

```
----------------------------------------------------------
| slot                                        | cardinal |
==========================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | 1        |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | 2        |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | 3        |
----------------------------------------------------------
```
### fx:before 

`fx:before(?a, ?b)` returns `true` if `?a` and `?b` are container membership properties and `?a` is lower than `?b`, `false` otherwise

#### Input

Container membership properties

#### Example

```
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:before(?p1, ?p2) AS ?p1_before_p2) (fx:before(?p2, ?p1) AS ?p2_before_p1) (fx:before(?p1, ?p1) AS ?p1_before_p1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,3]" ;
                  fx:media-type  "application/json" .
        ?s        ?p1            1 ;
                  ?p2            2
      }
  }
```

Result

```
----------------------------------------------
| p1_before_p2 | p2_before_p1 | p1_before_p1 |
==============================================
| true         | false        | false        |
----------------------------------------------
```

### fx:after

`fx:after(?a, ?b)`  returns `true` if `?a` and `?b` are container membership properties and `?a` is higher than `?b`, `false` otherwise

#### Input

Container membership properties

#### Example

```
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:after(?p1, ?p2) AS ?p1_after_p2) (fx:after(?p2, ?p1) AS ?p2_after_p1) (fx:after(?p1, ?p1) AS ?p1_after_p1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,3]" ;
                  fx:media-type  "application/json" .
        ?s        ?p1            1 ;
                  ?p2            2
      }
  }
```

Result

```
-------------------------------------------
| p1_after_p2 | p2_after_p1 | p1_after_p1 |
===========================================
| false       | true        | false       |
-------------------------------------------
```

### fx:previous

`fx:previous(?a)` returns the container membership property that preceeds `?a` (`rdf:_2` -> `rdf:_1`)

#### Input

Container membership property

#### Example

```
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot ?p (fx:previous(?p) AS ?previous)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,3]" ;
                  fx:media-type  "application/json" .
        ?s        ?p             ?slot
        FILTER ( ?p != rdf:type )
      }
  }
```

Result

```
---------------------------------------------------------------------------------------------------------------------------------------------------
| slot                                        | p                                               | previous                                        |
===================================================================================================================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_0> |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> |
---------------------------------------------------------------------------------------------------------------------------------------------------
```

<!--
### 


#### Input


#### Example

```
```

Result

```
```
-->

<!--

- `fx:next(?b)` returns the container membership property that succeedes `?b` (`rdf:_1` -> `rdf:_2`)
- `fx:forward(?a, ?b)` returns the container membership property that follows `?a` of `?b` steps (`rdf:_2, 5` -> `rdf:_7`)
- `fx:backward(?a, ?b)` returns the container membership property that preceeds `?a` of `?b` steps (`rdf:_24, 4` -> `rdf:_20`)

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

- `fx:WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))
- `fx:WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))
- `fx:WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))
- `fx:WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))
- `fx:WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))

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
- `fx:URLEncoder.decode` wraps [`java.net.URLEncoder.decode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))

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

### The function `fx:bnode`
The function `fx:bnode( ?a) ` builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph. Apparently, the standard function `BNODE` does generate a new node for each query solution (see issue [#273](https://github.com/SPARQL-Anything/sparql.anything/issues/273) for an explanatory case).
-->