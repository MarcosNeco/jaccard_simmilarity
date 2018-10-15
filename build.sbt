lazy val root = (project in file("."))
  .settings(
    organization := "com.globo.jacsimm",
    name := "jaccard_similarity",
    version := "0.1",
    scalaVersion := "2.11.2"
  )

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.3.0"
