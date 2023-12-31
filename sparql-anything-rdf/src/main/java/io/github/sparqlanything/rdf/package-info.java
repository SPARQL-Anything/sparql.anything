@Format(description = "RDF files can be targeted by the option `location`, the content is loaded as-is (no facade-x interpretation, obviously). In addition, the SPARQL Anything Command Line Interface can load static RDF files.\n" +
		"\n" +
		"The query does not need to include a SERVICE clause, so you can use the tool to just query some RDF file of your choice.\n" +
		"This is useful when you want to break down the process so that RDF files produced by previous SPARQL Anything processes are joined with data coming from additional transformatioons.\n" +
		"Examples of this can be found in the [tutorials](TUTORIALS.md).",
name="RDF",
resourceExample = "https://sparql-anything.cc/examples/simple.ttl")
package io.github.sparqlanything.rdf;

import io.github.sparqlanything.model.annotations.Format;
