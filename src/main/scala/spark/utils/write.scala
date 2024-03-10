package spark.utils

import org.apache.spark.sql.DataFrame

object Write {

  /** Writes a DataFrame to a Parquet File with optional overwrite.
    * @param dest
    *   The destination path where the DataFrame will be written.
    * @param df
    *   The DataFrame to write.
    * @param overwriteIfExists
    *   Boolean indicating whether to overwrite existing files at the
    *   destination.
    * @example
    *   {{{Write.writeParquet(path, renamedDF, overwriteIfExists = true)}}}
    */
  def writeParquet(
      dest: String,
      df: DataFrame,
      overwriteIfExists: Boolean = false
  ): Unit = {
    val writer = if (overwriteIfExists) df.write.mode("overwrite") else df.write
    writer.parquet(dest)
  }

  /** Writes a DataFrame to a CSV File with optional overwrite.
    * @param dest
    *   The destination path where the DataFrame will be written.
    * @param df
    *   The DataFrame to write.
    * @param overwriteIfExists
    *   Boolean indicating whether to overwrite existing files at the
    *   destination.
    * @example
    *   {{{Write.writeCSV(path, renamedDF, overwriteIfExists = true)}}}
    */
  def writeCSV(
      dest: String,
      df: DataFrame,
      overwriteIfExists: Boolean = false
  ): Unit = {
    val writer = if (overwriteIfExists) df.write.mode("overwrite") else df.write
    writer.option("header", "true").csv(dest)
  }
}
