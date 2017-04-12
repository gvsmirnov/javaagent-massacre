#!/usr/bin/env sh

if [ -z ${1+x} ]; then
  echo "Usage: run.sh <experiment_id>"
  exit 1
else
  EXPERIMENT_ID=$1
fi

echo "Running experiment $EXPERIMENT_ID"

./gradlew :clean :massacre-$EXPERIMENT_ID

JAR_FILE=build/libs/massacre-$EXPERIMENT_ID.jar

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
