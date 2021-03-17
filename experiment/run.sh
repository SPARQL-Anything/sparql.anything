SPARQL_ANYTHING_VERSION=0.0.5-SNAPSHOT
#SPARQL_ANYTHING_VERSION=0.1.0-SNAPSHOT
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
	   	eval $($1 >/dev/null)
	   	t1=$(gdate +%s%3N)
	   	total=$(($total+$t1-$t0))
	   	#echo "test $i $1 $(($t1-$t0))ms"
	done
	echo "Average: $1 $(($total/3)) ms"
}

JVM_ARGS=


m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q1.rqg -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q2.rqg -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q3.rqg -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q4.rqg -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q5.rqg -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q6.rqg -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q7.rqg -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q8.rqg -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q9.rqg -fo TTL -o generated-data/sparql-generate-q9.ttl -l INFO"
m "java  $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q10.rqg -fo TTL -o generated-data/sparql-generate-q10.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q11.rqg -fo TTL -o generated-data/sparql-generate-q11.ttl -l INFO"
m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12.rqg -fo TTL -o generated-data/sparql-generate-q12.ttl -l INFO"

m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m1.ttl -s turtle -o generated-data/rml-m1.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m2.ttl -s turtle -o generated-data/rml-m2.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m3.ttl -s turtle -o generated-data/rml-m3.ttl"
m "java $JVM_ARGS -jar bin/rmlmapper.jar -m rml-mappings/m4.ttl -s turtle -o generated-data/rml-m4.ttl"

m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q1.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q2.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q3.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q4.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q5.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q6.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q7.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q8.rqg"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q9.rqg -f TTL -o generated-data/sparql-anything-q9.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q10.rqg -f TTL -o generated-data/sparql-anything-q10.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q11.rqg -f TTL -o generated-data/sparql-anything-q11.ttl"
m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q12.rqg -f TTL -o generated-data/sparql-anything-q12.ttl"
