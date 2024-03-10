package spark.transforms.xml

import com.databricks.spark.xml.util.XSDToSchema
import java.nio.file.Paths
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import com.databricks.spark.xml._
import org.apache.spark.sql.Row

import spark.utils.Schema
import spark.utils.Write

/*
https://github.com/databricks/spark-xml

groupId: com.databricks
artifactId: spark-xml_2.12
version: 0.17.0
 */

object XML {
  def main(args: Array[String]): Unit = {
    convertXMLtoCSVParquet()

    extractXMLSchema(
      "src/main/resources/data/economic/fred/FRB_Z1/Z1_struct.xml",
      "message:CodeLists",
      "z1metadata"
    )

  }

  def printXMLSchema(
      xmlFilePath: String,
      xmlTag: String,
      schemaFilename: String
  ): Unit = {

    val spark = SparkSession.builder
      .appName("XML Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", xmlTag)
      .xml(xmlFilePath)

    println("Read XML Data to DF")

    df.printSchema()
  }

  /** Convert an XML File and write to CSV and Parquet for convenient SQL Ingest
    * @example
    *   {{{XML.convertXMLtoCSVParquet()}}}
    */
  def convertXMLtoCSVParquet(): Unit = {
    println("Reading XML")

    val spark = SparkSession.builder
      .appName("XML Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "kf:Series")
      .xml("src/main/resources/data/economic/fred/FRB_Z1/Z1_data.xml")

    println("Read XML Data to DF")

    df.printSchema()

    // Schema.writeSchema("fred_finaccounts", df.schema)

    val distinctSeriesNamesDF = df.select("_SERIES_NAME").distinct()
    distinctSeriesNamesDF.show(50, false)
    println(
      s"Number of distinct series names: ${distinctSeriesNamesDF.count()}"
    )

    /* Flattening fields from Observations for Each Series */
    val explodedObsDF = df.withColumn("Obs", explode($"frb:Obs"))

    val explodedAnnotationsDF = explodedObsDF.withColumn(
      "Annotation",
      explode($"frb:Annotations.common:Annotation")
    )

    // Filter for Short and Long Descriptions within the annotations
    val filteredAnnotationsDF = explodedAnnotationsDF
      .withColumn(
        "ShortDescription",
        when(
          $"Annotation.common:AnnotationType" === "Short Description",
          $"Annotation.common:AnnotationText"
        ).otherwise(null)
      )
      .withColumn(
        "LongDescription",
        when(
          $"Annotation.common:AnnotationType" === "Long Description",
          $"Annotation.common:AnnotationText"
        ).otherwise(null)
      )
      .drop("Annotation")

    // Aggregate back to get one row per series with combined short and long descriptions
    val aggregatedDF = filteredAnnotationsDF
      .groupBy(
        $"_SERIES_NAME",
        $"_CURRENCY",
        $"_FREQ",
        $"_SERIES_INSTRUMENT",
        $"_SERIES_PREFIX",
        $"_SERIES_SECTOR",
        $"_SERIES_TYPE",
        $"_UNIT",
        $"_UNIT_MULT",
        $"Obs"
      )
      .agg(
        max("ShortDescription").alias("ShortDescription"),
        max("LongDescription").alias("LongDescription")
      )

    // Now select and rename columns as needed, including the Short and Long Descriptions
    val finalDF = aggregatedDF
      .select(
        $"_SERIES_NAME".alias("SeriesName"),
        $"_CURRENCY".alias("Currency"),
        $"_FREQ".alias("Frequency"),
        $"_SERIES_INSTRUMENT".alias("SeriesInstrument"),
        $"_SERIES_PREFIX".alias("SeriesPrefix"),
        $"_SERIES_SECTOR".alias("SeriesSector"),
        $"_SERIES_TYPE".alias("SeriesType"),
        $"_UNIT".alias("Unit"),
        $"_UNIT_MULT".alias("UnitMult"),
        $"ShortDescription",
        $"LongDescription",
        $"Obs._OBS_STATUS".alias("ObservationStatus"),
        $"Obs._OBS_VALUE".alias("ObservationValue"),
        $"Obs._TIME_PERIOD".alias("TimePeriod"),
        $"Obs._VALUE".alias("Value")
      )

    val outputPathCsv =
      "/Users/kuro/Documents/Data/raw/fred/FRB_Z1/transformed/csv"

    val outputPathParquet =
      "/Users/kuro/Documents/Data/raw/fred/FRB_Z1/transformed/parquet"

    Write.writeCSV(outputPathCsv, finalDF, overwriteIfExists = true)
    Write.writeParquet(outputPathParquet, finalDF, overwriteIfExists = true)

  }

  /** Extract and Write the Schema of an XML File from a Tag from the File
    *
    * @param xmlFilePath
    * @param xmlTag
    * @param schemaFilename
    * @param outputPath
    */
  def extractXMLSchema(
      xmlFilePath: String,
      xmlTag: String,
      schemaFilename: String,
      outputPath: Option[String] = None
  ): Unit = {
    val spark = SparkSession.builder
      .appName("XML Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", xmlTag)
      .xml(xmlFilePath)

    println("Read XML Data to DF")

    df.printSchema()

    outputPath match {
      case Some(value) =>
        Schema.writeSchema(schemaFilename, df.schema, Some(value))
      case None => Schema.writeSchema(schemaFilename, df.schema)
    }

    println("Successfully Extracted Schema")

    val flatdf = df
      .select(explode($"structure:CodeList").alias("CodeList"))
      .select(
        $"CodeList._agency".alias("Agency"),
        $"CodeList._id".alias("ID"),
        explode($"CodeList.structure:Code").alias("Code"),
        $"CodeList.structure:Name".alias("Name")
      )
      .select(
        $"Agency",
        $"ID",
        $"Code._value".alias("CodeValue"),
        $"Code.structure:Description".alias("Description"),
        $"Name"
      )

    // Print ten rows for each ID
    flatdf.groupBy($"ID").count().collect().foreach {
      case Row(id: String, count: Long) =>
        println(s"ID: $id, Count: $count")
        flatdf.filter($"ID" === id).show(10, truncate = false)
    }

    val outputPathParquet =
      "/Users/kuro/Documents/Data/raw/fred/FRB_Z1/transformed/parquet/mapping"

      Write.writeParquet(outputPathParquet, flatdf, overwriteIfExists = true)

  }

}

/* Schema
[info]  |-- _CURRENCY: string (nullable = true)
[info]  |-- _FREQ: long (nullable = true)
[info]  |-- _SERIES_INSTRUMENT: long (nullable = true)
[info]  |-- _SERIES_NAME: string (nullable = true)
[info]  |-- _SERIES_PREFIX: string (nullable = true)
[info]  |-- _SERIES_SECTOR: long (nullable = true)
[info]  |-- _SERIES_TYPE: long (nullable = true)
[info]  |-- _UNIT: string (nullable = true)
[info]  |-- _UNIT_MULT: long (nullable = true)
[info]  |-- frb:Annotations: struct (nullable = true)
[info]  |    |-- common:Annotation: array (nullable = true)
[info]  |    |    |-- element: struct (containsNull = true)
[info]  |    |    |    |-- common:AnnotationText: string (nullable = true)
[info]  |    |    |    |-- common:AnnotationType: string (nullable = true)
[info]  |-- frb:Obs: array (nullable = true)
[info]  |    |-- element: struct (containsNull = true)
[info]  |    |    |-- _OBS_STATUS: string (nullable = true)
[info]  |    |    |-- _OBS_VALUE: double (nullable = true)
[info]  |    |    |-- _TIME_PERIOD: date (nullable = true)
[info]  |    |    |-- _VALUE: string (nullable = true)
Number of distinct series names: 39493
 */
