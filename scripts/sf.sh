#!/bin/sh

version=1.0.0-SNAPSHOT
env=prod
port=1010
node_range=0,1023
server_name=tinkertian-snowflake-server
jar_name=${server_name}-${version}.jar
pid_file=${server_name}-${port}.pid

case "$1" in
  start)
    echo "Starting snowflake service."
    nohup java -jar -Dfile.encoding=UTF-8 $jar_name --spring.profiles.active=${env} --server.port=${port} --snowflake.node.large=${node_range} --snowflake.node.small=${node_range} >/dev/null &
    echo $! >$pid_file
    ;;
  stop)
    if [ ! -f $pid_file ]; then
      echo "$pid_file does not exist, process is not running"
    else
      kill $(cat ${server_name}-${port}.pid)
      echo "Snowflake server stopped."
    fi
    ;;
  status)
    ps -ef | grep ${jar_name}
    ;;
  log)
    tail -f _logs/spring.log
    ;;
  restart)
    "$0" stop
    sleep 3
    "$0" start
    ;;
  *)
    echo "Please use start or stop or restart as first argument"
    ;;
esac
