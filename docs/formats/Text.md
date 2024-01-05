<!-- This page has been generated with sparql-anything-documentation-generator module -->

# Text

A text file is a computer file containing an ordered sequence of characters.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- txt

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/plain

## Default implementation

- [io.github.sparqlanything.text.TextTriplifier](../sparql-anything-text/src/main/java/io/github/sparqlanything/text/TextTriplifier.java)

## Default Transformation

### Data

Located at https://sparql-anything.cc/examples/simple.txt

### Query

```
CONSTRUCT 
  { 
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt>
      { GRAPH ?g
          { ?s  ?p  ?o }
      }
  }

```

### Facade-X RDF

```turtle
@prefix dc:     <http://purl.org/dc/elements/1.1/> .
@prefix eg:     <http://www.example.org/> .
@prefix fx:     <http://sparql.xyz/facade-x/ns/> .
@prefix ja:     <http://jena.hpl.hp.com/2005/11/Assembler#> .
@prefix owl:    <http://www.w3.org/2002/07/owl#> .
@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .
@prefix rss:    <http://purl.org/rss/1.0/> .
@prefix vcard:  <http://www.w3.org/2001/vcard-rdf/3.0#> .
@prefix whatwg: <https://html.spec.whatwg.org/#> .
@prefix xhtml:  <http://www.w3.org/1999/xhtml#> .
@prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz:    <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root ;
  rdf:_1    "Hello world!\nHello world!\n"
] .

```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
| [txt.regex](#txtregex) | It tells SPARQL Anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex. | Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) | Not set |
| [txt.group](#txtgroup) | It tells SPARQL Anything to generate slots by using a specific group of the regular expression. | Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) | `-1` |
| [txt.split](#txtsplit) | It tells SPARQL Anything to split the input around the matches of the give regular expression. | Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) | Not set |

---
### `txt.regex`

#### Description

It tells SPARQL Anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex.

#### Valid Values

Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)

#### Default Value

Not set

#### Examples

##### Example 1

Retrieving lines of the file.

###### Input

https://sparql-anything.cc/examples/simple.txt

###### Query

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  ?line
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt>
      { fx:properties
                  fx:txt.regex  ".*\\n" .
        ?s        fx:anySlot    ?line
      }
  }

```

###### Result

```turtle
--------------------
| line             |
====================
| "Hello world!\n" |
| "Hello world!\n" |
--------------------

```

---
### `txt.group`

#### Description

It tells SPARQL Anything to generate slots by using a specific group of the regular expression.

#### Valid Values

Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)

#### Default Value

`-1`

#### Examples

##### Example 1

Retrieving the lines of the file and strips `\n` out.

###### Input

https://sparql-anything.cc/examples/simple.txt

###### Query

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  ?line
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt>
      { fx:properties
                  fx:txt.regex  "(.*)\\n" ;
                  fx:txt.group  1 .
        ?s        fx:anySlot    ?line
      }
  }

```

###### Result

```turtle
------------------
| line           |
==================
| "Hello world!" |
| "Hello world!" |
------------------

```

---
### `txt.split`

#### Description

It tells SPARQL Anything to split the input around the matches of the give regular expression.

#### Valid Values

Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)

#### Default Value

Not set

#### Examples

##### Example 1

Retrieving the lines of the file by splitting by `\n`

###### Input

https://sparql-anything.cc/examples/simple.txt

###### Query

```
PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>

SELECT  ?line
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt>
      { fx:properties
                  fx:txt.split  "\\n" .
        ?s        fx:anySlot    ?line
      }
  }

```

###### Result

```turtle
------------------
| line           |
==================
| "Hello world!" |
| "Hello world!" |
------------------

```





