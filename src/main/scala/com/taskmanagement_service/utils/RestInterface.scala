package com.taskmanagement_service.utils

import akka.http.scaladsl.server.Route
import cats.effect.IO
import com.taskmanagement_service.routes.{TaskRoutes, UserRoutes}
import com.taskmanagement_service.business.{TaskService, TaskServiceImpl, UserService, UserServiceImpl}
import com.taskmanagement_service.database_config.{Configuration, DatabaseConnector}
import doobie.util.transactor.Transactor
import akka.http.scaladsl.server.RouteConcatenation._

import scala.concurrent.ExecutionContext

trait RestInterface extends Resources {

  implicit def executionContext: ExecutionContext

  implicit def db: DatabaseConnector

  implicit def xa: Transactor[IO] = db.getTransactor(Configuration.serviceConf.dbConfig)

  override val userService: UserService = new UserServiceImpl()

  override val taskService: TaskService = new TaskServiceImpl()

  val routes: Route = userRoutes ~ taskRoutes

}

trait Resources extends UserRoutes with TaskRoutes
