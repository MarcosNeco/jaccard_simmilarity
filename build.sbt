import org.apache.ivy.core.module.descriptor.ExcludeRule

lazy val root = (project in file("."))
  .settings(
    organization := "com.globo.jacsimm",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.2",
    mainClass := Some("com.jacsimm.StartApp")
  )

val springVersion = "2.0.6.RELEASE"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.3.0" excludeAll ExclusionRule(organization = "net.jpountz.lz4", name = "lz4")
libraryDependencies += "org.springframework.boot" % "spring-boot-starter-web" % springVersion excludeAll(ExclusionRule("org.springframework.boot","logback-classic"))
libraryDependencies += "org.springframework.boot" % "spring-boot-starter-actuator" % springVersion excludeAll(ExclusionRule("org.springframework.boot","logback-classic"))
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.2"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.9.7"


libraryDependencies += "org.springframework.boot" % "spring-boot-starter-test" % springVersion % Test
libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "2.1.0_0.8.0" % Test
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "2.0.0" % Test excludeAll(ExclusionRule("javax.ws.rs","javax.ws.rs-api"))
libraryDependencies += "org.apache.spark" %% "spark-hive"  % "2.0.0" % Test

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

artifact in(Compile, assembly) := {
  val art = (artifact in(Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}
assemblyOutputPath in assembly := file("target/jaccard-simmilarity.jar")
addArtifact(artifact in(Compile, assembly), assembly)