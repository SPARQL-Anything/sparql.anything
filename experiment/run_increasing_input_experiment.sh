SPARQL_ANYTHING_VERSION=0.0.5-SNAPSHOT
if [[ ! -e "bin" ]]; then
	echo "bin not exists"
	mkdir bin
	cd bin/
	curl -OL  https://github.com/sparql-generate/sparql-generate/releases/download/2.0.1/sparql-generate-2.0.1.jar
	curl -OL https://github.com/RMLio/rmlmapper-java/releases/download/v4.9.0/rmlmapper.jar
	#mvn -f ../../pom.xml clean install
	#cp ../../sparql.anything.cli/target/sparql-anything-$SPARQL_ANYTHING_VERSION.jar .
	curl -OL https://github.com/spice-h2020/sparql.anything/releases/download/v$SPARQL_ANYTHING_VERSION/sparql-anything-$SPARQL_ANYTHING_VERSION.jar
	cd ..
fi
if [[ ! -e "generated-data" ]]; then
	mkdir generated-data
fi

function m() {

	total=0
	for i in 1 2 3
	do
		t0=$(gdate +%s%3N)
	   	eval $($1 > /dev/null)
	   	t1=$(gdate +%s%3N)
	   	total=$(($total+$t1-$t0))
	   	#echo "test $i $1 $(($t1-$t0))ms"
	done
	echo "Average: $1 $(($total/3)) ms"
}
JVM_ARGS=-Xmx10g
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_10.rqg -f TTL -o generated-data/sparql-anything-q12_10.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_100.rqg -f TTL -o generated-data/sparql-anything-q12_100.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_1000.rqg -f TTL -o generated-data/sparql-anything-q12_1000.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_10000.rqg -f TTL -o generated-data/sparql-anything-q12_10000.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_100000.rqg -f TTL -o generated-data/sparql-anything-q12_100000.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar  -q sparql-anything-queries/q12_1000000.rqg -f TTL -o generated-data/sparql-anything-q12_1000000.ttl"

m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_10.ttl -s turtle -o generated-data/rml-m4_10.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_100.ttl -s turtle -o generated-data/rml-m4_100.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_1000.ttl -s turtle -o generated-data/rml-m4_1000.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_10000.ttl -s turtle -o generated-data/rml-m4_10000.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_100000.ttl -s turtle -o generated-data/rml-m4_100000.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4_1000000.ttl -s turtle -o generated-data/rml-m4_1000000.ttl"

m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_10.rqg -fo TTL -o generated-data/sparql-generate-q12_10.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_100.rqg -fo TTL -o generated-data/sparql-generate-q12_100.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_1000.rqg -fo TTL -o generated-data/sparql-generate-q12_1000.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_10000.rqg -fo TTL -o generated-data/sparql-generate-q12_10000.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_100000.rqg -fo TTL -o generated-data/sparql-generate-q12_100000.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12_1000000.rqg -fo TTL -o generated-data/sparql-generate-q12_1000000.ttl -l INFO"
