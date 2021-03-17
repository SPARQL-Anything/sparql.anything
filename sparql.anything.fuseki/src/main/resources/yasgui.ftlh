<DOCTYPE html>
<!--html lang="en">
<head>
  <meta charset="utf-8">
  <title>SPARQL Endpoint</title>
  <link href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)' rel='stylesheet' type='text/css'/>
</head>
<body>
  <div id="yasqe"></div>
  <div id="yasr"></div>
  <script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
  <script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
  <script>
  var yasqe = YASQE(document.getElementById("yasqe"), {
  	sparql : {
  		showQueryButton : true,
  		endpoint : '${sparqlPath}'
  	}
  });

	yasqe.addPrefixes({ fx: "${fxPrefix}" });

  var yasr = YASR(document.getElementById("yasr"), {
  	getUsedPrefixes : yasqe.getPrefixesFromQuery
  });

  // link both together
  yasqe.options.sparql.callbacks.complete = yasr.setResponse;

  </script>
</body-->
<html lang="en">

<head>
	<meta charset="utf-8">
	<title>SPARQL.Anything</title>

	<!--link href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)' rel='stylesheet' type='text/css' /-->

	<link href="https://unpkg.com/@triply/yasgui/build/yasgui.min.css" rel="stylesheet" type="text/css" />
  <script src="https://unpkg.com/@triply/yasgui/build/yasgui.min.js"></script>

	<!-- Bootstrap core CSS -->
	<!--link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet"-->

	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

	<!-- Custom fonts for this template -->
	<link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
	<link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

	<!-- Plugin CSS -->
	<link href="vendor/magnific-popup/magnific-popup.css" rel="stylesheet" type="text/css">

	<!-- Custom styles for this template -->
	<link href="css/freelancer.css" rel="stylesheet">


	<style>
		.query-button {padding:4px 8px;}
	</style>

</head>

<body>
	<!-- Navigation -->
	<nav class="navbar navbar-expand-lg bg-secondary fixed-top text-uppercase" id="mainNav">
		<div class="container">
			<a class="navbar-brand js-scroll-trigger" href="index.html">Framester</a>
			<button class="navbar-toggler navbar-toggler-right text-uppercase text-white rounded" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
				<i class="fa fa-bars"></i>
			</button>
			<div class="collapse navbar-collapse" id="navbarResponsive">
				<ul class="navbar-nav ml-auto">
					<li class="nav-item mx-0 mx-lg-1">
						<a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="https://w3id.org/framester/schema">Schema</a>
					</li>
					<li class="nav-item mx-0 mx-lg-1">
						<a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="index.html#data">Data</a>
					</li>
					<li class="nav-item mx-0 mx-lg-1">
						<a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="index.html#queryexamples">Query examples</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>

	<section>
		<div class="container">
			<hr class="mb-1">


			<div class="row">
				<div class="col-lg-12">
					<div class="btn-toolbar" id="buttons" role="group" aria-label="Button group with nested dropdown">
						<div class="btn-group query-button" role="group">
							<button id="btnGroupDrop1" type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Query examples</button>
							<div class="dropdown-menu" id="datasets" aria-labelledby="btnGroupDrop1"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12" id="yasgui"></div>
			</div>

			<!--div class="row">
				<div class="col-lg-12" id="yasqe">
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12" id="yasr"></div>
			</div-->


		</div>
	</section>

	<script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
	<script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
	<script src="vendor/jquery/jquery.min.js"></script>
	<script src='js/createQueriesYASGUI.js'></script>
</body>
