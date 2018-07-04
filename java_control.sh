#!/bin/bash -l

CURRENT_PATH="/sccp/eln_pmb_bridge"
PID_FILE="/sccp/eln_pmb_bridge/java.pid"

function java_start() {
  if [ -f $PID_FILE ] && ps -p `cat $PID_FILE` >> /dev/null ; then
    echo 'Existing server appears to be running'
    exit $SERVER_ALREADY_EXISTS
  fi

  cd $CURRENT_PATH
  /sccp/jre/jre1.8.0_131/bin/java -jar eln_pmb_bridge-1.0-jar-with-dependencies.jar &
  PID=$!
  echo $PID > $PID_FILE
}

function java_stop() {
  cd $CURRENT_PATH
  kill -9  `cat $PID_FILE`
  echo 'Killed process'
  rm -f $PID_FILE
}

function java_restart() {
  cd $CURRENT_PATH
  java_stop
  java_start
  echo 'Performing hot restart'
}

java_${1}
