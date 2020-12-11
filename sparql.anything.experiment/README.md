For generating synthetic experimental data:

```
mvn clean install
mvn exec:java  -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.experiment.TestDataCreator" -Dexec.args="[OUT_FOLDER]" 
```

For computing the statistincs about the number of tokens and distinct tokens within a query

```
mvn clean install
mvn exec:java  -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.experiment.ComputeStats"  -Dexec.args="/path/to/experiment/folder" 
```