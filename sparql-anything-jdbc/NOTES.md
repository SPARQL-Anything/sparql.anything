## FX

```turtle
:table1 a fx:table1 ;
  rdf:_1 [
    fx:col1 "colvalue" ;
    fx:col2 "12"^^xsd:int ;
    fx:col3 "12/12/2022"^^xsd:date
  ]
```

## Definitions

Container: a FX container
ContainerTable: URI of the entity representing the table (the root FX container)
ContainerRow: a blank node representing any row in the table
TypeProperty: the RDF type predicate
TypeTable: the range of the RDF Type property, pointing to the table name in the database
SlotRow: a container membership property linking a ContainerTable to any of its ContainerRow
SlotColumn: an RDF property named after a table column linking a ContainerRow to the SlotValue (the typed literal from the column value)
SlotValue: an RDF typed literal (the column value in the database table)
Join: some roles can be joined together. E.g. ContainerRow(S) ^ ContainerRow(O). Not all assumption can be joined. Same assumptions in different triples can always be joined. Rules for inferring valid joined below.

## Interpreting basic graph patterns

T <- ( S, P, O )
S <- URI|BN
O <- URI|BN|TL
CMP <- URI(rdf:\_\*)
P <- URI
BGP <- { T }


### Generate constraints
;; Inferring the role of S
ContainerTable(S) <- URI(S) | FXRoot(O) | SlotRow(P) | ContainerRow(O)
ContainerRow(S) <- SlotColumn(P) | SlotValue(O) | TypeTable(O)
;; Inferring role of P
SlotColumn(P) <- SlotValue(O)
TypeProperty(P) <- TypeTable(O) | FXRoot(O)
SlotRow(P) <- ContainerRow(O)
SlotRow(P) <- CMP(P)
SlotColumn(P) <- URI(fx:\*)
TypeProperty(P) <- URI(rdf:type)
;; Inferring the role of O
SlotValue(O) <- SlotColumn(P)
ContainerRow(O) <- SlotRow(P) 
TypeTable(O) <- URI(O)
SlotValue(O) <- TL(O)
;; Making joins
Join(n) <- Assumption(n) ^ Assumption(n) -- on different triples but same Assumption type
;; Inferring joins from different assumption types
Join(n) <- ContainerRow(n) ^ ContainerRow(n) <- Subject(n) ^ Object(n)
Join(n) <- ContainerRow(n) ^ ContainerRow(n) <- Object(n) ^ Subject(n)
;; there are not other cases where joins with different types make sense (proof!)


Wrong:
;; TypeTable(O) <- TypeProperty(P)
;; ContainerRow(O) <- BN(O) // Not true, in SPARQL matches also values

### Specialising in
Subject -> ContainerTable | ContainerRow
Predicate -> SlotRow | SlotColumn | TypeProperty
Object -> ContainerRow | SlotValue | FXRoot | TypeTable

### Guards
ContainerTable(S) -> !SlotColumn(P) & !SlotValue(O)


## Search the space of interpretations

State = Assumption[] ^ Hypothesis[] (the set of constraints)
expand = generate next Stete by adding hypothesis (trying to specialise them as much as possible)
Guard = checks for inconsistencies and avoids following inconsistent paths
Visited = keeps the record of visited States (both valid and invalid)
Proceed until all BGPInterpretations are visited (brute force)

next (x) 
  Uses SpecialiseHypothesis to return a new State, 
     which keeps track of the previous State
  Uses Guard to validate the new State
  returns False if invalid, otherwise the new State

follow(Stete y)
  while ( State s <- next(y) )
    if visited(s) continue
    else follow(s) 

## Preliminary notes

?s ?p ?o

S ?p ?o
?s P ?o -> IF P anySlot Then ?s is containerTable ELSE ?s is containerRow AND OP is column name AND ?o is columnValue(P)
?s ?p O

S P ?o
?s P O
S ?p O

S P O


BGP -> BindingsWithRules ?s tableContainer && ?o valueFromTableXandColumnY && -> JoinAnalysis -> Query Assembly


Functions:
- container to table -- OK
- table to container -- OK
- container to rownum -- OK
- rownum to container -- OK
- P to column
- ?p to column set
- P to rownum
- ?p to row set
- Ptype to tables (or P O to table)

[] a xyz:table1 ;
fx:anySlot [
xyz:col1 ?value  ] .

select table1.col1 from table1 limit 1 offset

?table rdf:_54 ?row . ?row xyz:col1 ?value

SELECT COL1 from table1 order by (primary key) limit 1 offset 54

?s fx:anySlot [ ?p ?o ]

NodeInterpretation:
- ContainerTable (Only S) (KnownTable or AnyTable)
- SlotRow (P) (KnownRow or AnyRow)
- ContainerRow (O or S) (KnownTable or AnyTable, KnownRow or AnyRow)
- SlotColumn (P) (KnownColumn or AnyColumn)
- Value (O) (KnownValue or AnyValue)

TripleInterpretation:
ContainerTable | ContainerRow
SlotRow | SlotColumn
ContainerTable | ContainerRow | Value

NodeVarJoin
- var name
- nodes


?s ?p ?o => ?s is table, ?p is CMP, ?o is row
?o ?x ?y => ?o is row, ?x is COLUMN, ?y is value
?g ?x ?q = ?r is row, ?x is COLUMN, ?q is value

Bindings (?s ?p ?o ?x ?y ?g ?q)

SELECT A.X FROM TABLE1 A, TABLE1 B where A.X=B.X
SELECT A.X FROM TABLE2 A, TABLE2 B where A.X=B.X
SELECT A.X FROM TABLE1 A, TABLE2 B where A.X=B.X

SELECT * FROM TABLE1
SELECT * FROM TABLE3

?p -> SlotColumn ( col1, col2, col3)