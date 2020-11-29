# Experiment

Objective: **TODO**

## Sources: [1](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json) [2](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json) [3](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json)

### Structure of the input files

```
[
...
 {
   "Autore": "GABINIO MARIO",
   "Titolo": "TORINO/ MONUMENTO A CARLO ALBERTO, PIAZZA CARLO ALBERTO, VISTA LATERALE DESTRA",
   "Datazione": "19/10/1923",
   "Tecnica": "STAMPA ALLA CELLOIDINA",
   "Dimensioni": "229X169",
   "Immagine": "http://93.62.170.226/foto/gabinio/001B1.jpg"
 },
 ...
]

```

### Competency questions

#### CQ1: What are the titles of the artworks attributed to "ANONIMO"?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>

SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Autore","$.Titolo") AS ?obj ?autore ?titolo
WHERE{
	FILTER(REGEX(?autore,".*ANONIMO.*","i"))
}

```

</details>




<details><summary>SPARQL Anything</summary>
	
```
PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json/>

SELECT DISTINCT ?titolo
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Autore "ANONIMO" .
		?s source:Titolo ?titolo .
	}
}

```

</details>


#### CQ2: What are the titles of the artworks made with the technique named "STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO"?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Tecnica","$.Titolo") AS ?obj ?technique ?titolo
WHERE{
  FILTER(REGEX(?technique,".*STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO.*","i"))
}

```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json/>

SELECT DISTINCT ?titolo
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Tecnica ?technique .
		?s source:Titolo ?titolo .
		FILTER(REGEX(?technique,".*STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO.*","i"))
	}
}


```

</details>

#### CQ3: What are the titles of the artworks created in the 1935?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Datazione","$.Titolo") AS ?obj ?date ?titolo
WHERE{
  FILTER(REGEX(?date,".*1935.*","i"))
}

```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json/>

SELECT DISTINCT ?titolo
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Datazione ?date .
		?s source:Titolo ?titolo .
		FILTER(REGEX(?date,".*1935.*","i"))
	}
}


```

</details>

### RDF Generation

#### Target model

```
@prefix ex: <http://example.org/> .
_:0 ex:Autore "GABINIO MARIO";
  ex:Datazione "19/10/1923";
  ex:Dimensioni "229X169";
  ex:Immagine "http://93.62.170.226/foto/gabinio/001B1.jpg";
  ex:Tecnica "STAMPA ALLA CELLOIDINA";
  ex:Titolo "TORINO/ MONUMENTO A CARLO ALBERTO, PIAZZA CARLO ALBERTO, VISTA LATERALE DESTRA" .
```

<details><summary>SPARQL generate for source 1</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX ex: <http://exmaple.org/>


GENERATE {
[] ex:Autore ?autore ;
	 ex:Datazione ?datazione ;
	 ex:Titolo ?titolo ;
   ex:Tecnica ?tecnica ;
	 ex:Immagine ?immagine ;
	 ex:Dimensioni ?dimensioni .
}
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Autore","$.Datazione","$.Titolo","$.Tecnica","$.Immagine","$.Dimensioni") AS ?obj ?autore ?datazione ?titolo  ?tecnica ?immagine ?dimensioni

```
	
</details>



<details><summary>SPARQL generate for source 2</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX ex: <http://exmaple.org/>


GENERATE {
[] ex:Autore ?autore ;
	 ex:Datazione ?datazione ;
	 ex:Titolo ?titolo ;
   ex:Tecnica ?tecnica ;
	 ex:Immagine ?immagine ;
	 ex:Dimensioni ?dimensioni .
}
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json>,"$[*]","$.Autore","$.Datazione","$.Titolo","$.Tecnica","$.Immagine","$.Dimensioni") AS ?obj ?autore ?datazione ?titolo  ?tecnica ?immagine ?dimensioni


```
	
</details>



<details><summary>SPARQL generate for source 3</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX ex: <http://exmaple.org/>


GENERATE {
[] ex:Autore ?autore ;
	 ex:Datazione ?datazione ;
	 ex:Titolo ?titolo ;
   ex:Tecnica ?tecnica ;
	 ex:Immagine ?immagine ;
	 ex:Dimensioni ?dimensioni .
}
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json>,"$[*]","$.Autore","$.Datazione","$.Titolo","$.Tecnica","$.Immagine","$.Dimensioni") AS ?obj ?autore ?datazione ?titolo  ?tecnica ?immagine ?dimensioni



```
	
</details>

<details><summary>SPARQL Anything for source 1</summary>
	
```

CONSTRUCT {
	?s ?p ?o
} WHERE {
	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s ?p ?o .
	}
}


```

</details>

<details><summary>SPARQL Anything for source 2</summary>
	
```


CONSTRUCT {
	?s ?p ?o
} WHERE {
	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json> {
		?s ?p ?o .
	}
}


```

