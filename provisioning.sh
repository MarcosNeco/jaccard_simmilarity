#!/bin/sh



dowload_dep(){
}
run_ws() {
  echo "Starting recommendation-docs-ws"
  (cd recommendation-docs-ws/ && exec sbt dist);
  (cd recommendation-docs-ws/ && exec unzip target/universal/recommendation-docs-ws*.zip);
  (cd recommendation-docs-ws/ && exec nohup ./recommendation-docs-ws-*/bin/recommendation-docs-ws -Dplay.crypto.secret="recommendationglobo" -Dhttp.port=8080 &)
}

run_pipe() {
  echo "Starting recommendation-docs-pipe"
  (cd recommendation-docs-pipe/ && exec nohup /opt/spark-2.0.2-bin-hadoop2.7/bin/spark-submit --class com.globo.selecao.recommendation.streaming.JaccardIndexCalculation target/scala-2.11/recommendation-docs-pipe-assembly-*.jar &)
}

run () {
  run_ws
  run_pipe
}

compile () {
  echo "Compiling recommendation-config"
  (cd recommendation-config/ && exec sbt clean publishLocal)
  echo "Compiling recommendation-redis"
  (cd recommendation-redis/ && exec sbt clean publishLocal)
  echo "Compiling recommendation-docs-ws"
  (cd recommendation-docs-ws/ && exec sbt clean compile)
  echo "Compiling recommendation-docs-pipe"
  (cd recommendation-docs-pipe/ && exec sbt clean assembly)
}

case "$1" in
   "compile") compile
   ;;
   "run") compile; run
   ;;
   "run_ws") run_ws
   ;;
   "run_pipe") run_pipe
   ;;
   *) echo "Usage[run|run_ws|run_pipe|compile]"
   ;;
esac
