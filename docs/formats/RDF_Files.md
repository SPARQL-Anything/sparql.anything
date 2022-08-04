# Query static RDF files

The SPARQL Anything engine can load static RDF files in memory and perform the query against it, alongside any `x-sparql-anything` service clause.
RDF files produced by previous SPARQL Anything processes can joined with data coming from additional resources.
This feature is enabled with the command line argument `-l|--load` that accepts a file or a directory.
In the second case, all RDF files in the folder are loaded in memory before execution.