</details>

<details><summary>SPARQL Anything for source 3</summary>
	
```


CONSTRUCT {
	?s ?p ?o
} WHERE {
	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json> {
		?s ?p ?o .
	}
}


```

</details>

<details><summary>RML for source 1</summary>
	
```
@prefix rml: <http://semweb.mmlab.be/ns/rml#> .
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix ql: <http://semweb.mmlab.be/ns/ql#> .
@prefix : <http://example.org/rules/> .
@prefix ex: <http://example.org/> .

:TriplesMap a rr:TriplesMap;
  rml:logicalSource [
    rml:source "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json";
    rml:referenceFormulation ql:JSONPath;
    rml:iterator "$.[*]"
  ].

:TriplesMap rr:subjectMap [
  rr:termType rr:BlankNode
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Autore ;
  rr:objectMap [
  rml:reference "Autore"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Datazione ;
  rr:objectMap [
  rml:reference "Datazione"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Titolo ;
  rr:objectMap [
  rml:reference "Titolo"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Tecnica ;
  rr:objectMap [
  rml:reference "Tecnica"
 ]
].


:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Immagine ;
  rr:objectMap [
  rml:reference "Immagine"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Dimensioni ;
  rr:objectMap [
  rml:reference "Dimensioni"
 ]
].


```
	
</details>


<details><summary>RML for source 2</summary>
	
```
@prefix rml: <http://semweb.mmlab.be/ns/rml#> .
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix ql: <http://semweb.mmlab.be/ns/ql#> .
@prefix : <http://example.org/rules/> .
@prefix ex: <http://example.org/> .

:TriplesMap a rr:TriplesMap;
  rml:logicalSource [
    rml:source "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json";
    rml:referenceFormulation ql:JSONPath;
    rml:iterator "$.[*]"
  ].

:TriplesMap rr:subjectMap [
  rr:termType rr:BlankNode
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Autore ;
  rr:objectMap [
  rml:reference "Autore"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Datazione ;
  rr:objectMap [
  rml:reference "Datazione"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Titolo ;
  rr:objectMap [
  rml:reference "Titolo"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Tecnica ;
  rr:objectMap [
  rml:reference "Tecnica"
 ]
].


:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Immagine ;
  rr:objectMap [
  rml:reference "Immagine"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Dimensioni ;
  rr:objectMap [
  rml:reference "Dimensioni"
 ]
].



```
	
</details>

<details><summary>RML for source 3</summary>
	
```
@prefix rml: <http://semweb.mmlab.be/ns/rml#> .
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix ql: <http://semweb.mmlab.be/ns/ql#> .
@prefix : <http://example.org/rules/> .
@prefix ex: <http://example.org/> .

:TriplesMap a rr:TriplesMap;
  rml:logicalSource [
    rml:source "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json";
    rml:referenceFormulation ql:JSONPath;
    rml:iterator "$.[*]"
  ].

:TriplesMap rr:subjectMap [
  rr:termType rr:BlankNode
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Autore ;
  rr:objectMap [
  rml:reference "Autore"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Datazione ;
  rr:objectMap [
  rml:reference "Datazione"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Titolo ;
  rr:objectMap [
  rml:reference "Titolo"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Tecnica ;
  rr:objectMap [
  rml:reference "Tecnica"
 ]
].


:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Immagine ;
  rr:objectMap [
  rml:reference "Immagine"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Dimensioni ;
  rr:objectMap [
  rml:reference "Dimensioni"
 ]
].




```
	
</details>





## Source [4](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json)


### Structure of the input files


```
[
...
{
	"Inventario": "1110/B",
	"Autore": "Ignoto",
	"Ambito culturale": "",
	"Datazione": "inizio XVI secolo",
	"Titolo-soggetto": "Abramo con tre angeli",
	"Materiali": "bronzo",
	"Immagine": "http://93.62.170.226/foto/1110_B.jpg",
	"lsreferenceby": "http://www.palazzomadamatorino.it/it/node/24055"
}
...
]

```

### Competency questions

#### CQ4: What are the identifiers of the artworks made of "bronzo"?


<details><summary>SPARQL Generate</summary>
	
```
	
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?id
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.Materiali") AS ?obj ?id ?material
WHERE{
  FILTER(REGEX(?material,".*bronzo.*","i"))
}

```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT DISTINCT ?id
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Materiali ?material .
		?s source:Inventario ?id .
		FILTER(REGEX(?material,".*bronzo.*","i"))
	}
}


