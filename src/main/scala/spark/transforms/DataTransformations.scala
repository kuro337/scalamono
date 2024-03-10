package spark.transforms

import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{
  StructType,
  StructField,
  StringType,
  LongType
}

/* sbt "runMain DataTransformations" */
object DataTransformations {
  def analyzeZigJson(): Unit = {
    val spark = SparkSession.builder
      .appName("Data Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read
      .option("multiline", "true")
      .json("src/main/resources/data/normal.json")

    df.printSchema()

    val rootSchema: StructType = df.schema

    /* Result Dataframe */
    var resultDf = spark.createDataFrame(
      spark.sparkContext.emptyRDD[Row],
      StructType(
        Array(
          StructField("Version", StringType, true),
          StructField("Date", StringType, true)
        )
      )
    )

    // Iterate over each field and aggregate version and date

    df.schema.fields.foreach { field =>
      field.dataType match {
        case structType: StructType
            if structType.fields.map(_.name).contains("date") =>
          /*
          selectExpr used for Custom Selection Logic for Cols.

          Since the Keys can be 0.9.0, etc. we want to make sure theyre not evaluated as Numbers
          This will cause a Runtime Error because field.name is not enclosed in ``
          df.selectExpr(s"${field.name}.date as date")

          s"`$fieldname`" : Enclosing in str literal solves Col Names being evaluated as Numbers etc.

           */

          val versionDf = df
            .selectExpr(s"`${field.name}`.date as Date")
            .withColumn("Version", lit(field.name))
            .select("Version", "Date")

          // Union the current version's DataFrame with the aggregated result
          resultDf = resultDf.union(versionDf)

        case _ =>
      }
    }

    println("Version and Date Table:")
    resultDf.show(truncate = false)

    // After aggregating the results in resultDf
    val resultsArray = resultDf.collect()

    spark.stop()
  }

  def analyzeJsonNewlines(): Unit = {
    val spark = SparkSession.builder
      .appName("Data Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read.json("src/main/resources/data/defaultspark.json")

    // Show the DataFrame content
    df.show()

    // Select only the "name" column
    df.select("name").show()

    // Select everybody, but increment the age by 1
    df.select($"name", $"age" + 1).show()

    // Select people older than 21
    df.filter($"age" > 21).show()

    // Count people by age
    df.groupBy("age").count().show()

    df.createOrReplaceTempView("people")

    // Run SQL queries
    val olderThan21 = spark.sql("SELECT name FROM people WHERE age > 21")

    olderThan21.show()
  }
  def main(args: Array[String]): Unit = {
    analyzeZigJson()
    analyzeJsonNewlines()
  }
}
