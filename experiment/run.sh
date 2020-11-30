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
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q9.rqg -fo TTL -o generated-data/sparql-generate-q9.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q10.rqg -fo TTL -o generated-data/sparql-generate-q10.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q11.rqg -fo TTL -o generated-data/sparql-generate-q11.ttl -l INFO
java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/q12.rqg -fo TTL -o generated-data/sparql-generate-q12.ttl -l INFO
#java -jar bin/sparql-generate-2.0.1.jar -q sparql-generate-queries/test-aggregates.rqg -l INFO


java -jar bin/rmlmapper.jar -m rml-mappings/m1.ttl -s turtle -o generated-data/rml-m1.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m2.ttl -s turtle -o generated-data/rml-m2.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m3.ttl -s turtle -o generated-data/rml-m3.ttl
java -jar bin/rmlmapper.jar -m rml-mappings/m4.ttl -s turtle -o generated-data/rml-m4.ttl

mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q1.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q2.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q3.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q4.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q5.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q6.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q7.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q8.rqg"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q9.rqg -f TTL -o generated-data/sparql-anything-q9.ttl"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q10.rqg -f TTL -o generated-data/sparql-anything-q10.ttl"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q11.rqg -f TTL -o generated-data/sparql-anything-q11.ttl"
mvn exec:java  -f ../sparql.anything.engine/pom.xml -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="com.github.spiceh2020.sparql.anything.engine.SPARQLAnything" -Dexec.args="-q sparql-anything-queries/q12.rqg -f TTL -o generated-data/sparql-anything-q12.ttl"  
