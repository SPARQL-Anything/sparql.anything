# Competency Questions

Objective: Assessing the

## SPARQL Generate

### Sources: [1](http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_FONDO_GABINIO_MARZO_2017.json) [2](http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_GAM.json) [3](http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_MAO.json)



What are the titles of the artworks attributed to "ANONIMO"?

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Autore","$.Titolo") AS ?obj ?autore ?titolo
WHERE{FILTER(REGEX(?autore,".*ANONIMO.*","i"))}
```


What are the titles of the artworks made with the technique named "STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO"?

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Tecnica","$.Titolo") AS ?obj ?technique ?titolo
WHERE{FILTER(REGEX(?technique,".*STAMPA ALLA GELATINA CLOROBROMURO D'ARGENTO.*","i"))}
```

What are the titles of the artworks created in the 1935?

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?titolo
ITERATOR ite:JSONPath(<http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_FONDO_GABINIO_MARZO_2017%20json.json>,"$[*]","$.Datazione","$.Titolo") AS ?obj ?date ?titolo
WHERE{FILTER(REGEX(?date,".*1935.*","i"))}
```

What are the identifiers of the artworks sharing the


### COLLEZIONI_PALAZZO_MADAMA_MARZO_2017.json

What are the identifiers of the artworks made of "bronzo"?

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?id
ITERATOR ite:JSONPath(<http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.Materiali") AS ?obj ?id ?material
WHERE{FILTER(REGEX(?material,".*bronzo.*","i"))}
```



What are the identifiers of the artworks whose subject is "manifattura Hochst"?

```
PREFIX ite: <http://w3id.org/sparql-generate/iter/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>


SELECT DISTINCT ?id
ITERATOR ite:JSONPath(<http://arco.istc.cnr.it/linked-data-hub-notebooks/COLLEZIONI_PALAZZO_MADAMA_marzo2017.json>,"$[*]","$.Inventario","$.['Ambito culturale']") AS ?obj ?id ?subject
WHERE{
	FILTER(REGEX(?subject,".*manifattura Hochst.*","i"))
}
```




- No cleansing, no normalization, no reenginering of input data
