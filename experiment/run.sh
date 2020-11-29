if [[ ! -e "bin" ]]; then
	echo "bin not exists"
	mkdir bin
	cd bin/
	curl -OL  https://github.com/sparql-generate/sparql-generate/releases/download/2.0.1/sparql-generate-2.0.1.jar
	curl -OL https://github.com/RMLio/rmlmapper-java/releases/download/v4.9.0/rmlmapper.jar
	cd ..
fi
if [[ ! -e "generated-data" ]]; then
	mkdir generated-data
fi
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q1.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q2.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q3.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q4.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q5.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q6.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q7.rqg -l INFO
#java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q8.rqg -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q9.rqg -fo TTL -o generated-data/q9.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q10.rqg -fo TTL -o generated-data/q10.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q11.rqg -fo TTL -o generated-data/q11.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12.rqg -fo TTL -o generated-data/q12.ttl -l INFO
#java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/test-aggregates.rqg -l INFO


java -jar bin/rmlmapper.jar -m rml-mappings/m1.ttl -s turtle -o generated-data/m1.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m2.ttl -s turtle -o generated-data/m2.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m3.ttl -s turtle -o generated-data/m3.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m4.ttl -s turtle -o generated-data/m4.ttl
