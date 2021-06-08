# Evaluation

We conducted a comparative evaluation of sparql.anything with respect to the state of art methods RML and SPARQL Generate.

## Cognitive Complexity Comparison

*Objective*: The objective of the experiment is to compare sparql.anything with [SPARQL generate](https://ci.mines-stetienne.fr/sparql-generate/) and [RML](https://rml.io/) frameworks in terms of usability and learnability of the frameworks. Specifically,  from the data sources of the SPICE project  we selected four non-RDF resources and for each resource we defined two kinds of tests: one aimed at assessing the usability and learnability of sparql.anything and [SPARQL generate](https://ci.mines-stetienne.fr/sparql-generate/) in retrieving data, the other meant at evaluating  the usability and learnability of three approaches   in generating data. 

*Approach*: As far as retrieval tests is concerned, we inspected the resources in order to identify a set of possible competency questions a user may want to ask. Then, for each competency question we defined the corresponding query according to sparql.anything and [SPARQL generate](https://ci.mines-stetienne.fr/sparql-generate/) frameworks.
Concerning the generation tests, for each resource we defined a target RDF model for exporting the data and the rules needed for transforming the data according to each of the compared frameworks.

*Results*: One effective measure of complexity is the number of distinct items or variables that need to be combined within a query or expression [(Halford et al. 2004)](https://www.tandfonline.com/doi/pdf/10.1080/13546780442000033?casa_token=4fEYMB3PswAAAAAA:wfaeKgz51sDOGKdq2KWDn38Iu-Pah0iGmXxMoG6SJIu1Zxv9PR7fcTuFLCdGTnNgiyh8YhamfjeZ). Such a measure of complexity has previously been used to explain difficulties in the comprehensibility of Description Logic statements [(Warren et al. 2015)](https://dl.acm.org/doi/abs/10.1145/2814864.2814866?casa_token=BLtmqOwo4ZUAAAAA:DYqfYy_tnY2GebHD2aG7NBDt2MjT6raKKBNJsrQj1HPofuFnVaykpETzu-PA-YPaShIUT1cUujU). 
Specifically, we counted the number of tokens needed for expressing a set of competency questions.
The queries were tokenized (by using ``"(){},;{}\n\t\r `` as token delimiters) and we computed the total number of tokens  and the number of distinct tokens needed for each queries. 



![Number of Distinct Token per Query](/experiment/img/chart.png)![Number of Distinct Token per Query](/experiment/img/number_of_tokens.png)


*Running the experiment*: The code implementing the tokenizer that has been used for the experiment can be found in the [sparql.anything.experiment module](/sparql.anything.experiment).
The experiment can be run with following command:

```
./run_cog_experiment.sh
```


## Performance Comparison
We assessed the performace of sparql.anything, [SPARQL generate](https://ci.mines-stetienne.fr/sparql-generate/) and [RML](https://rml.io/) frameworks in retrieving and generating RDF data.  All of the tests described below were run three times and the average time among the three executions is reported.
The tests were executed on a MacBook Pro 2020 (CPU: i7 2.3 GHz, RAM: 32GB).

### Performace in retrieving RDF data
The following Figure shows the time needed for evaluating the SELECT queries q1-q8 and for generating the RDF triples according to the CONSTRUCT queries/mapping rules q9-q12.

![Execution time per  query](/experiment/img/execution_time_queries.png)

### Performance in generating RDF data

We also measured the performance in transforming input of increasing size. 
To do so, we  repeatedly concatenated the data sources in order to obtain a JSON array containing 1M JSON objects and we cut this array at length 10, 100, 1K, 10K and 100K.
We ran the query/mapping q12 on these files and we measured the execution time which is shown in the following figure:

![Execution time per  query](/experiment/img/execution_time_increasing_input.png)


All the perfomance comparisons can be run with the following command:

```
./run_performance_experiment.sh
```


## Queries

In the following the queries used for the experiment are reported.

### Sources: [1](/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json) [2](/experiment/data/COLLEZIONI_GAM.json) [3](/experiment/data/COLLEZIONI_MAO.json)

#### Structure of the input files

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

#### Competency questions

##### CQ1: What are the titles of the artworks attributed to "ANONIMO"?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Autore "ANONIMO" .
		?s source:Titolo ?titolo .
	}
}


```

</details>


##### CQ2: What are the titles of the artworks made with the technique named "STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO"?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>


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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Tecnica ?technique .
		?s source:Titolo ?titolo .
		FILTER(REGEX(?technique,".*STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO.*","i"))
	}
}



```

</details>

##### CQ3: What are the titles of the artworks created in the 1935?


<details><summary>SPARQL Generate</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?s source:Datazione ?date .
		?s source:Titolo ?titolo .
		FILTER(REGEX(?date,".*1935.*","i"))
	}
}



```

</details>

#### RDF Generation

##### Target model

```
@prefix ex: <http://example.org/> .
_:0 ex:Autore "GABINIO MARIO";
  ex:Datazione "19/10/1923";
  ex:Dimensioni "229X169";
  ex:Immagine "http://93.62.170.226/foto/gabinio/001B1.jpg";
  ex:Tecnica "STAMPA ALLA CELLOIDINA";
  ex:Titolo "TORINO/ MONUMENTO A CARLO ALBERTO, PIAZZA CARLO ALBERTO, VISTA LATERALE DESTRA" .
```

<details><summary>SPARQL generate for source 1 (q10)</summary>
	
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



<details><summary>SPARQL generate for source 2 (q11)</summary>
	
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



<details><summary>SPARQL generate for source 3 (q12)</summary>
	
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

<details><summary>SPARQL Anything for source 1 (q10)</summary>
	
```

PREFIX ex: <http://exmaple.org/>
CONSTRUCT {
	_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
           ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
            ex:Titolo    ?t .
} WHERE {
	SERVICE <facade-x:namespace=http://exmaple.org/,location=https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json> {
		?arr ?p _:b .
		_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
            ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
           ex:Titolo    ?t .
	}
}

