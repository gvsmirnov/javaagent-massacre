#!/usr/bin/env sh

./gradlew :clean :massacre1 

java -XX:NativeMemoryTracking=summary -javaagent:build/libs/massacre-1.jar -jar build/libs/massacre-1.jar &

JAVA_PID=$!

for i in `seq 1 10`; do
  output_file=mem.with.retroagent.$i.txt

  date | tee $output_file
  jcmd $JAVA_PID VM.native_memory summary | tee -a $output_file | grep 'Total:'

  echo  "----------------------------------------------------------"
  sleep 1
done

# TODO: force-kill?
kill $JAVA_PID

trap "kill -9 $JAVA_PID" EXIT
