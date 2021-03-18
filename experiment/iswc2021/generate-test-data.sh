mvn -f ../../sparql.anything.experiment/pom.xml clean install
THIS_FOLDER=$(pwd)
mvn -f ../../sparql.anything.experiment/pom.xml exec:java  -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.experiment.TestDataCreatorForISWC2021" -Dexec.args="$THIS_FOLDER"
