#!/bin/bash
#shell script to start processor
echo 'Starting server...'
nohup $JAVA8_HOME/bin/java -Xms512m -Xmx2046m -jar npx2-paynow-1.0.0-SNAPSHOT.jar -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:logs/gclog.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=20 -XX:GCLogFileSize=5000k  --debug > /dev/null 2>&1 & echo $! > run.pid
echo 'Server started successfully'+$(cat run.pid)

