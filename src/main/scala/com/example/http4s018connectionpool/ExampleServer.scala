package com.example.http4s018connectionpool

import fs2.StreamApp
import scalaz.concurrent.Task
import io.chrisdavenport.scalaz.task._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.client.blaze.{BlazeClientConfig, Http1Client}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ExampleServer extends StreamApp[Task] {
  def stream(args: List[String], requestShutdown: Task[Unit]) = Main.stream
}

object Main {
  val config: BlazeClientConfig =
    BlazeClientConfig.defaultConfig.copy(
      maxTotalConnections = 50,
      maxConnectionsPerRequestKey = _ => 50,
      requestTimeout = 60.seconds,
      idleTimeout = 59.seconds,
      responseHeaderTimeout = 599.seconds,
      maxWaitQueueLimit = 20000
    )

  def stream =
    Http1Client.stream[Task](config).flatMap { client =>
      BlazeBuilder[Task]
        .bindHttp(8080, "0.0.0.0")
        .mountService(Example.service(client), "/benchmark")
        .serve
    }
}
