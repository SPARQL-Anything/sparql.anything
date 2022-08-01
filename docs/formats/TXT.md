# TXT

A text file  is a computer file containing an ordered sequence of characters.

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

- txt

## Media types

SPARQL Anything selects this transformer for the following media types:

- text/plain

## Default Transformation


### Data

```
Hello world!
Hello world!

```

Located at https://sparql-anything.cc/examples/simple.txt

### Query

```
CONSTRUCT
  {
    ?s ?p ?o .
  }
WHERE
  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt>
      { ?s  ?p  ?o }
  }

```

### Facade-X RDF

```turtle

@prefix fx:  <http://sparql.xyz/facade-x/ns/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix xyz: <http://sparql.xyz/facade-x/data/> .

[ rdf:type  fx:root ;
  rdf:_1    "Hello world!\nHello world!\n"
] .


```


## Options

### Summary

Option name|Description|Valid Values|Default Value|
|-|-|-|-|
|txt.regex|It tells SPARQL Anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex.|Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)|No value|
|txt.group|It tells SPARQL Anything to generate slots by using a specific group of the regular expression.|Any integer|No value|
|txt.split|It tells SPARQL Anything to split the input around the matches of the give regular expression.|Any valid regular expression|No value|

---

### `txt.regex`

#### Description

It tells SPARQL Anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex.

#### Valid Values

Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)

#### Default Value

No value

#### Examples

##### Input

```
Hello world!
Hello world!

```

Located at https://sparql-anything.cc/examples/simple.txt

##### Use Case 1: Retrieving lines of the file.

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

```
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

Any integer

#### Default Value

No value

#### Examples

##### Input

```
Hello world!
Hello world!

```

Located at https://sparql-anything.cc/examples/simple.txt

##### Use Case 1: Retrieving the lines of the file and strips `\n` out.

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

```
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

Any valid regular expression

#### Default Value

No value

#### Examples

##### Input


```
Hello world!
Hello world!

```

Located at https://sparql-anything.cc/examples/simple.txt

##### Use Case 1: Retrieving the lines of the file by splitting by `\n`

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

```

------------------
| line           |
==================
| "Hello world!" |
| "Hello world!" |
------------------

```

<!--
### `option`

#### Description

#### Valid Values

#### Default Value

#### Examples

##### Input

##### Use Case 1: TODO

###### Query

```
TODO
```

###### Result

```
TODO
```
-->
