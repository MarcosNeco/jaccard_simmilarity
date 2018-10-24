#!/bin/sh
unzip_servers(){
  if [ ! -d zookeeper-3.4.12_server ]; 
    then
      echo "unzip zookeeper server"
      unzip zookeeper-3.4.12.zip
    fi

  if [ ! -d kafka_2.11_server ];
    then
      echo "unzip kafka server"
      unzip kafka_2.11.zip
    fi
}

start_servers(){
  echo "initializing zookeeper service"
  start /WAIT .\\zookeeper-3.4.12_server\\bin\\zkServer.cmd
  echo "waiting 10 seconds to start kafka server"
  sleep 10s
  echo "initializing kafka service"
  start /WAIT .\\kafka_2.11_server\\bin\\windows\\kafka-server-start.bat kafka_2.11_server\\config\\server.properties
}

build_project(){
  echo "clean and build project"
  (exec sbt clean publishLocal)
}

run_spark_streaming(){
  exec spark-submit --class com.jacsimm.core.JaccardSimilarityProcessor target/scala-2.11/jaccard_similarity_2.11-0.1.jar
}

run(){
  unzip_servers
  start_servers
  build_project
  run_spark_streaming
}

case "$1" in
   "run") run
   ;;
   *) echo "Usage[run]"
   ;;
esac
