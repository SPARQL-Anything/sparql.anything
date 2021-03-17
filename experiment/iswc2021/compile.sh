mvn -f ../../pom.xml clean install
THIS_FOLDER=$(pwd)
if [[ ! -e "bin" ]]; then
	mkdir bin
fi

mv ../../sparql.anything.cli/target/sparql-anything-$1.jar bin/
