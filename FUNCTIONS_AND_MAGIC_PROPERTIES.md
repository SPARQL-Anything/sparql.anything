# Functions and magic properties

SPARQL Anything provides a number of magical functions and properties to facilitate the users in querying the sources and constructing knowledge graphs.



**NOTE**: SPARQL Anything is built on Apache Jena, see a list of supported functions on the [Apache Jena documentation](https://jena.apache.org/documentation/query/library-function.html).


| Name                                                                                 | Function/Magic Property | Input                                  | Output                        | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|--------------------------------------------------------------------------------------|-------------------------|----------------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [fx:anySlot](#fxanyslot)                                                             | Magic Property          | -                                      | -                             | This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).                                                                                                                                                                                                                                                                                                                                                                              | 
| [fx:cardinal(?a)](#fxcardinal)                                                       | Function                | Container membership property          | Integer                       | `fx:cardinal(?a)` returns the corresponding cardinal integer from `?a` (`rdf:_24` -> `24`)                                                                                                                                                                                                                                                                                                                                                                                    |
| [fx:isContainerMembershipProperty(?p)](#fxiscontainermembershipproperty)             | Function                | Container membership property          | Boolean                       | `fx:isContainerMembershipProperty(?p)` returns true if the node passed as parameter is a container membership property (`rdf:_24` -> `true`)                                                                                                                                                                                                                                                                                                                                  |
| [fx:before(?a, ?b)](#fxbefore)                                                       | Function                | Container membership properties        | Boolean                       | `fx:before(?a, ?b)` returns `true` if `?a` and `?b` are container membership properties and `?a` is lower than `?b`, `false` otherwise                                                                                                                                                                                                                                                                                                                                        |
| [fx:after(?a, ?b)](#fxafter)                                                         | Function                | Container membership property          | Boolean                       | `fx:after(?a, ?b)`  returns `true` if `?a` and `?b` are container membership properties and `?a` is higher than `?b`, `false` otherwise                                                                                                                                                                                                                                                                                                                                       |
| [fx:previous(?a)](#fxprevious)                                                       | Function                | Container membership property          | Container membership property | `fx:previous(?a)` returns the container membership property that preceeds `?a` (`rdf:_2` -> `rdf:_1`)                                                                                                                                                                                                                                                                                                                                                                         |
| [fx:next(?b)](#fxnext)                                                               | Function                | Container membership property          | Container membership property | `fx:next(?b)` returns the container membership property that succeedes `?b` (`rdf:_1` -> `rdf:_2`)                                                                                                                                                                                                                                                                                                                                                                            |
| [fx:forward(?a, ?b)](#fxforward)                                                     | Function                | Container membership property, Integer | Container membership property | `fx:forward(?a, ?b)` returns the container membership property that follows `?a` of `?b` steps (`rdf:_2, 5` -> `rdf:_7`)                                                                                                                                                                                                                                                                                                                                                      |
| [fx:backward(?a, ?b)](#fxbackward)                                                   | Function                | Container membership property, Integer | Container membership property | `fx:backward(?a, ?b)` returns the container membership property that preceeds `?a` of `?b` steps (`rdf:_24, 4` -> `rdf:_20`)                                                                                                                                                                                                                                                                                                                                                  |
| [fx:String.startsWith(?stringA, ?stringB)](#fxstringstartswith)                      | Function                | String, String                         | Boolean                       | `fx:String.startsWith` wraps [`java.lang.String.startsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                              |
| [fx:String.endsWith(?stringA, ?stringB)](#fxstringendswith)                          | Function                | String, String                         | Boolean                       | `fx:String.endsWith` wraps [`java.lang.String.endsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                  |
| [fx:String.indexOf(?stringA, ?stringB)](#fxstringindexof)                            | Function                | String, String                         | Integer                       | `fx:String.indexOf` wraps [`java.lang.String.indexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                    |
| [fx:String.lastIndexOf(?stringA, ?stringB)](#fxstringlastindexof)                    | Function                | String, String                         | Integer                       | `fx:String.lastIndexOf` wraps [`java.lang.String.lastIndexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                            |
| [fx:String.substring(?string)](#fxstringsubstring)                                   | Function                | String, Integer, (Integer?)            | String                        | `fx:String.substring` wraps [`java.lang.String.substring`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                |
| [fx:String.toLowerCase(?string)](#fxstringtolowercase)                               | Function                | String                                 | String                        | `fx:String.toLowerCase` wraps [`java.lang.String.toLowerCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                            |
| [fx:String.toUpperCase(?string)](#fxstringtouppercase)                               | Function                | String                                 | String                        | `fx:String.toUpperCase` wraps [`java.lang.String.toUpperCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                            |
| [fx:String.replace(?string, ?characterA, ?characterB)](#fxstringreplace)             | Function                | String, Character, Character           | String                        | `fx:String.replace` wraps [`java.lang.String.replace`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#replace(java.lang.CharSequence,java.lang.CharSequence))                                                                                                                                                                                                                                                                             |
| [fx:String.trim(?string)](#fxstringtrim)                                             | Function                | String                                 | String                        | `fx:String.trim` wraps [`java.lang.String.trim`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)                                                                                                                                                                                                                                                                                                                                          |
| [fx:String.stripLeading(?string)](#fxstringstripleading)                             | Function                | String                                 | String                        | `fx:String.stripLeading` wraps [`java.lang.String.stripLeading`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#stripLeading())                                                                                                                                                                                                                                                                                                           |
| [fx:String.stripTrailing(?string)](#fxstringstriptrailing)                           | Function                | String                                 | String                        | `fx:String.stripTrailing` wraps [`java.lang.String.stripTrailing`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#stripTrailing())                                                                                                                                                                                                                                                                                                        |
| [fx:String.removeTags(?string)](#fxstringremovetags)                                 | Function                | String                                 | String                        | `fx:String.removeTags`  removes the XML tags from the input string                                                                                                                                                                                                                                                                                                                                                                                                            |
| [fxWordUtils.capitalize(?string)](#fxwordutilscapitalize)                            | Function                | String                                 | String                        | `WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))                                                                                                                                                                                                                                                        |
| [fxWordUtils.capitalizeFully(?string)](#fxwordutilscapitalizefully)                  | Function                | String                                 | String                        | `fx:WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))                                                                                                                                                                                                                                      |
| [fx:WordUtils.initials(?string)](#fxwordutilsinitials)                               | Function                | String                                 | String                        | `fx:WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))                                                                                                                                                                                                                                                           |
| [fx:WordUtils.swapCase(?string)](#fxwordutilsswapcase)                               | Function                | String                                 | String                        | `fx:WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))                                                                                                                                                                                                                                                           |
| [fx:WordUtils.uncapitalize(?string)](#fxwordutilsuncapitalize)                       | Function                | String                                 | String                        | `fx:WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))                                                                                                                                                                                                                                               |
| [fx:DigestUtils.md2Hex(?string)](#fxdigestutilsmd2hex)                               | Function                | String                                 | String                        | `fx:DigestUtils.md2Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md2Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md2Hex-java.lang.String-)                                                                                                                                                                                                                                                   |
| [fx:DigestUtils.md5Hex(?string)](#fxdigestutilsmd5hex)                               | Function                | String                                 | String                        | `fx:DigestUtils.md5Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md5Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md5Hex-java.lang.String-)                                                                                                                                                                                                                                                   |
| [fx:DigestUtils.sha1Hex(?string)](#fxdigestutilssha1hex)                             | Function                | String                                 | String                        | `fx:DigestUtils.sha1Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha1Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha1Hex-java.lang.String-)                                                                                                                                                                                                                                                |
| [fx:DigestUtils.sha256Hex(?string)](#fxdigestutilssha256hex)                         | Function                | String                                 | String                        | `fx:DigestUtils.sha256Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha256Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha256Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:DigestUtils.sha384Hex(?string)](#fxdigestutilssha384hex)                         | Function                | String                                 | String                        | `fx:DigestUtils.sha384Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha384Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha384Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:DigestUtils.sha512Hex(?string)](#fxdigestutilssha512hex)                         | Function                | String                                 | String                        | `fx:DigestUtils.sha512Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha512Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha512Hex-java.lang.String-)                                                                                                                                                                                                                                          |
| [fx:URLEncoder.encode(?string)](#fxurlencoderencode)                                 | Function                | String, String                         | String                        | `fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html#encode(java.lang.String,java.lang.String))                                                                                                                                                                                                                                                                                  |
| [fx:URLDecoder.decode(?string)](#fxurldecoderdecode)                                 | Function                | String, String                         | String                        | `fx:URLDecoder.decode` wraps [`java.net.URLDecoder.decode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))                                                                                                                                                                                                                                                                                  |
| [fx:serial(?a ... ?n)](#fxserial)                                                    | Function                | Any sequence of nodes                  | Integer                       | The function `fx:serial (?a ... ?n)` generates an incremental number using the arguments as reference counters. For example, calling `fx:serial("x")` two times will generate `1` and then `2`. Instead, calling `fx:serial(?x)` multiple times will generate sequential numbers for each value of `?x`.                                                                                                                                                                      |
| [fx:entity(?a ... ?n)](#fxentity)                                                    | Function                | Any sequence of nodes                  | URI node                      | The function `fx:entity (?a ... ?n)` accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (`rdf:_1`,`rdf:_2`,...) are cast to numbers and then to strings (`"1","2"`).                                                                                                                                                                                                                                     |
| [fx:literal(?a, ?b)](#fxliteral)                                                     | Function                | String, (URI or language code)         | Literal node                  | The function `fx:literal( ?a , ?b )` builds a literal from the string representation of `?a`, using `?b` either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).                                                                                                                                                                                                                                                                |
| [fx:bnode(?a)](#fxbnode)                                                             | Function                | Any node                               | Blank node                    | The function `fx:bnode( ?a) ` builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph. Apparently, the standard function `BNODE` does generate a new node for each query solution (see issue [#273](https://github.com/SPARQL-Anything/sparql.anything/issues/273) for an explanatory case). |
| [fx:LevenshteinDistance(?n1, ?n2)](#fxlevenshteindistance)                           | Function                | String, String           | Integer                       | The function `fx:LevenshteinDistance(?n1, ?n2) `  computes the Levenshtein Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                                                           |
| [fx:CosineDistance(?n1, ?n2)](#fxcosinedistance)                                     | Function                | String, String           | Double                        | The function `fx:CosineDistance(?n1, ?n2) `  computes the Cosine Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                                                                     |
| [fx:JaccardDistance(?n1, ?n2)](#fxjaccarddistance)                                   | Function                | String, String           | Double                        | The function `fx:JaccardDistance(?n1, ?n2) `  computes the Jaccard Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                                                                   |
| [fx:JaroWinklerDistance(?n1, ?n2)](#fxjarowinklerdistance)                           | Function                | String, String           | Double                        | The function `fx:JaroWinklerDistance(?n1, ?n2) `  computes the Jaro-Winkler Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                                                          |
| [fx:LongestCommonSubsequenceDistance(?n1, ?n2)](#fxlongestcommonsubsequencedistance) | Function                | String, String           | Integer                       | The function `fx:LongestCommonSubsequenceDistance(?n1, ?n2) `  computes the Longest Common Subsequence Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                               |
| [fx:HammingDistance(?n1, ?n2)](#fxhammingdistance)                                   | Function                | String, String           | Integer                       | The function `fx:HammingDistance(?n1, ?n2) `  computes the Hamming Distance between ?n1 and ?n2 (see #182).                                                                                                                                                                                                                                                                                                                                                                   |
| [fx:QGramDistance(?n1, ?n2)](#fxqgramdistance)                                       | Function                | String, String           | Double                        | The function `fx:QGramDistance(?n1, ?n2) `  computes the QGram Distance between ?n1 and ?n2 (see #394).                                                                                                                                                                                                                                                                                                                                                                       |


## Working with sequences

### fx:anySlot

The execution engine is sensible to the magic property ``fx:anySlot``.

This property matches the RDF container membership properties (e.g. ``rdf:_1``, ``rdf:_2`` ...).

#### Example

Query 

```sparql
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

#### Output 

Integer

#### Example

```sparql
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


### fx:isContainerMembershipProperty

`fx:isContainerMembershipProperty(?p)` returns true if the node passed as parameter is a container membership property (`rdf:_24` -> `true`)

#### Input

Container membership property

#### Output

Boolean

#### Example

```sparql
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  ?resultTrue ?resultFalse
WHERE
  { BIND(fx:isContainerMembershipProperty(rdf:_42) AS ?resultTrue)
    BIND(fx:isContainerMembershipProperty(42) AS ?resultFalse)
  }
```

Result

```
----------------------------
| resultTrue | resultFalse |
============================
| true       | false       |
----------------------------
```

### fx:before 

`fx:before(?a, ?b)` returns `true` if `?a` and `?b` are container membership properties and `?a` is lower than `?b`, `false` otherwise

#### Input

Container membership properties

#### Output

Boolean

#### Example

```sparql
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

#### Output

Boolean

#### Example

```sparql
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

#### Output

Container membership property

#### Example

```sparql
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
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> |                                                 |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> |
---------------------------------------------------------------------------------------------------------------------------------------------------
```


### fx:next

`fx:next(?b)` returns the container membership property that succeedes `?b` (`rdf:_1` -> `rdf:_2`)

#### Input

Container membership property

#### Output

Container membership property

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot ?p (fx:next(?p) AS ?next)
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
| slot                                        | p                                               | next                                            |
===================================================================================================================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> |
---------------------------------------------------------------------------------------------------------------------------------------------------
```

### fx:forward

`fx:forward(?a, ?b)` returns the container membership property that follows `?a` of `?b` steps (`rdf:_2, 5` -> `rdf:_7`)


#### Input

A container membership property and an integer

#### Output

Container membership property

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot ?p (fx:forward(?p, 3) AS ?forward)
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
| slot                                        | p                                               | forward                                         |
===================================================================================================================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> |
---------------------------------------------------------------------------------------------------------------------------------------------------
```


### fx:backward

`fx:backward(?a, ?b)` returns the container membership property that preceeds `?a` of `?b` steps (`rdf:_24, 4` -> `rdf:_20`)

#### Input

A container membership property and an integer

#### Output

Container membership property

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?slot ?p (fx:backward(?p, 2) AS ?backward)
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
| slot                                        | p                                               | backward                                        |
===================================================================================================================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> |                                                 |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> |                                                 |
| "3"^^<http://www.w3.org/2001/XMLSchema#int> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> |
---------------------------------------------------------------------------------------------------------------------------------------------------
```

## Working with strings
The system supports the following functions for string manipulation (See [issue 104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):

### fx:String.startsWith

`fx:String.startsWith` wraps [`java.lang.String.startsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String

#### Output

Boolean 

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.startsWith(?string, "this") AS ?result1) (fx:String.startsWith(?string, "This") AS ?result2)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "this is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
---------------------
| result1 | result2 |
=====================
| true    | false   |
---------------------
```

### fx:String.endsWith

`fx:String.endsWith` wraps [`java.lang.String.endsWith`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String, String

#### Output

Boolean

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.endsWith(?string, "test") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "this is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
-----------
| result1 |
===========
| true    |
-----------
```

### fx:String.indexOf

`fx:String.indexOf` wraps [`java.lang.String.indexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String, String

#### Output

Integer

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.indexOf(?string, "i") AS ?result1) (fx:String.indexOf(?string, "test") AS ?result2)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "this is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
---------------------
| result1 | result2 |
=====================
| 2       | 10      |
---------------------
```

### fx:String.lastIndexOf

`fx:String.lastIndexOf` wraps [`java.lang.String.lastIndexOf`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String, String

#### Output

Integer

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.lastIndexOf(?string, "b") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "bob" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
-----------
| result1 |
===========
| 2       |
-----------
```

### fx:String.substring

`fx:String.substring` wraps [`java.lang.String.substring`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String, Integer, (Integer?)

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.substring(?string, 10) AS ?result1) (fx:String.substring(?string, 5, 7) AS ?result2)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "this is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
---------------------
| result1 | result2 |
=====================
| "test"  | "is"    |
---------------------
```

### fx:String.toLowerCase 

`fx:String.toLowerCase` wraps [`java.lang.String.toLowerCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String

#### Output

String 

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.toLowerCase(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "THIS IS A TEST" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "this is a test" |
--------------------
```

### fx:String.toUpperCase

`fx:String.toUpperCase` wraps [`java.lang.String.toUpperCase`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.toUpperCase(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "this is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "THIS IS A TEST" |
--------------------
```

### fx:String.trim

`fx:String.trim` wraps [`java.lang.String.trim`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.trim(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "  this is a test  " .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "this is a test" |
--------------------
```

### fx:String.replace

`fx:String.replace` wraps [`java.lang.String.replace`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#replace(java.lang.CharSequence,java.lang.CharSequence))

#### Input

String, Character, Character

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.replace(?string, "f", "d") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "fog" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.replace(?string, "f", "d") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "fog" .
        ?s        rdf:_1      ?string
      }
  }
```

### fx:String.stripLeading

`fx:String.stripLeading` wraps [`java.lang.String.stripLeading`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#stripLeading())

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.stripLeading(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "  this is a test  " .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
----------------------
| result1            |
======================
| "this is a test  " |
----------------------
```

### fx:String.stripTrailing

`fx:String.stripTrailing` wraps [`java.lang.String.stripTrailing`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#stripTrailing())

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.stripTrailing(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "  this is a test  " .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
----------------------
| result1            |
======================
| "  this is a test" |
----------------------
```

### fx:String.removeTags

`fx:String.removeTags`  removes the XML tags from the input string

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:String.removeTags(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "<p>This is a test</p>" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "This is a test" |
--------------------
```

## Functions for manipulating words

The system supports the following functions to manipulate words (See [issue 121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)).

### fx:WordUtils.capitalize

`fx:WordUtils.capitalize` wraps [`org.apache.commons.text.WordUtils.capitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalize(java.lang.String))

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:WordUtils.capitalize(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a TEST" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "This Is A TEST" |
--------------------
```

### fx:WordUtils.capitalizeFully

`fx:WordUtils.capitalizeFully` wraps [`org.apache.commons.text.WordUtils.capitalizeFully`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#capitalizeFully(java.lang.String))

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:WordUtils.capitalizeFully(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a TEST" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "This Is A Test" |
--------------------
```

### fx:WordUtils.initials

`fx:WordUtils.initials` wraps [`org.apache.commons.text.WordUtils.initials`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#initials(java.lang.String))

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:WordUtils.initials(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a TEST" .
        ?s        rdf:_1      ?string
      }
  }

```

Result

```
-----------
| result1 |
===========
| "TiaT"  |
-----------
```

### fx:WordUtils.swapCase

- `fx:WordUtils.swapCase` wraps [`org.apache.commons.text.WordUtils.swapCase`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#swapCase(java.lang.String))

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:WordUtils.swapCase(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a TEST" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "tHIS IS A test" |
--------------------
```

### fx:WordUtils.uncapitalize

`fx:WordUtils.uncapitalize` wraps [`org.apache.commons.text.WordUtils.uncapitalize`](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/WordUtils.html#uncapitalize(java.lang.String))

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:WordUtils.uncapitalize(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a TEST" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "this is a tEST" |
--------------------
```

## Hash functions
The system supports the following functions for computing hash digest from strings (See issues [104](https://github.com/SPARQL-Anything/sparql.anything/issues/104) and [121](https://github.com/SPARQL-Anything/sparql.anything/issues/121)):

### fx:DigestUtils.md2Hex

`fx:DigestUtils.md2Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md2Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md2Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.md2Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------------------------
| result1                            |
======================================
| "dc378580fd0722e56b82666a6994c718" |
--------------------------------------
```

### fx:DigestUtils.md5Hex

`fx:DigestUtils.md5Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.md5Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#md5Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.md5Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------------------------
| result1                            |
======================================
| "ce114e4501d2f4e2dcea3e17b546f339" |
--------------------------------------
```

### fx:DigestUtils.sha1Hex

`fx:DigestUtils.sha1Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha1Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha1Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.sha1Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
----------------------------------------------
| result1                                    |
==============================================
| "a54d88e06612d820bc3be72877c74f257b561b19" |
----------------------------------------------
```

### fx:DigestUtils.sha256Hex

`fx:DigestUtils.sha256Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha256Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha256Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.sha256Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }

```

Result

```
----------------------------------------------------------------------
| result1                                                            |
======================================================================
| "c7be1ed902fb8dd4d48997c6452f5d7e509fbcdbe2808b16bcf4edce4c07d14e" |
----------------------------------------------------------------------
```

### fx:DigestUtils.sha384Hex

`fx:DigestUtils.sha384Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha384Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha384Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.sha384Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
------------------------------------------------------------------------------------------------------
| result1                                                                                            |
======================================================================================================
| "a27c7667e58200d4c0688ea136968404a0da366b1a9fc19bb38a0c7a609a1eef2bcc82837f4f4d92031a66051494b38c" |
------------------------------------------------------------------------------------------------------
```

### fx:DigestUtils.sha512Hex

`fx:DigestUtils.sha512Hex` wraps [`org.apache.commons.codec.digest.DigestUtils.sha512Hex`](https://www.javadoc.io/doc/commons-codec/commons-codec/1.15/org/apache/commons/codec/digest/DigestUtils.html#sha512Hex-java.lang.String-)

#### Input

String

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:DigestUtils.sha512Hex(?string) AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This is a test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------------------------------------------------------------------------------------------------------------------------
| result1                                                                                                                            |
======================================================================================================================================
| "a028d4f74b602ba45eb0a93c9a4677240dcf281a1a9322f183bd32f0bed82ec72de9c3957b2f4c9a1ccf7ed14f85d73498df38017e703d47ebb9f0b3bf116f69" |
--------------------------------------------------------------------------------------------------------------------------------------
```

## Functions on URLs

The system supports the following functions operating on strings that are URLs (See [issue 176](https://github.com/SPARQL-Anything/sparql.anything/issues/)):

### fx:URLEncoder.encode

`fx:URLEncoder.encode` wraps [`java.net.URLEncoder.encode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html#encode(java.lang.String,java.lang.String))

#### Input

- String to be translated.
- String - The name of a supported character [encoding](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/Charset.html).

#### Output

String

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:URLEncoder.encode(?string, "UTF-8") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "/This is a test/" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------------
| result1                |
==========================
| "%2FThis+is+a+test%2F" |
--------------------------
```

### fx:URLDecoder.decode

`fx:URLEncoder.decode` wraps [`java.net.URLEncoder.decode`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLDecoder.html#decode(java.lang.String,java.lang.String))

#### Input

String, The string to decode
String, The name of a supported character [encoding](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/Charset.html).

#### Output

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:URLDecoder.decode(?string, "UTF-8") AS ?result1)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "This+is+a+test" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
--------------------
| result1          |
====================
| "This is a test" |
--------------------
```

## Working with graph nodes

The system supports the following functions for working on the graph nodes.

### fx:serial

The function `fx:serial (?a ... ?n)` generates an incremental number using the arguments as reference counters. For example, calling `fx:serial("x")` two times will generate `1` and then `2`. Instead, calling `fx:serial(?x)` multiple times will generate sequential numbers for each value of `?x`.

#### Input

Any sequence of nodes

#### Output

Integer

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?s (fx:serial(?s) AS ?serial)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[1,2,1,2,4]" ;
                  fx:media-type  "application/json" .
        ?c        fx:anySlot     ?s
      }
  }

```

Result

```
--------------------------------------------------------
| s                                           | serial |
========================================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | 1      |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | 1      |
| "1"^^<http://www.w3.org/2001/XMLSchema#int> | 2      |
| "2"^^<http://www.w3.org/2001/XMLSchema#int> | 2      |
| "4"^^<http://www.w3.org/2001/XMLSchema#int> | 1      |
--------------------------------------------------------
```

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?wins ?team (fx:serial(?wins, ?team) AS ?serial)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[{\"team\":\"Golden State Warriors\", \"year\":2015, \"wins\": 67}, {\"team\":\"Golden State Warriors\", \"year\":2016, \"wins\": 73}, {\"team\":\"Golden State Warriors\", \"year\":2017, \"wins\": 67}]" ;
                  fx:media-type  "application/json" .
        ?c        xyz:wins       ?wins ;
                  xyz:team       ?team
      }
  }
```

Result

**Note**: ?serial increments when a certain team concludes the season with a certain number of score. 

```
-----------------------------------------------------------------------------------
| wins                                         | team                    | serial |
===================================================================================
| "67"^^<http://www.w3.org/2001/XMLSchema#int> | "Golden State Warriors" | 1      |
| "73"^^<http://www.w3.org/2001/XMLSchema#int> | "Golden State Warriors" | 1      |
| "67"^^<http://www.w3.org/2001/XMLSchema#int> | "Golden State Warriors" | 2      |
-----------------------------------------------------------------------------------
```

### fx:entity

The function `fx:entity (?a ... ?n)` accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (`rdf:_1`,`rdf:_2`,...) are cast to numbers and then to strings (`"1","2"`).
```
BIND ( fx:entity ( myns:, "dummy-entity", 1) AS ?myentity)
# is equivalent to
BIND ( IRI( CONCAT ( STR (myns:), "dummy-entity", STR(1) ) AS ?myentity )
```
See also [issue 106](https://github.com/SPARQL-Anything/sparql.anything/issues/106)

#### Input

Any sequence of nodes

#### Output

URI Node

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  ?team ?year (fx:entity("http://example.org/", fx:URLEncoder.encode(?team, "UTF-8"), ?year) AS ?entity)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content     "[{\"team\":\"Golden State Warriors\", \"year\":2015, \"wins\": 67}, {\"team\":\"Golden State Warriors\", \"year\":2016, \"wins\": 73}, {\"team\":\"Golden State Warriors\", \"year\":2017, \"wins\": 67}]" ;
                  fx:media-type  "application/json" .
        ?c        xyz:year       ?year ;
                  xyz:team       ?team
      }
  }
```

Result

```
-----------------------------------------------------------------------------------------------------------------------------
| team                    | year                                           | entity                                         |
=============================================================================================================================
| "Golden State Warriors" | "2015"^^<http://www.w3.org/2001/XMLSchema#int> | <http://example.org/Golden+State+Warriors2015> |
| "Golden State Warriors" | "2016"^^<http://www.w3.org/2001/XMLSchema#int> | <http://example.org/Golden+State+Warriors2016> |
| "Golden State Warriors" | "2017"^^<http://www.w3.org/2001/XMLSchema#int> | <http://example.org/Golden+State+Warriors2017> |
-----------------------------------------------------------------------------------------------------------------------------
```

### fx:literal

The function `fx:literal( ?a , ?b )` builds a literal from the string representation of `?a`, using `?b` either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).

#### Input

String, (URI or language code)

#### Output

Literal node

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:literal(?string, xsd:int) AS ?result)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "1" .
        ?s        rdf:_1      ?string
      }
  }

```

Result

```
-----------------------------------------------
| result                                      |
===============================================
| "1"^^<http://www.w3.org/2001/XMLSchema#int> |
-----------------------------------------------
```

#### Example

```sparql
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT  (fx:literal(?string, "it") AS ?result)
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content  "uno" .
        ?s        rdf:_1      ?string
      }
  }
```

Result

```
------------
| result   |
============
| "uno"@it |
------------
```

### fx:bnode

The function `fx:bnode(?a) ` builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph. Apparently, the standard function `BNODE` does generate a new node for each query solution (see issue [#273](https://github.com/SPARQL-Anything/sparql.anything/issues/273) for an explanatory case).

#### Input

Any node

#### Output

Blank node

#### Example

```sparql
PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>
PREFIX  ex:   <http://example/>

CONSTRUCT 
  { 
    ?bnode ex:p ?A .
  }
WHERE
  { SERVICE <x-sparql-anything:>
      { fx:properties
                  fx:content      "c1,c2\nb0,A\nb0,B\nb0,C\nb0,D\nb0,E\nb1,A\nb2,B\nb3,C\nb4,D\nb5,E" ;
                  fx:media-type   "text/csv" ;
                  fx:csv.headers  true .
        _:b0      xyz:c1          ?b0 ;
                  xyz:c2          ?A
      }
    BIND(fx:bnode(?b0) AS ?bnode)
  }
```

**Note**: the CSV passed as content string is the following

```csv
c1,c2
b0,A
b0,B
b0,C
b0,D
b0,E
b1,A
b2,B
b3,C
b4,D
b5,E
```

Result

```turtle
@prefix ex:   <http://example/> .
@prefix fx:   <http://sparql.xyz/facade-x/ns/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz:  <http://sparql.xyz/facade-x/data/> .

[ ex:p    "D" ] .

[ ex:p    "A" ] .

[ ex:p    "C" ] .

[ ex:p    "E" ] .

[ ex:p    "C" , "E" , "D" , "A" , "B" ] .

[ ex:p    "B" ] .
```



### fx:LevenshteinDistance

The function `fx:LevenshteinDistance(?n1, ?n2) `  computes the Levenshtein Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Integer

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:LevenshteinDistance("abc", "cbe") AS ?result) 
} 
```

Result

| result |
|--------|
| 2      |



### fx:CosineDistance

The function `fx:CosineDistance(?n1, ?n2) `  computes the Cosine Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Double

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:CosineDistance("abc", "cbe") AS ?result) 
} 
```

Result

| result |
|--------|
| 1.0    |



### fx:JaccardDistance

The function `fx:JaccardDistance(?n1, ?n2) `  computes the Jaccard Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Double

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:JaccardDistance("abc", "cbe") AS ?result) 
} 
```

Result

| result |
|--------|
| 0.5    |



### fx:JaroWinklerDistance

The function `fx:JaroWinklerDistance(?n1, ?n2) `  computes the Jaro-Winkler Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Double

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:JaroWinklerDistance("abc", "cbe") AS ?result) 
} 
```

Result

| result              |
|---------------------|
| 0.44444444444444453 |



### fx:LongestCommonSubsequenceDistance

The function `fx:LongestCommonSubsequenceDistance(?n1, ?n2) `  computes the Longest Common Subsequence Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Integer

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:HammingDistance("abc", "abe") AS ?result) 
} 
```

Result

| result |
|--------|
| 2      |





### fx:HammingDistance

The function `fx:HammingDistance(?n1, ?n2) `  computes the Hamming Distance between ?n1 and ?n2 (see #182).

#### Input

String, String

#### Output

Integer

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:HammingDistance("abc", "abe") AS ?result) 
} 
```

Result

| result |
|--------|
| 1      |


### fx:QGramDistance

The function `fx:QGramDistance(?n1, ?n2) `  computes the QGram Distance between ?n1 and ?n2 (see #394).

#### Input


String, String


#### Output

Double

#### Example

```sparql
PREFIX fx:  <http://sparql.xyz/facade-x/ns/> 

SELECT ?result WHERE { 
    BIND (fx:QGramDistance("abcd", "abce") AS ?result) 
} 
```

Result

| result |
|--------|
| 2.0    |

<!--
###

#### Input

#### Output

#### Example

```
```

Result

```
```
-->
