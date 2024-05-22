package com.taskmanagement_service.utils

import akka.http.scaladsl.server.Route
import cats.effect.IO
import com.taskmanagement_service.routes.UserRoute
import com.taskmanagement_service.business.{UserService, UserServiceImpl}
import com.taskmanagement_service.database_config.{Configuration, DatabaseConnector}
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

trait RestInterface extends Resources {

  implicit def executionContext: ExecutionContext

  implicit def db: DatabaseConnector

  implicit def xa: Transactor[IO] = db.getTransactor(Configuration.serviceConf.dbConfig)

  override val userService: UserService = new UserServiceImpl()

  val routes: Route = getUserByIdRoute

}

trait Resources extends UserRoute
