mvn -f ../../pom.xml clean install -DskipTests
THIS_FOLDER=$(pwd)
if [[ ! -e "bin" ]]; then
	mkdir bin
fi

cp ../../sparql.anything.cli/target/sparql-anything-$1.jar bin/
