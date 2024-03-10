package spark.csv

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import java.io.File

object CSVSchema {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("CSVSchemaReader")
      .config(
        "spark.master",
        "local"
      )
      .getOrCreate()

    printSchemaForAllFiles(
      spark,
      "src/main/resources/data/bls/oe",
      "\t"
    )

  }

  def inferSchemaFromSample(
      spark: SparkSession,
      filePath: String,
      sep: String = ","
  ): StructType = {
    val sampleDF = spark.read
      .option("header", "true")
      .option("sep", sep)
      .csv(filePath)
      .limit(10) // Only read 10 rows to make the process faster

    sampleDF.show()

    sampleDF.schema
  }

  def printSchemaForAllFiles(
      spark: SparkSession,
      folderPath: String,
      sep: String = ","
  ): Unit = {
    val files = new File(folderPath).listFiles()

    files.foreach(file => {
      println(s"Inferring schema for: ${file.getName}")
      val schema = inferSchemaFromSample(spark, file.getAbsolutePath, sep)
      println(schema.treeString)
    })
  }

}
