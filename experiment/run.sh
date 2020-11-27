if [[ ! -e "bin" ]]; then
	echo "bin not exists"
	mkdir bin
	cd bin/
	curl -OL  https://github.com/sparql-generate/sparql-generate/releases/download/2.0.1/sparql-generate-2.0.1.jar
	cd ..
fi
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q1.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q2.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q3.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q4.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q5.rqg -l INFO