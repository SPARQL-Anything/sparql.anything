# Command Line Interface

## Download

## Usage

```bash
java -jar sparql.anything-<version>.jar  -q <query> [-f <output
format>] [-v <filepath | name=value> ... ] [-c <option=value>]
[-l <path>] [-o <filepath>]
```

### -q,--query <query>                    
The path to the file storing the query to execute or the query itself.

### -o,--output <file>
OPTIONAL - The path to the output file. [Default: STDOUT]

### -a,--append
OPTIONAL - Should output to file be appended? 

> [!WARNING] 
> This option does not ensure that the whole file is valid -- that is up to the user to set up the conditions (such as using NQ serialization and not using blank nodes)

### -e,--explain                          
OPTIONAL - Explain query execution

### -l,--load <load>                      
OPTIONAL - The path to one RDF file or a folder including a set of files to be loaded. When present, the data is loaded in memory and the query executed against it.

> [!NOTE]  
> This is useful to combine existing RDF with newly incoming data to be converted, and build small workflows with subsequent queries. See examples in [IMMA # step-3-generate-the-list-of-artworks](https://github.com/SPARQL-Anything/showcase-imma#step-3-generate-the-list-of-artworks)

### -f,--format <string>                  
OPTIONAL -  Format of the output file. 
Supported values: JSON, XML, CSV, TEXT, TTL, NT, NQ. [Default: TEXT or TTL]

| Query type               | Default |                                                                              Supported |
|:-------------------------|:-------:|---------------------------------------------------------------------------------------:|
| SELECT / ASK             |   CSV   |                                                                        JSON, XML, TEXT |
| CONSTRUCT / DESCRIBE     |   TTL   | JSON, JSONLD, JSONLD11, XML, TTL / TURTLE, NT / NTRIPLES, NQ / NQUADS, TRIG, TRIX, CSV |

### -s,--strategy <strategy>              
OPTIONAL - Strategy for query evaluation. Possible values: '1' - triple filtering (default), '0' - triplify all data. The system fallbacks to '0' when the strategy is not implemented yet for the given resource type.

### -p,--output-pattern <outputPattern>   
OPTIONAL - Output filename pattern, e.g. 'my-file-?friendName.json'. Variables should start with '?' and refer to bindings from the input file (`-v`). This option can only be used in combination with 'values' (`-v` or `--values`) and is ignored otherwise. This option overrides 'output'.

### -v,--values <values>
OPTIONAL - Values passed as input parameter to a query template. When present, the query is pre-processed by substituting variable names with the values provided. Variable names must use prefix `_` and follow the Basil convention, [see README for details](README.md#query-templates-and-variable-bindings-cli-only). You should also use `$` instead of `?` to indicate that the variable is an external parameter. In particular, you can pass IRIs if the var has suffix `_iri` (e.g. `$_my_iri`) , and you can use relative IRIs if the query specifies `BASE`.
The argument can be used in two ways:
- 
- (1) Provide a single SPARQL ResultSet file (in [CSV, TSV](https://www.w3.org/TR/2013/REC-sparql11-results-csv-tsv-20130321), [JSON](https://www.w3.org/TR/2013/REC-sparql11-results-json-20130321) or [XML](https://www.w3.org/TR/2013/REC-rdf-sparql-XMLres-20130321) format. In this case, the query is executed for each set of bindings in the input result set. Only 1 file is allowed.
- (2) Named variable bindings: the argument value must follow the syntax: `var_name=var_value`. The argument can be passed multiple times and the query repeated for each set of values. WARNING: the var name given on the command-line must not include the prefix and suffix. Eg to pass the IRI mentioned as example above, use `-v my=https://example.org/`

### -c,--configuration <option=value>     
OPTIONAL - Configuration to be passed to the SPARQL Anything engine (this is equivalent to define them in the SERVICE IRI). The argument can be passed multiple times (one for each option to be
set). Options passed in this way can be overwritten in the SERVICE IRI or in the Basic Graph Pattern.


### -nc,--no-clobber
OPTIONAL - Do not execute if the specified output file already exists.
