package utils.signals

class ShutdownListener(onShutdown: () => Unit) {
  def start(): Unit = {
    new Thread(() => {
      println("Type 'exit' to stop the consumer.")
      Iterator
        .continually(scala.io.StdIn.readLine())
        .takeWhile(_ != "exit")
        .foreach(_ => ())
      onShutdown()
    }).start()
  }
}

object Graceful {}
