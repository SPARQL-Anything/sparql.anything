SPARQL_ANYTHING_VERSION=${1:-0.1.0-SNAPSHOT}
SPARQL_ANYTHING_OLD_VERSION=${2:-0.0.4}

function errcho {
    >&2 echo "$@"
}

function mem {
	process=$1
  MS=${2:-600}
	MPID=$1
	errcho "Monitoring $MPID (will interrupt in $MS seconds)"
	kill="pkill -TERM -P $process"
	if [ ! -z "$process" ]
	then
	  SECONDS=0
	  while kill -0 $process 2> /dev/null; do
		[ "$SECONDS" -gt "$MS" ] && break || errcho -n "."
		ps -p $MPID -o pid,%cpu,%mem,vsz,rss|sed 1d
	  done
	  [ "$SECONDS" -gt "$MS" ] && $kill && errcho " Interrupted." || errcho " Done."
	fi
}

function m() {
  memlog=${2:-mem.log}
  size=${3:-0}
	total=0
	rm -f $memlog*
	#echo "" >> $memlog
	for i in 1 2 3
	do
		t0=$(gdate +%s%3N)
		echo "$1,$size,$i,start,$t0" >> $memlog
	  $1 > /dev/null &       #> "$memlog.out.$i" & # WITHOUT STDOUT
	  mem $! > $memlog.$i
	  t1=$(gdate +%s%3N)
	  echo "$1,$size,$i,end,$t1" >> $memlog
	  tt=$(($t1-$t0))
	  total=$(($total+$tt))
	  echo "$1,$size,$i,time,$tt" >> $memlog
	  #echo "test $i $1 $(($t1-$t0))ms"
	done
  #	echo "Average: $1 $(($total/3)) ms"
	echo "$1,$size,$i,avg,$(($total/3))" >> $memlog
}


JVM_ARGS=

#m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q queries/q1_10000.rqg" logs/sparql-generate-2.0.1.log 10000
#m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q queries/q1_10000.sparql" logs/sparql-anything-0.1.0-SNAPSHOT.log 10000
#m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_OLD_VERSION.jar -q queries/q1_10000.sparql" logs/sparql-anything-0.0.4.log 10000
#exit 1

for q in 1 2 3
do
	for size in 10 100 1000 10000 100000 1000000
	do
	    m "java $JVM_ARGS -jar bin/sparql-generate-2.0.1.jar -q queries/q${q}_${size}.rqg" logs/sparql-generate-2.0.1_q${q}_$size.log $size
      m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_VERSION.jar -q queries/q${q}_${size}.sparql" logs/sparql-anything-${SPARQL_ANYTHING_VERSION}_q${q}_${size}.log $size
      m "java $JVM_ARGS -jar bin/sparql-anything-$SPARQL_ANYTHING_OLD_VERSION.jar -q queries/q${q}_${size}.sparql" logs/sparql-anything-${SPARQL_ANYTHING_OLD_VERSION}_q${q}_${size}.log $size
	done
done
