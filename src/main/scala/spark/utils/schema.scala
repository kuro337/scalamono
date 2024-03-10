package spark.utils
import java.io.PrintWriter
import org.apache.spark.sql.types.StructType
import java.nio.file.Paths

object Schema {

  /** Write the Schema of a DataFrame to a File in JSON formatted form.
    * @param schemaName
    *   The name of the schema file to be written.
    * @param schema
    *   The StructType schema to be written.
    * @param filePath
    *   Optional. The file path where the schema file will be saved. Defaults to
    *   the current directory.
    */
  def writeSchema(
      schemaName: String,
      schema: StructType,
      filePath: Option[String] = None
  ): Unit = {
    val schemaJson = schema.prettyJson
    val outputPath =
      filePath.getOrElse(".")
    val fullFilePath = Paths.get(outputPath, s"$schemaName.json").toString

    new PrintWriter(fullFilePath) { write(schemaJson); close() }

    println(s"Schema extracted and written successfully to $fullFilePath")
  }

}
