import org.apache.ivy.core.module.descriptor.ExcludeRule

lazy val root = (project in file("."))
  .settings(
    organization := "com.globo.jacsimm",
    name := "jaccard_similarity",
    version := "0.1",
    scalaVersion := "2.11.2"
  )

val springVersion = "2.0.6.RELEASE"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.3.0"
libraryDependencies += "org.springframework.boot" % "spring-boot-starter-web" % springVersion excludeAll(ExclusionRule("org.springframework.boot","logback-classic"))
libraryDependencies += "org.springframework.boot" % "spring-boot-starter-actuator" % springVersion excludeAll(ExclusionRule("org.springframework.boot","logback-classic"))
libraryDependencies += "org.springframework.boot" % "spring-boot-starter-test" % springVersion % Test
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.2"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.9.7"






