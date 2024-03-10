package spark.csv

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import java.io.File

object CSVExample {

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      println("")
      println("""Specify the Application to Run.
Usage: CSVExample <function_name>

Spark Submit Usage:

spark-submit \
  --class spark.csv.CSVExample \
  --master "local[*]" \
  target/scala-2.12/sparkuro-assembly-0.1.0-SNAPSHOT.jar \
  csvDataFrameUsage

spark-submit \
  --class spark.csv.CSVExample \
  --master "local[*]" \
  target/scala-2.12/sparkuro-assembly-0.1.0-SNAPSHOT.jar \
  csvDataSetUsage
""")

      System.exit(1)
    }

    val spark = SparkSession.builder
      .appName("CSVExample")
      .config(
        "spark.master",
        "local"
      )
      .getOrCreate()

    args(0) match {
      case "csvDataFrameUsage" => csvDataFrameUsage(spark)
      case "csvDataSetUsage"   => csvDataSetUsage(spark)
      case _ =>
        println(
          "Invalid function name. Use 'csvDataFrameUsage' or 'csvDataSetUsage'"
        )
    }

    spark.stop()
  }

  def csvDataFrameUsage(spark: SparkSession): Unit = {

    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .option("sep", ";")
      .csv("src/main/resources/data/realestate/geolocation.csv")

    df.show()
    df.printSchema()
    df.select("latitude", "longitude").show()
    df.filter($"longitude" < "10").show()

    // Count of Rows that have the same unified_id
    df.groupBy("unified_id").agg(count("*").as("Total")).show()

    /* withColumn - 'adds' a new col that we can analyze for the full df */
    val dfYearCount = df
      .withColumn("year", year(col("month")))
      .groupBy("year")
      .agg(count("*").as("Total"))
      .filter(col("year") === 2022)

    dfYearCount.show() // count of rows for year 2022

    val dfDecemberCount = df
      .withColumn("year", year(col("month")))
      .withColumn("monthNum", month(col("month")))
      .groupBy("year", "monthNum")
      .agg(count("*").as("Total"))
      .filter(
        col("year") === 2022 && col("monthNum") === 12
      )

    dfDecemberCount.show() // count of rows for 2022-12

    // Show Count of Rows each Month
    val countByMonthDF = df
      .groupBy(year($"month").alias("year"), month($"month").alias("month"))
      .agg(count("*").alias("Total"))
      .orderBy("year", "month")

    countByMonthDF.show()

    // SQL Operations

    df.createOrReplaceTempView("Geolocation")

    val countByMonth = spark.sql("""
      |SELECT YEAR(month) AS year, MONTH(month) AS month, COUNT(*) AS Total
      |FROM Geolocation
      |GROUP BY YEAR(month), MONTH(month)
      |ORDER BY year, month
    """.stripMargin)

    countByMonth.show() // shows 20 by default
    countByMonth.show(numRows = countByMonth.count().toInt)

    val windowSpec = Window.partitionBy("street_name").orderBy("unified_id")
    val dfWithCumulativeSum =
      df.withColumn("cumulativeSumLatitude", sum("latitude").over(windowSpec))

    dfWithCumulativeSum.show()

  }

  case class GeoLocation(
      unified_id: String,
      month: java.sql.Timestamp,
      street_name: String,
      latitude: Double,
      longitude: Double
  )

  case class Amenities(
      unified_id: String,
      month: java.sql.Timestamp,
      hot_tub: Int,
      pool: Int
  )

  def csvDataSetUsage(spark: SparkSession): Unit = {

    import spark.implicits._

    val geoLocationDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .option("sep", ";")
      .csv("src/main/resources/data/realestate/geolocation.csv")
      .withColumn(
        "latitude",
        // LatLng are Strs such as "111,33" so we need to normalize it to use '.'
        regexp_replace($"latitude", ",", ".").cast("double")
      )
      .withColumn(
        "longitude",
        regexp_replace($"longitude", ",", ".").cast("double")
      )
    val geoloc_ds: Dataset[GeoLocation] = geoLocationDF.as[GeoLocation]

    geoloc_ds.show()

    val amenitiesDf = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .option("sep", ";")
      .csv("src/main/resources/data/realestate/amenities.csv")

    val amenities_ds: Dataset[Amenities] = amenitiesDf.as[Amenities]

    amenities_ds.printSchema()
    geoloc_ds.printSchema()

    /* Joining Datasets */
    val geoLocWithAmenities = geoloc_ds
      .join(
        amenities_ds,
        geoloc_ds.col("unified_id") === amenities_ds.col("unified_id") &&
          geoloc_ds("month") === amenities_ds("month")
      )
      .select(
        geoloc_ds("unified_id"),
        geoloc_ds("month"),
        geoloc_ds("street_name"),
        geoloc_ds("latitude"),
        geoloc_ds("longitude"),
        amenities_ds("hot_tub"),
        amenities_ds("pool")
      )

    geoLocWithAmenities.printSchema()
    geoLocWithAmenities.show()

    // Add columns to indicate the presence of hot tub, pool, or both
    val withIndicators = geoLocWithAmenities
      .withColumn("Hot Tub", when($"hot_tub" === 1, 1).otherwise(0))
      .withColumn("Pool", when($"pool" === 1, 1).otherwise(0))
      .withColumn(
        "Both",
        when($"hot_tub" === 1 && $"pool" === 1, 1).otherwise(0)
      )
      .withColumn(
        "Neither",
        when($"hot_tub" === 0 && $"pool" === 0, 1).otherwise(0)
      )

    // Calculate the total counts
    val totalCount = withIndicators.count()

    /*
    Lit adds the Column as Value so we can perform Calculations
    Without Lit - the dataframe wouldnt be able to compute calculations for a Column - with a constant
    sum("Hot Tub") returns a Column type not a Double
     */

    val summary =
      withIndicators.agg( // Aggregate to get counts for each category
        round(sum("Hot Tub") / lit(totalCount) * 100, 2) as "Hot Tub %",
        round(sum("Pool") / lit(totalCount) * 100, 2) as "Pool %",
        round(sum("Both") / lit(totalCount) * 100, 2) as "Both %",
        round(sum("Neither") / lit(totalCount) * 100, 2) as "Neither %"
      )

    summary.show()

    geoLocWithAmenities.createOrReplaceTempView("amenitiesView")

    val query = """
SELECT 
  format_number(SUM(CASE WHEN hot_tub = 1 THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) AS `Hot Tub %`,
  format_number(SUM(CASE WHEN pool = 1 THEN 1 ELSE 0 END) / COUNT(*) * 100,2) AS `Pool %`,
  format_number(SUM(CASE WHEN hot_tub = 1 AND pool = 1 THEN 1 ELSE 0 END) / COUNT(*) * 100,2) AS `Both %`,
  format_number(SUM(CASE WHEN hot_tub = 0 AND pool = 0 THEN 1 ELSE 0 END) / COUNT(*) * 100,2)AS `Neither %`
FROM amenitiesView
"""

    val result = spark.sql(query)
    result.show()

  }
}
