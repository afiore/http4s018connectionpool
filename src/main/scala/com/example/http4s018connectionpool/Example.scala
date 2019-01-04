package com.example.http4s018connectionpool

import io.chrisdavenport.scalaz.task._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpService, Uri}
import scalaz.concurrent.Task

object Example extends Http4sDsl[Task] {

  def service(client: Client[Task]): HttpService[Task] = {
    HttpService[Task] {
      case GET -> Root / "client" / "run" / IntVar(n) =>
        val runRequests = Task.gatherUnordered((1 to n).toList.map( _ =>
          client.expect[String](Uri.unsafeFromString("https://example.com"))
        ))
        for {
          start <- Task(System.currentTimeMillis())
          _ <- runRequests
          end <- Task(System.currentTimeMillis())
          resp <- Ok(s"Took ${end - start} milliseconds to run $n requests")
        } yield resp
    }
  }
}
