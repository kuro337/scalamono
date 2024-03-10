package http.client

object WebClient {
  def main(args: Array[String]): Unit = {
    makeReqWithHeaders()
  }

  def basicUsage(): Unit = {
    val r = requests.get("https://api.github.com/users/lihaoyi")
    println(r.statusCode) // 200
    r.headers("content-type") // Buffer("application/json; charset=utf-8")
    r.text // {"login":"lihaoyi","id":934140,"node_id":"MDQ6VXNlcjkzNDE0MA==",...

  }

  def makeReqWithHeaders(): Unit = {

    val username = "admin"
    val password = "admin"

    val r = requests.get(
      "http://localhost:9200",
      auth = (username, password)
    )

    println(r.statusCode)

    println(r.headers("content-type"))

    val prettyJson = ujson.read(r.text).render(indent = 4)
    println(prettyJson)

  }
}
