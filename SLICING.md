# Slicing large files

By default, SPARQL Anything builds the whole Façade-X graph of a resource before evaluating the query. For large files this can exhaust memory. With slicing, the resource is split into parts and the query is evaluated on each part in turn. Only one part is held in memory at a time.

Enable it with the `slice` option:

```
SERVICE <x-sparql-anything:location=big.csv,csv.headers=true,slice=true> { ... }
```

or

```
fxe:properties fxe:slice true .
```

## How it works

Each slice is triplified into a small Façade-X graph containing the root container and one item only, at the same slot position it has in the full graph (`rdf:_n`). The query pattern inside `SERVICE` is evaluated against that graph, and the results of all slices are concatenated.

The shape of each slice is the same as in the full graph, so a query written for the unsliced resource usually works unchanged.

## What is a slice

| Format | Slice | Requires |
|--------|-------|----------|
| CSV | one row (headers apply to every row with `csv.headers=true`) | – |
| JSON | one item of the top-level array | top-level array |
| JSON | one match of the JsonPath expression | `json.path` (when the top level is an object) |
| XML | one element matched by the XPath expression | `xml.path` |

Other formats ignore `slice`.

## What changes in the query semantics

The query sees one slice at a time. So:

- **Joins across items do not match.** A pattern relating two rows (or two array items, or two XML elements) returns nothing, because they are never in the same graph. See the example in [slice](Configuration.md#slice).
- **Aggregates, `DISTINCT`, `ORDER BY` and `LIMIT` belong outside the `SERVICE` clause.** Inside it, they apply to each slice separately. Outside it, they apply to the concatenated results, as expected.
- **Container-level patterns see one item.** For example, counting the slots of the root inside `SERVICE` returns 1 per slice.

If a query needs to relate items, either do not slice, or move the join outside `SERVICE`, over the results.

## Batches: `slice.size`

By default (`slice.size=1`) each slice is evaluated on its own. `slice.size` groups several slices into one evaluation:

```
SERVICE <x-sparql-anything:location=allCountries.txt,csv.headers=true,csv.delimiter=%09,slice=true,slice.size=100000> { ... }
```

Larger batches reduce the per-execution overhead, at the cost of memory. Joins across items in the same batch do match. As `slice.size` grows, the behaviour approaches `slice=false`.

## Memory

- **CSV and top-level JSON arrays** are read as a stream: memory is bounded by the batch.
- **`json.path` and `xml.path`** select their matches over the whole document first (XML is indexed in memory as a whole). Slicing then bounds the size of the RDF graph, not the parsing of the input.

## Examples

### CSV: filter and aggregate, row by row

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT (AVG(xsd:float(?petalLength)) AS ?avgPetalLength)
WHERE {
  SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true,csv.format=TDF,slice=true> {
    ?row xyz:Sepal_length ?length ;
         xyz:Petal_length ?petalLength .
    FILTER (xsd:float(?length) > 4.9)
  }
}
```

The `AVG` is outside `SERVICE`, so it is computed over all rows.

### JSON: top-level array, item by item

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>

SELECT ?name ?surname ?movie
WHERE {
  SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simpleArray.json,slice=true> {
    ?p xyz:name ?name ;
       xyz:surname ?surname ;
       xyz:movie ?movie .
  }
}
```

### XML: one element per slice with `xml.path`

```sparql
PREFIX xyz: <http://sparql.xyz/facade-x/data/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT ?name ?price
WHERE {
  SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple-menu.xml,xml.path=//food,slice=true> {
    ?food a xyz:food ;
          ?s1 [ a xyz:name ; rdf:_1 ?name ] ;
          ?s2 [ a xyz:price ; rdf:_1 ?price ] .
  }
}
```

For JSON objects, use `json.path` the same way, e.g. `json.path=$.items[*],slice=true`.
