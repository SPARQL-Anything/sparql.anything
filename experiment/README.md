# Experiment

Objective: Assessing the

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

##### SPARQL Generate

<details>
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


#### CQ2: What are the titles of the artworks made with the technique named "STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO"?

##### SPARQL Generate

<details>
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

#### CQ3: What are the titles of the artworks created in the 1935?

##### SPARQL Generate

<details>
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

What are the identifiers of the artworks sharing the


## Source [4](https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json)


### Structure of the input files


```
[
...
{
	"Inventario": "1110/B",
	"Autore": "Ignoto",
	"Ambito culturale": "",
	"Datazione":
	"inizio XVI secolo",
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

##### SPARQL Generate

<details>

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


#### CQ5: What are the identifiers of the artworks whose subject is "manifattura Hochst"?

##### SPARQL Generate

<details>

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
