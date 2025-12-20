<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<meta name="theme-color" content="#000000" />
	<meta name="description" content="SPARQL Anything - Query any file format with SPARQL" />
	<title>SPARQL Anything</title>

	<!-- YASGUI CSS -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@matdata/yasgui@5.8.1/build/yasgui.min.css">
	
	<!-- Font Awesome for icons -->
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

	<style>
		/* Global styles */
		html {
			height: 100%;
		}
		body {
			height: 100%;
			margin: 0;
			padding: 0;
			font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
				"Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
				sans-serif;
			-webkit-font-smoothing: antialiased;
			-moz-osx-font-smoothing: grayscale;
			background-color: #ffffff;
			transition: background-color 0.3s ease;
		}

		/* Dark theme styles */
		[data-theme="dark"] body {
			background-color: #1e1e1e;
		}

		/* YASGUI container */
		#yasgui {
			height: 100%;
			margin: 0;
		}
	</style>

</head>

<body>
	<noscript>You need to enable JavaScript to run this app.</noscript>

	<!-- YASGUI container -->
	<div id="yasgui"></div>

	<!-- YASGUI JavaScript -->
	<script src="https://cdn.jsdelivr.net/npm/@matdata/yasgui@5.8.1/build/yasgui.min.js"></script>

	<script>
	// Function to sync theme with body
	function syncThemeWithBody() {
		const theme = document.documentElement.getAttribute("data-theme") || "light";
		document.body.setAttribute("data-theme", theme);
	}

	// YASGUI initialization
	if (typeof Yasgui !== "undefined") {
		const yasgui = new Yasgui(document.getElementById("yasgui"), {
			// Set the SPARQL endpoint
			requestConfig: {
				endpoint: '${sparqlPath}'
			},

			// Allow resizing of the Yasqe editor
			resizeable: true,

			// Whether to autofocus on Yasqe on page load
			autofocus: true,

			// Use the default endpoint when a new tab is opened
			copyEndpointOnNewTab: false,

    		yasqe: {
				snippets: [
					// File Format Snippets
					{ label: "JSON", code: "# Query JSON file: {\"users\": [{\"name\": \"Alice\", \"age\": 30}]}\n# All JSON options:\n#   json.path - JsonPath expression filter (e.g., $.data.items)\n#   json.path.1, json.path.2 - Multiple JsonPath filters\n#   json.literalize - Treat keys as opaque strings (e.g., for GeoJSON)\n#   json.literalize.1, json.literalize.2 - Multiple keys to literalize\n#   json.include-null-values - Produce triples for null values (default: false)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?name ?age WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/users.json\" ;\n      fx:json.path \"$.users[*]\" ;\n      fx:json.include-null-values \"false\" ;\n    .\n    ?user xyz:name ?name ;\n          xyz:age ?age .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "XML", code: "# Query XML: <books><book id=\"1\"><title>Title</title><author>Author</author></book></books>\n# All XML options:\n#   xml.path - XPath expression to filter elements (e.g., /root/items/item)\n#   xml.path.1, xml.path.2 - Multiple XPath expressions\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?id ?title ?author WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/books.xml\" ;\n      fx:xml.path \"/books/book\" ;\n    .\n    ?book xyz:id ?id ;\n          xyz:title ?title ;\n          xyz:author ?author .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "CSV", code: "# Query CSV: name,age,city\\nAlice,30,NYC\\nBob,25,LA\n# All CSV options:\n#   csv.headers - Use first row as headers (default: false)\n#   csv.headers-row - Row number for headers (default: 1)\n#   csv.format - CSV dialect: Default|Excel|InformixUnload|InformixUnloadCsv|MySQL|Oracle|PostgreSQLCsv|PostgreSQLText|RFC4180|TDF\n#   csv.delimiter - Column delimiter (default: ,)\n#   csv.quote-char - Quote character (default: \")\n#   csv.null-string - String to treat as null\n#   csv.ignore-columns-with-no-header - Ignore columns without headers\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?name ?age ?city WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.csv\" ;\n      fx:csv.headers \"true\" ;\n      fx:csv.headers-row \"1\" ;\n      fx:csv.delimiter \",\" ;\n      fx:csv.quote-char \"\\\"\" ;\n      fx:csv.format \"Default\" ;\n      fx:csv.ignore-columns-with-no-header \"false\" ;\n      fx:csv.null-string \"NULL\" ;\n    .\n    ?root fx:anySlot ?row .\n    ?row xyz:name ?name ;\n         xyz:age ?age ;\n         xyz:city ?city .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "YAML", code: "# Query YAML: users:\\n  - name: Alice\\n    age: 30\\n  - name: Bob\\n    age: 25\n# All YAML options:\n#   yaml.allow-duplicate-keys - Allow duplicate keys in YAML (default: false)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?name ?age WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/users.yaml\" ;\n      fx:yaml.allow-duplicate-keys \"false\" ;\n    .\n    ?root xyz:users ?users .\n    ?users fx:anySlot ?user .\n    ?user xyz:name ?name ;\n          xyz:age ?age .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "HTML", code: "# Query HTML: <table><tr><th>Name</th><th>Age</th></tr><tr><td>Alice</td><td>30</td></tr></table>\n# All HTML options:\n#   html.selector - CSS selector (default: :root)\n#   html.metadata - Extract inline RDF (default: false)\n#   html.browser - Browser for JS rendering: chromium|webkit|firefox\n#   html.parser - Parser: html|xml (default: html)\n#   html.browser.wait - Seconds to wait after page load\n#   html.browser.screenshot - Path to save screenshot\n#   html.browser.timeout - Browser timeout in ms (default: 30000)\nPREFIX xyz: <${fxPrefix}>\nPREFIX whatwg: <https://html.spec.whatwg.org/#>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?text WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/page.html\" ;\n      fx:html.selector \"table\" ;\n      fx:html.metadata \"false\" ;\n      fx:html.browser \"chromium\" ;\n      fx:html.parser \"html\" ;\n      fx:html.browser.wait \"5\" ;\n      fx:html.browser.timeout \"30000\" ;\n    .\n    ?s whatwg:innerText ?text .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Markdown", code: "# Query Markdown: # Title\\n## Section\\nParagraph text\n# Extracts structure: headings, paragraphs, lists, code blocks\n# No format-specific options (uses common options only)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?type ?text WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/README.md\" ;\n      fx:trim.strings \"true\" ;\n    .\n    ?doc fx:anySlot ?section .\n    ?section a ?type ;\n             fx:anySlot ?text .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Spreadsheet", code: "# Query Excel/ODS: Sheet with headers in row 1, data in subsequent rows\n# All Spreadsheet options:\n#   spreadsheet.headers - Use first row as headers (default: false)\n#   spreadsheet.headers-row - Row number for headers (default: 1)\n#   spreadsheet.evaluate-formulas - Evaluate formulas (default: false)\n#   spreadsheet.composite-values - Extract hyperlinks/comments (default: false)\n#   spreadsheet.ignore-columns-with-no-header - Ignore columns without headers\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?name ?value WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.xlsx\" ;\n      fx:spreadsheet.headers \"true\" ;\n      fx:spreadsheet.headers-row \"1\" ;\n      fx:spreadsheet.evaluate-formulas \"false\" ;\n      fx:spreadsheet.composite-values \"false\" ;\n      fx:spreadsheet.ignore-columns-with-no-header \"false\" ;\n    .\n    ?root fx:anySlot ?row .\n    ?row xyz:name ?name ;\n         xyz:value ?value .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Slides", code: "# Query PowerPoint presentations (PPTX)\n# Extracts slides, shapes, paragraphs, and text runs\n# All Slides options:\n#   slides.extract-sections - Extract presentation sections (default: false)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?slideNum ?text WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/presentation.pptx\" ;\n      fx:slides.extract-sections \"false\" ;\n    .\n    ?root fx:anySlot ?slide .\n    BIND(fx:cardinal(?slide) AS ?slideNum)\n    ?slide fx:anySlot ?shape .\n    ?shape fx:anySlot ?text .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Docs", code: "# Query Word documents (DOCX)\n# Extracts paragraphs, lists, headings, and comments\n# All Docs options:\n#   docs.merge-paragraphs - Merge paragraphs into single slot (default: false)\n#   docs.table-headers - Use table headers for property URIs (default: false)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?blockType ?text WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/document.docx\" ;\n      fx:docs.merge-paragraphs \"false\" ;\n      fx:docs.table-headers \"false\" ;\n    .\n    ?root fx:anySlot ?block .\n    ?block a ?blockType ;\n           fx:anySlot ?text .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Binary", code: "# Query binary files (images, PDFs, etc.) for metadata\n# Extracts EXIF, file properties, content metadata\n# All Binary options:\n#   bin.encoding - Encoding for file representation (default: BASE64)\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?property ?value WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/image.jpg\" ;\n      fx:bin.encoding \"BASE64\" ;\n    .\n    ?file ?property ?value .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Text", code: "# Query plain text files (line by line or with regex)\n# All Text options:\n#   txt.regex - Regular expression to match patterns\n#   txt.group - Regex group number to extract (default: -1 = whole match)\n#   txt.split - Split input around regex matches\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?lineNum ?lineText WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/file.txt\" ;\n      fx:txt.regex \"(.*)\\\\n\" ;\n      fx:txt.group \"1\" ;\n    .\n    ?root ?slot ?lineText .\n    FILTER(fx:isContainerMembershipProperty(?slot))\n    BIND(fx:cardinal(?slot) AS ?lineNum)\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "RDF", code: "# Query RDF files (Turtle, JSON-LD, N-Triples, etc.)\n# RDF content is queried as-is, no facade-x transformation\n# Supports: .ttl, .jsonld, .nt, .nq, .rdf, .owl, .trig, .trix\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT ?s ?p ?o WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.ttl\" ;\n    .\n    ?s ?p ?o .\n  }\n}", group: "FILE FORMATS" },
					
					{ label: "Archive", code: "# Query archives (ZIP, TAR, GZ) - lists contained files\n# All Archive options:\n#   archive.matches - Regex to match filenames (default: .*)\n#   from-archive - Specific file to extract and triplify\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nCONSTRUCT { \n  ?s1 ?p1 ?o1 .\n}\nWHERE {\n  SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar> {\n    fx:properties fx:archive.matches \".*txt|.*csv\" .\n    ?s fx:anySlot ?file1\n    SERVICE <x-sparql-anything:> {\n      fx:properties fx:location ?file1 ;\n                    fx:from-archive \"https://sparql-anything.cc/examples/example.tar\" .\n      ?s1 ?p1 ?o1\n    }\n  }\n}", group: "FILE FORMATS" },
					
					// Configuration Options with explanations
					{ label: "media-type", code: "# Force a specific media type when file extension doesn't match content\n# Useful for .txt files containing JSON, XML, etc.\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.txt\" ;\n      fx:media-type \"application/json\" ;\n    .\n    ?s ?p ?o .\n  }\n} LIMIT 10", group: "CONFIGURATION" },
					{ label: "blank-nodes", code: "# Use blank nodes (true) or IRIs (false) for containers\n# blank-nodes=false generates more stable, referenceable URIs\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.json\" ;\n      fx:blank-nodes \"false\" ;\n    .\n    ?s ?p ?o .\n    FILTER(!isBlank(?s))\n  }\n} LIMIT 10", group: "CONFIGURATION" },
					{ label: "namespace", code: "# Set custom namespace for generated properties\n# Default uses the file's URL as namespace\nPREFIX xyz: <${fxPrefix}>\nPREFIX ex: <http://example.org/>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.json\" ;\n      fx:namespace \"http://example.org/\" ;\n    .\n    ?s ex:name ?name .\n  }\n} LIMIT 10", group: "CONFIGURATION" },
					{ label: "trim-strings", code: "# Remove leading/trailing whitespace from string values\n# Useful for cleaning CSV/text data\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.csv\" ;\n      fx:trim.strings \"true\" ;\n    .\n    ?s ?p ?o .\n  }\n} LIMIT 10", group: "CONFIGURATION" },
					{ label: "null-string", code: "# Define which string values should be treated as null/missing\n# Prevents empty strings or 'NULL' text from appearing as values\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.json\" ;\n      fx:null.string \"NULL\" ;\n    .\n    ?s ?p ?o .\n    FILTER(BOUND(?o))\n  }\n} LIMIT 10", group: "CONFIGURATION" },
                    { label: "use-rdfs-member", code: "# Use rdf:_1, rdf:_2 (true) or custom slots (false) for array items\n# false provides more flexibility for querying\nPREFIX xyz: <${fxPrefix}>\nPREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\nSELECT * WHERE {\n  SERVICE <x-sparql-anything:> {\n    fx:properties\n      fx:location \"https://example.org/data.json\" ;\n      fx:use.rdfs.member \"false\" ;\n    .\n    ?s ?p ?o .\n  }\n} LIMIT 10", group: "CONFIGURATION" },
					
					// Magic Properties with explanations
					{ label: "fx:anySlot", code: "# Match any slot property (useful for arrays/containers)\n# Retrieves all items regardless of their slot number\n?container fx:anySlot ?item .", group: "MAGIC PROPERTIES" },
					
					// Functions with explanations
					{ label: "fx:cardinal", code: "# Extract numeric index from a slot property (0-based)\n# Converts slot properties to their array positions\nBIND(fx:cardinal(?slot) AS ?index)", group: "FUNCTIONS" },
					{ label: "fx:serial", code: "# Extract numeric index from a slot property (1-based)\n# Similar to cardinal but starts counting from 1\nBIND(fx:serial(?slot) AS ?position)", group: "FUNCTIONS" },
					{ label: "fx:isSlot", code: "# Check if a property is a slot (array index)\n# Returns true for properties like _1, _2, _3, etc.\nFILTER(fx:isSlot(?slot))", group: "FUNCTIONS" },
					{ label: "fx:isContainer", code: "# Check if a value is a container (has slots/properties)\n# Useful for distinguishing objects/arrays from literals\nFILTER(fx:isContainer(?container))", group: "FUNCTIONS" },
					{ label: "fx:isContainerMembershipProperty", code: "# Check if a property is a container membership property\n# Returns true for slot properties (_1, _2, etc.)\nFILTER(fx:isContainerMembershipProperty(?slot))", group: "FUNCTIONS" },
					{ label: "fx:String", code: "# Convert a value to string representation\n# Useful for type coercion and formatting\nBIND(fx:String(?value) AS ?stringValue)", group: "FUNCTIONS" },
					{ label: "fx:literal", code: "# Convert a container to its literal representation\n# Extracts the actual value from wrapper containers\nBIND(fx:literal(?value) AS ?literal)", group: "FUNCTIONS" },
					{ label: "fx:before", code: "# Get the slot before the given slot\n# Navigate backward through container members\nBIND(fx:before(?slot) AS ?previousSlot)", group: "FUNCTIONS" },
					{ label: "fx:after", code: "# Get the slot after the given slot\n# Navigate forward through container members\nBIND(fx:after(?slot) AS ?nextSlot)", group: "FUNCTIONS" },
					{ label: "fx:previous", code: "# Get the value in the previous slot\n# Shortcut for navigating backward\n?container fx:previous(?slot) ?previousValue .", group: "FUNCTIONS" },
					{ label: "fx:next", code: "# Get the value in the next slot\n# Shortcut for navigating forward\n?container fx:next(?slot) ?nextValue .", group: "FUNCTIONS" },

					// Advanced Examples
					{ label: "Multiple files UNION", code: "# Query multiple files and combine results\n{\n  SERVICE <x-sparql-anything:location=https://example.org/data1.json> {\n    ?s ?p ?o .\n  }\n} UNION {\n  SERVICE <x-sparql-anything:location=https://example.org/data2.json> {\n    ?s ?p ?o .\n  }\n}", group: "ADVANCED" },
					{ label: "Navigate nested structure", code: "# Navigate through nested JSON/YAML paths\n?root xyz:items ?items .\n?items fx:anySlot ?item .\n?item xyz:id ?id ;\n      xyz:name ?name .", group: "ADVANCED" },
					{ label: "Create custom URIs", code: "# Generate custom URIs from data values\nBIND(IRI(CONCAT(\"http://example.org/item/\", STR(?value))) AS ?entity)", group: "ADVANCED" },
					{ label: "Nested SERVICE calls", code: "# Query files within archives or process results\nSERVICE <x-sparql-anything:location=file.tar> {\n  ?s fx:anySlot ?innerFile\n  SERVICE <x-sparql-anything:> {\n    fx:properties fx:location ?innerFile ;\n                  fx:from-archive \"file.tar\" .\n    ?s1 ?p1 ?o1\n  }\n}", group: "ADVANCED" },

					// Standard SPARQL Patterns
					{ label: "SELECT", code: "SELECT * WHERE {\n  ?s ?p ?o .\n} LIMIT 10", group: "QUERY TYPES" },
					{ label: "CONSTRUCT", code: "CONSTRUCT {\n  ?s ?p ?o .\n} WHERE {\n  ?s ?p ?o .\n} LIMIT 10", group: "QUERY TYPES" },
					{ label: "ASK", code: "ASK {\n  ?s ?p ?o .\n}", group: "QUERY TYPES" },
					{ label: "DESCRIBE", code: "DESCRIBE ?s", group: "QUERY TYPES" },
					{ label: "OPTIONAL", code: "OPTIONAL {\n  ?s ?p ?o .\n}", group: "PATTERNS" },
					{ label: "UNION", code: "{ ?s ?p ?o . } UNION { ?s ?p2 ?o2 . }", group: "PATTERNS" },
					{ label: "FILTER", code: "FILTER (?var = \"value\")", group: "PATTERNS" },
					{ label: "BIND", code: "BIND(?var AS ?newVar)", group: "PATTERNS" },
					{ label: "ORDER BY", code: "ORDER BY DESC(?var)", group: "MODIFIERS" },
					{ label: "GROUP BY", code: "GROUP BY ?var", group: "MODIFIERS" },
					{ label: "DISTINCT", code: "SELECT DISTINCT ?var WHERE {\n  ?s ?p ?var .\n}", group: "MODIFIERS" },
				],
			},

		});

		// Add custom prefix
		yasgui.getTab().yasqe.addPrefixes({ xyz: "${fxPrefix}" });

		// Sync theme immediately after initialization
		syncThemeWithBody();

		// Watch for theme changes on document.documentElement
		const observer = new MutationObserver((mutations) => {
			mutations.forEach((mutation) => {
				if (mutation.type === "attributes" && mutation.attributeName === "data-theme") {
					syncThemeWithBody();
				}
			});
		});

		observer.observe(document.documentElement, {
			attributes: true,
			attributeFilter: ["data-theme"],
		});
	}
	</script>
</body>
</html>
