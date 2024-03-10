package spark.ml

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/* sbt "runMain MLSpark" libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.5.0" */
object MLSpark {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Data Transformations")
      .config("spark.master", "local")
      .getOrCreate()

    import spark.implicits._

    val data = Seq(
      (0, Vectors.dense(1.0, 0.1, -1.0), 0.0),
      (1, Vectors.dense(2.0, 1.1, 1.0), 1.0),
      (2, Vectors.dense(0.0, 1.2, -0.5), 0.0)
    )

    val df = data.toDF("id", "features", "label")

    // Create a new LogisticRegression instance
    val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.01)

    // Fit the model
    val model = lr.fit(df)

    // Print the model coefficients and intercept
    println(
      s"Coefficients: ${model.coefficients} Intercept: ${model.intercept}"
    )

  }
}
