package com.taskmanagement_service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.taskmanagement_service.database_config.{Configuration, DatabaseConnector, FlywayService}
import com.taskmanagement_service.utils.RestInterface

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object QuickstartApp extends App with RestInterface {

  implicit val system: ActorSystem             = ActorSystem("TaskManagementService")
  implicit val executionContext                = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val timeout: Timeout = Timeout(10 seconds)

  val config     = Configuration.serviceConf
  val api: Route = routes
  val port       = config.port
  val host       = config.host

  val flyWayService = new FlywayService(config.dbConfig)
  flyWayService.migrateDatabaseSchema()
  system.log.info("In the Start HTTP Server Method")

  Http().newServerAt(host, port).bind(api).map { binding =>
    println(s"REST interface bound to ${binding.localAddress}")
  } recover { case ex =>
    (s"REST interface could not bind to $host:$port", ex.getMessage)
  }
  override implicit def db: DatabaseConnector = new DatabaseConnector
}
