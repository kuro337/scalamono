name := "sparkuro"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.15"

fork := true
run / connectInput := true /* Required for stdin if fork == true */

scalacOptions += "-deprecation"

javaOptions ++= Seq(
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.io=ALL-UNNAMED",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED"
)

libraryDependencies ++= Seq(
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.apache.spark" %% "spark-core" % "3.5.0",
  "org.apache.spark" %% "spark-sql" % "3.5.0",
  "org.apache.spark" %% "spark-mllib" % "3.5.0",
  "com.lihaoyi" %% "requests" % "0.8.0",
  "com.lihaoyi" %% "upickle" % "3.2.0",
  "com.databricks" %% "spark-xml" % "0.17.0",
  "org.apache.kafka" % "kafka-clients" % "3.7.0"
)

assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)

/* For Compiling and Submitting Spark Prod => Add the % provided
libraryDependencies ++= Seq(
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.apache.spark" %% "spark-core" % "3.5.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.5.0" % "provided",
  "org.apache.spark" %% "spark-mllib" % "3.5.0" % "provided",
  "com.lihaoyi" %% "requests" % "0.8.0",
  "com.lihaoyi" %% "upickle" % "3.2.0",
  "com.databricks" %% "spark-xml" % "0.17.0"
  )
 */

/*
groupId: com.databricks
artifactId: spark-xml_2.12
version: 0.17.0
 */

/*
sbt assembly

spark-submit --class com.kuro.csv.CSVExample --master local[*] target/scala-2.11/test_2.11-1.0.jar 100
spark-submit --class spark.csv.CSVExample --master "local[*]" target/scala-2.12/sparkuro-assembly-0.1.0-SNAPSHOT.jar
spark-submit --class spark.csv.CSVExample --master "local[*]" target/scala-2.12/sparkuro-assembly-0.1.0-SNAPSHOT.jar csvDataFrameUsage
 */
