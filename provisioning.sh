#!/bin/sh
unzip_servers(){
  if [ ! -d zookeeper-3.4.12_server ]; 
    then
      unzip zookeeper-3.4.12.zip
    fi

  if [ ! -d kafka_2.11_server ];
    then
      unzip kafka_2.11.zip
    fi
}

start_servers(){
  echo "initializing zookeeper service"
  start ./zookeeper-3.4.12_server/bin/zkServer.sh
  echo "initializing kafka service"
  start .\\kafka_2.11_server\\bin\\windows\\kafka-server-start.bat \\kafka_2.11_server\\config\\server.properties
}

run(){
  unzip_servers
  start_servers
}

case "$1" in
   "run") run
   ;;
   *) echo "Usage[run]"
   ;;
esac
