#!/bin/bash -l

CURRENT_PATH="/sccp/eln_pmb_bridge"
PID_FILE="/sccp/eln_pmb_bridge/java.pid"
JAR_FILE="eln_pmb_bridge-jar-with-dependencies.jar"
JRE="/sccp/jre/jre1.8.0_131/bin/java"

function java_start() {
  if [ -f $PID_FILE ] && ps -p `cat $PID_FILE` >> /dev/null ; then
    echo 'Existing server appears to be running'
    exit 1
  fi

  cd $CURRENT_PATH
  $JRE -jar $JAR_FILE ${ENV} &
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

ENV=${2}
java_${1}
