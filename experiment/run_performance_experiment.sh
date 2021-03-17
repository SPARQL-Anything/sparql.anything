mvn -f ../sparql.anything.experiment/pom.xml clean install
THIS_FOLDER=$(pwd)
mvn -f ../sparql.anything.experiment/pom.xml exec:java  -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.experiment.TestDataCreator" -Dexec.args="$THIS_FOLDER"

echo "RUN CQ experiment"
sh run.sh

echo "RUN increasing input experiment"
sh run_increasing_input_experiment.sh
