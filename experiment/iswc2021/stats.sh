#!/bin/bash
#
# Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

logfile=$1

systems=( sparql-anything-0.0.4 sparql-anything-0.1.0-SNAPSHOT sparql-generate-2.0.1 )

for s in ${systems[@]}
do
  for q in 1 2 3
  do
    for size in 10 100 1000 10000 100000 1000000
    do
      log=logs/${s}_q${q}_${size}.log
      # AVG time already computed
      avg=$(cat $log|grep ',avg,'|cut -d',' -f5)
      CPU=0
      RSS=0
      for e in 1 2 3
      do
#        sleep 0.02
        # pid,%cpu,%mem,vsz,rss
        logfile=$log.${e}
        # CPU
        #        echo $logfile
        c=$(cat "$logfile" |sed 's/^ *//;s/ *$//'|cut -d" " -f2 |sort -n |tail -n 1)
        r=$(cat "$logfile" |sed 's/^ *//;s/ *$//'|cut -d" " -f5 |sort -n |tail -n 1)
#        echo $logfile
#        echo "$CPU + $c"
#        echo "$RSS + $r"
        CPU=$(echo "$CPU + $c" | bc)
        RSS=$(echo "$RSS + $r" | bc)
      done
      echo $s,$q,$size,$avg,$(echo "$CPU / 3" | bc),$(echo "$RSS / 3" | bc)
    done
  done

done