```

</details>


#### CQ5: What are the identifiers of the artworks whose subject is "manifattura Hochst"?

<details><summary>SPARQL Generate</summary>

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?id
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.['Ambito culturale']") AS ?obj ?id ?subject
WHERE{
	FILTER(REGEX(?subject,".*manifattura Hochst.*","i"))
}
```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT DISTINCT ?id
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s ?p ?subject .
		?s source:Inventario ?id .
		FILTER(REGEX(?subject,".*manifattura Hochst.*","i"))
		FILTER(str(?p) = "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/Ambito culturale")
	}
}


```

</details>



#### CQ6: How many artworks are made of bronzo?


<details><summary>SPARQL Generate</summary>

```

PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT  (count(DISTINCT ?id) AS ?numberOfMadeArtworksMadeOfBronzo)
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.Materiali") AS ?obj ?id ?material
WHERE{
  FILTER(REGEX(?material,".*bronzo.*","i"))
}

```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT DISTINCT ?id
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Materiali ?material .
		?s source:Inventario ?id .
		FILTER(REGEX(?material,".*bronzo.*","i"))
	}
}



```

</details>



#### CQ7: How many artworks each author has made?

<details><summary>SPARQL Generate</summary>

```

PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT  ?author (count(DISTINCT ?id) AS ?numberOfWorks)
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.Autore") AS ?obj ?id ?author
WHERE{

} GROUP BY ?author


```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT DISTINCT ?author (count(DISTINCT ?id) AS ?numberOfWorks)
WHERE{

	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Autore ?author .
		?s source:Inventario ?id .
	}
} GROUP BY ?author



```

</details>



#### CQ8: What is the average number of artworks per author?

<details><summary>SPARQL Generate</summary>

```

???


```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT (AVG(?numberOfWorks) AS ?averageNumberOfWorksPerAuthor) { 
	{
		SELECT DISTINCT ?author (count(DISTINCT ?id) AS ?numberOfWorks)
		WHERE{

			SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
				?s source:Autore ?author .
				?s source:Inventario ?id .
			}
		} GROUP BY ?author
	}
}



```

</details>



### RDF Generation

#### Target model

```
@prefix ex: <http://example.org/> .

_:0 ex:Ambito_culturale "";
  ex:Autore "Ignoto";
  ex:Datazione "inizio XVI secolo";
  ex:Immagine "http://93.62.170.226/foto/1110_B.jpg";
  ex:Inventario "1110/B";
  ex:Materiali "bronzo";
  ex:Titolo-soggetto "Abramo con tre angeli";
  ex:lsreferenceby "http://www.palazzomadamatorino.it/it/node/24055" .
```

<details><summary>SPARQL generate for source 4</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX ex: <http://exmaple.org/>


GENERATE {
[] ex:Inventario ?id ;
   ex:Autore ?autore ;
	 ex:Ambito_Culturale ?ambito_culturale ;
	 ex:Datazione ?datazione ;
	 ex:Titolo-soggetto ?titolo ;
   ex:Materiali ?material ;
	 ex:Immagine ?immagine ;
	 ex:lsreferenceby ?lsreferenceby .
}
ITERATOR ite:JSONPath(<https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.Autore","$['Ambito Culturale']","$.Datazione","$.Titolo-soggetto","$.Materiali","$.Immagine","$.lsreferenceby") AS ?obj ?id ?autore ?ambito_culturale ?datazione ?titolo  ?material ?immagine ?lsreferenceby


```
	
</details>

<details><summary>SPARQL Anything for source 4</summary>
	
```

CONSTRUCT {
	?s ?p ?o
} WHERE {
	SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s ?p ?o .
	}
}



```

</details>



<details><summary>RML for source 4</summary>
	
```
@prefix rml: <http://semweb.mmlab.be/ns/rml#> .
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix ql: <http://semweb.mmlab.be/ns/ql#> .
@prefix : <http://example.org/rules/> .
@prefix ex: <http://example.org/> .

:TriplesMap a rr:TriplesMap;
  rml:logicalSource [
    rml:source "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json";
    rml:referenceFormulation ql:JSONPath;
    rml:iterator "$.[*]"
  ].

:TriplesMap rr:subjectMap [
  rr:termType rr:BlankNode 
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Inventario ;
  rr:objectMap [
  rml:reference "Inventario"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Autore ;
  rr:objectMap [
  rml:reference "Autore"
 ]
].



:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Ambito_culturale ;
  rr:objectMap [
  rml:reference "Ambito culturale"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Datazione ;
  rr:objectMap [
  rml:reference "Datazione"
 ]
].


:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Titolo-soggetto ;
  rr:objectMap [
  rml:reference "Titolo-soggetto"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Materiali ;
  rr:objectMap [
  rml:reference "Materiali"
 ]
].

:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:Immagine ;
  rr:objectMap [
  rml:reference "Immagine" ;
 ]
].


:TriplesMap rr:predicateObjectMap [
  rr:predicate ex:lsreferenceby ;
  rr:objectMap [
  rml:reference "lsreferenceby"
 ]
].
```
	
</details>



