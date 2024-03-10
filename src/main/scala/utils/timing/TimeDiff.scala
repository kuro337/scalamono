package utils.timing

object TimeUtils {

  @inline def calcTimeList(
      bigger: Long,
      shorter: Long,
      threshold: Long
  ): Boolean = bigger - shorter > threshold

}
