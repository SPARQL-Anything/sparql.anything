# Parametrised queries

The SPARQL Anything CLI can run the same query with different input values. Parameters are written as SPARQL variables following the [BASIL convention](https://github.com/basilapi/basil/wiki/SPARQL-variable-name-convention-for-WEB-API-parameters-mapping) and their values are passed with `-v`. This tutorial goes through the main cases, from one value to a batch driven by the results of another query.

Parametrised queries are a feature of the command line interface.

## Setup

Save this file as `artworks.csv`:

```csv
id,title,artist,year
1,The Kiss,Klimt,1908
2,Water Lilies,Monet,1906
3,Judith,Klimt,1901
4,Impression Sunrise,Monet,1872
```

In the examples, `sa` stands for `java -jar sparql-anything-<version>.jar`.

## 1. A parameter

A variable whose name starts with `_` is a parameter. This query, `by-artist.sparql`, returns the works of one artist:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
SELECT ?title ?year WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:title ?title ; xyz:artist ?artist ; xyz:year ?year .
  }
  FILTER (?artist = $_artist)
}
```

The value is passed with `-v`, using the name **without** the `_` prefix:

```bash
sa -q by-artist.sparql -v artist=Klimt
```

```
title,year
The Kiss,1908
Judith,1901
```

SPARQL treats `?` and `$` the same. As a good practice, use `$` for parameters, so they stand out from the variables bound by the query.

The value is written into the query text before the query is parsed. Without `-v`, no substitution happens: `$_artist` is an ordinary unbound variable and the query above returns no results, without an error.

## 2. Typed values

By default a value is substituted as a plain literal. A suffix on the variable name sets its type:

| Variable | Substituted as |
|----------|----------------|
| `$_name` | plain literal |
| `$_name_iri` | IRI |
| `$_name_en` | literal with language tag `en` (any tag) |
| `$_name_integer` | literal typed `xsd:integer` (any XSD type) |
| `$_name_prefix_type` | literal typed `prefix:type` (the prefix must be declared in the query) |

On the command line, the name never includes the suffix.

An integer, in `since.sparql`:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?title ?year WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:title ?title ; xyz:year ?year .
  }
  FILTER (xsd:integer(?year) >= $_since_integer)
}
```

```bash
sa -q since.sparql -v since=1905
```

```
title,year
The Kiss,1908
Water Lilies,1906
```

An IRI, used as a namespace in `to-rdf.sparql`:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
CONSTRUCT { ?s rdfs:label ?title } WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:id ?id ; xyz:title ?title .
  }
  BIND (IRI(CONCAT(STR($_ns_iri), ?id)) AS ?s)
}
```

```bash
sa -q to-rdf.sparql -v ns=http://example.org/artwork/ -f nt
```

```
<http://example.org/artwork/4> <http://www.w3.org/2000/01/rdf-schema#label> "Impression Sunrise" .
<http://example.org/artwork/3> <http://www.w3.org/2000/01/rdf-schema#label> "Judith" .
<http://example.org/artwork/2> <http://www.w3.org/2000/01/rdf-schema#label> "Water Lilies" .
<http://example.org/artwork/1> <http://www.w3.org/2000/01/rdf-schema#label> "The Kiss" .
```

If the query declares a `BASE`, relative IRIs can be passed to `_iri` parameters.

## 3. Optional parameters

A double underscore makes a parameter optional. If no value is given, the variable stays unbound, and the query can test it with `BOUND`. In `optional.sparql`:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
SELECT ?title ?artist WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:title ?title ; xyz:artist ?artist .
  }
  BIND ($__artist AS ?selected)
  FILTER (!BOUND(?selected) || ?artist = ?selected)
}
```

```bash
sa -q optional.sparql
```

```
title,artist
The Kiss,Klimt
Water Lilies,Monet
Judith,Klimt
Impression Sunrise,Monet
```

```bash
sa -q optional.sparql -v artist=Monet
```

```
title,artist
Water Lilies,Monet
Impression Sunrise,Monet
```

## 4. Several values

Repeating `-v` for the same parameter runs the query once per value:

```bash
sa -q by-artist.sparql -v artist=Klimt -v artist=Monet
```

```
title,year
Water Lilies,1906
Impression Sunrise,1872
title,year
The Kiss,1908
Judith,1901
```

With several parameters, the query runs once for each combination of their values (cartesian product). Numeric ranges can be given as `from...to`, inclusive. In `by-id.sparql`:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
SELECT ?title WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:id ?id ; xyz:title ?title .
  }
  FILTER (?id = $_id)
}
```

```bash
sa -q by-id.sparql -v id=1...3
```

```
title
Judith
title
Water Lilies
title
The Kiss
```

The order of the runs is not the order of the values on the command line, and repeated values are run once.

## 5. Values from a query result

Instead of inline values, `-v` accepts a SPARQL result set file (CSV, TSV, JSON or XML). The query runs once per row, in file order. Column names are parameter names, without prefix or suffix.

A first query, `artists.sparql`, lists the artists:

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
SELECT DISTINCT ?artist WHERE {
  SERVICE <x-sparql-anything:location=artworks.csv,csv.headers=true> {
    ?row xyz:artist ?artist .
  }
} ORDER BY ?artist
```

```bash
sa -q artists.sparql -o artists.csv
```

```
artist
Klimt
Monet
```

Its result drives the second query:

```bash
sa -q by-artist.sparql -v artists.csv -p 'out/?artist.csv'
```

```
== out/Klimt.csv
title,year
The Kiss,1908
Judith,1901
== out/Monet.csv
title,year
Water Lilies,1906
Impression Sunrise,1872
```

Only one result set file can be given, and it cannot be combined with inline values.

## 6. Output files

With several runs, there are three ways to handle the output:

- **No `-o`:** all results are printed to STDOUT, one after the other (sections 4 and 5).
- **`-o file`:** one file per run, numbered in run order:

```bash
  sa -q by-artist.sparql -v artists.csv -o res.csv
```

```
  == res-1.csv
  title,year
  The Kiss,1908
  Judith,1901
  == res-2.csv
  title,year
  Water Lilies,1906
  Impression Sunrise,1872
```

- **`-p pattern`:** one file per run, named after the values, as in section 5. The variable in the pattern is the parameter name, and must be followed by a character that is not a letter, digit or `_` (e.g. the extension). Values containing `/` create subfolders.

To link outputs to inputs, prefer `-p`, or `-o` with a result set file. With inline values the run order is not stable, so `res-1` may hold a different value in another run.

### Resuming a batch

With `-nc` (`--no-clobber`), a run is skipped if its output file already exists. After a failure, re-running the same command regenerates only the missing files:

```bash
rm res-2.csv
sa -q by-artist.sparql -v artists.csv -o res.csv -nc   # rewrites res-2.csv only
```

## 7. Notes

- **Missing values:** a run with a missing mandatory parameter is skipped with an error in the log; the other runs continue.
- **Substitution is textual:** it happens on the query text before parsing. Values are inserted as given, so check them if they come from untrusted sources.
- **Examples in practice:** see the [IMMA showcase](https://github.com/SPARQL-Anything/showcase-imma) for a full pipeline built with parametrised queries.