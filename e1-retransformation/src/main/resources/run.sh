#!/usr/bin/env sh

START=`date +%s`



java -verbose:gc -XX:+PrintGCTimeStamps -Xloggc:exp.$EXPERIMENT_ID.noa.gc.log -Djavaagent:$JAR_FILE -jar $JAR_FILE &

JAVA_PID=$!

trap "kill -9 $JAVA_PID" EXIT

for i in `seq 1 10`; do
  RSS=`ps -o rss -p $JAVA_PID | grep -v RSS`
  NOW=`date +%s`
  T=`expr $NOW - $START`

  printf "T+%05sT: RSS=%010sK" "$T" "$RSS"
  echo ""

  sleep 1
done

# TODO: force-kill?
kill $JAVA_PID
