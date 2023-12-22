@Format(name = "HTML", description = "HTML can be captured by the Document Object Model (DOM) specification. HTML elements (also known as tags) can be considered containers.\n" +
		"\n" +
		"According to the Facade-X model, SPARQL Anything uses:\n" +
		"\n" +
		"RDF Properties for specifying tag attributes;\n" +
		"Container membership properties for specifying relations to child elements in the DOM tree. These may include text, which can be expressed as RDF literals of type xsd:string.\n" +
		"Tag names are used to type the container. Specifically, the tag name is used to mint a URI that identifies the class of the corresponding containers.",
resourceExample = "https://sparql-anything.cc/examples/simple.html")
package io.github.sparqlanything.html;

import io.github.sparqlanything.model.annotations.Format;
