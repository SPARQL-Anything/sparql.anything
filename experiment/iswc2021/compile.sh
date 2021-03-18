mvn -f ../../pom.xml clean install -DskipTests


THIS_FOLDER=$(pwd)
if [[ ! -e "bin" ]]; then
	mkdir bin
fi

cp ../../sparql.anything.cli/target/sparql-anything-$1.jar bin/


curl -OL https://github.com/spice-h2020/sparql.anything/releases/download/v$2/sparql-anything-$2.jar

mv sparql-anything-$2.jar bin/
