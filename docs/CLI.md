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

    ! WARNING: this option does not ensure that the whole file is valid -- that is up to the user to set up the conditions (such as using NQ serialization and not using blank nodes)

### -e,--explain                          
OPTIONAL - Explain query execution

### -l,--load <load>                      
OPTIONAL - The path to one RDF file or a folder including a set of files to be loaded. When present, the data is loaded in memory and the query executed against it.

### -f,--format <string>                  
OPTIONAL -  Format of the output file. Supported values: JSON, XML, CSV, TEXT, TTL, NT, NQ. [Default: TEXT or TTL]

### -s,--strategy <strategy>              
OPTIONAL - Strategy for query evaluation. Possible values: '1' - triple filtering (default), '0' - triplify all data. The system fallbacks to '0' when the strategy is not implemented yet for the given resource type.

### -p,--output-pattern <outputPattern>   
OPTIONAL - Output filename pattern, e.g. 'my-file-?friendName.json'. Variables should start with '?' and refer to bindings from the input file. This option can only be used in combination with 'input' and is ignored otherwise. This option overrides 'output'.

### -v,--values <values>
OPTIONAL - Values passed as input parameter to a query template. When present, the query is pre-processed by substituting variable names with the values provided. The argument
can be used in two ways. 

- (1) Providing a single SPARQL ResultSet file. In this case, the query is executed for each set of bindings in the input result set. Only 1 file is allowed. 
- (2) Named variable bindings: the argument value must follow the syntax: <var_name=var_value>. The argument can be passed multiple times and
the query repeated for each set of values.

### -c,--configuration <option=value>     
OPTIONAL - Configuration to be passed to the SPARQL Anything engine (this is equivalent to define them in the SERVICE IRI). The argument can be passed multiple times (one for each option to be
set). Options passed in this way can be overwritten in the SERVICE IRI or in the Basic Graph Pattern.

### -i,--input <input> (Deprecated) 
Superseded by `-v | --values`

OPTIONAL - The path to a SPARQL result set file to be used as input. When present, the query
is pre-processed by substituting variable names with values from the bindings provided. The query is repeated for each set of bindings
in the input result set.

## Build from source