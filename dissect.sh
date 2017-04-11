#!/usr/bin/env sh

if [ -z ${1+x} ]; then
  echo "Usage: run.sh <experiment_id>"
  exit 1
else
  EXPERIMENT_ID=$1
fi

echo "Running experiment $EXPERIMENT_ID"

./gradlew :clean :massacre$EXPERIMENT_ID

JAR_FILE=build/libs/massacre-$EXPERIMENT_ID.jar

START=`date +%s`

java -XX:NativeMemoryTracking=summary -javaagent:$JAR_FILE -jar $JAR_FILE &

JAVA_PID=$!

trap "kill -9 $JAVA_PID" EXIT

for i in `seq 1 60`; do
  output_file=exp.$EXPERIMENT_ID.null.$i.txt
  jcmd $JAVA_PID VM.native_memory summary > $output_file

  T=`expr $(date +%s) - $START`
  RSS=`ps -o rss -p $JAVA_PID | grep -v RSS`
  VMS=`grep 'Total:' $output_file`

  printf "T+%05dT: RSS=%010dK; JVM=%s;" "$T" "$RSS" "$VMS"
  echo "" # line break

  sleep 1
done

# TODO: force-kill?
kill $JAVA_PID