```

</details>

<details><summary>SPARQL Anything for source 2 (q11)</summary>
	
```
PREFIX ex: <http://exmaple.org/>
CONSTRUCT {
	_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
           ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
            ex:Titolo    ?t .
} WHERE {
	SERVICE <facade-x:namespace=http://exmaple.org/,location=https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json> {
		?arr ?p _:b .
		_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
            ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
           ex:Titolo    ?t .
	}
}

```

</details>

<details><summary>SPARQL Anything for source 3 (q12)</summary>
	
```
PREFIX ex: <http://exmaple.org/>
CONSTRUCT {
	_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
           ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
            ex:Titolo    ?t .
} WHERE {
	SERVICE <facade-x:namespace=http://exmaple.org/,location=https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json> {
		?arr ?p _:b .
		_:b  ex:Autore     ?a ;
            ex:Datazione  ?d ;
            ex:Dimensioni ?dim ;
            ex:Immagine   ?im ;
            ex:Tecnica    ?s ;
           ex:Titolo    ?t .
	}
}

```

</details>

<details><summary>RML for source 1 (q10)</summary>
	
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


<details><summary>RML for source 2 (q11)</summary>
	
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

<details><summary>RML for source 3 (q12)</summary>
	
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





### Source [4](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json)


#### Structure of the input files


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

#### Competency questions

##### CQ4: What are the identifiers of the artworks made of "bronzo"?


<details><summary>SPARQL Generate</summary>
	
```
	
PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Materiali ?material .
		?s source:Inventario ?id .
		FILTER(REGEX(?material,".*bronzo.*","i"))
	}
}



```

</details>


##### CQ5: What are the identifiers of the artworks whose subject is "manifattura Hochst"?

<details><summary>SPARQL Generate</summary>

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s ?p ?subject .
		?s source:Inventario ?id .
		FILTER(REGEX(?subject,".*manifattura Hochst.*","i"))
		FILTER(str(?p) = "https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/Ambito culturale")
	}
}



```

</details>



##### CQ6: How many artworks are made of bronzo?


<details><summary>SPARQL Generate</summary>

```

PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Materiali ?material .
		?s source:Inventario ?id .
		FILTER(REGEX(?material,".*bronzo.*","i"))
	}
}



```

</details>



##### CQ7: How many artworks each author has made?

<details><summary>SPARQL Generate</summary>

```

PREFIX ite: <http://w3id.org/sparql-generate/iter/>

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

	SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?s source:Autore ?author .
		?s source:Inventario ?id .
	}
} GROUP BY ?author


```

</details>



##### CQ8: What is the average number of artworks per author?

<details><summary>SPARQL Generate</summary>

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>

SELECT (AVG(?averageOfWorksPerAuthor) AS ?averageOfWorksPerAuthor)
ITERATOR <sparql-generate-queries/q7.rqg>() AS ?author ?id ?numberOfWorks
WHERE {

}

```

</details>

<details><summary>SPARQL Anything</summary>
	
```

PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json/>

SELECT (AVG(?numberOfWorks) AS ?averageNumberOfWorksPerAuthor) {
	{
		SELECT DISTINCT ?author (count(DISTINCT ?id) AS ?numberOfWorks)
		WHERE{

			SERVICE <facade-x:https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
				?s source:Autore ?author .
				?s source:Inventario ?id .
			}
		} GROUP BY ?author
	}
}

```

</details>



#### RDF Generation

##### Target model

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

<details><summary>SPARQL generate for source 4 (q9)</summary>
	
```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX ex: <http://exmaple.org/>


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

<details><summary>SPARQL Anything for source 4 (q9)</summary>
	
```

PREFIX ex: <http://exmaple.org/>
CONSTRUCT {
	_:b ex:Inventario ?in .
		_:b ex:Autore ?a .
		_:b <http://exmaple.org/Ambito_culturale> ?ac .
		_:b ex:Datazione ?d .
		_:b ex:Titolo-soggetto ?t .
		_:b ex:Materiali ?m .
		_:b ex:Immagine ?i .
		_:b ex:lsreferenceby ?ref .
} WHERE {
	SERVICE <facade-x:namespace=http://exmaple.org/,location=https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json> {
		?arr ?p  _:b .
		_:b ex:Inventario ?in .
		_:b ex:Autore ?a .
		_:b ?acp ?ac .
		_:b ex:Datazione ?d .
		_:b ex:Titolo-soggetto ?t .
		_:b ex:Materiali ?m .
		_:b ex:Immagine ?i .
		_:b ex:lsreferenceby ?ref .
		FILTER(str(?acp)="http://exmaple.org/Ambito culturale")
	}
}

```

</details>



<details><summary>RML for source 4 (q9)</summary>
	
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


