SPARQL_ANYTHING_VERSION=$1
SPARQL_ANYTHING_OLD_VERSION=$2
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

for q in 1 2 3
do
	for size in 10 100 1000 10000 100000 1000000
	do
			m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q sparql-anything-queries/q${q}_$size"
			m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_OLD_VERSION.jar -q sparql-anything-queries/q${q}_$size"
	done
done
