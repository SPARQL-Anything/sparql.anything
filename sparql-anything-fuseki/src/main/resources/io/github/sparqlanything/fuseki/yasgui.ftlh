<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<meta name="theme-color" content="#000000" />
	<meta name="description" content="SPARQL Anything - Query any file format with SPARQL" />
	<title>SPARQL Anything</title>

	<!-- YASGUI CSS -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@matdata/yasgui@5.14.0/build/yasgui.min.css">
	
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
	<script src="https://cdn.jsdelivr.net/npm/@matdata/yasgui@5.14.0/build/yasgui.min.js"></script>

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
				snippets: ${generatedSnippets?no_esc},
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
