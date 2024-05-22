package com.taskmanagement_service.Routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.taskmanagement_service.business.UserService
import com.taskmanagement_service.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

trait UserRoute extends JsonSupport with LazyLogging {
  implicit def executionContext: ExecutionContext
  val userService: UserService

  def getUserByIdRoute: Route =
    path("user") {
      get {
        parameter("id".as[Long]) { userId =>
          onSuccess(userService.getUserById(userId)) {
            case Some(user) =>
              logger.info(s"User with ID $userId found: $user")
              complete(user)
            case None       =>
              logger.warn(s"User with ID $userId not found")
              complete(StatusCodes.NotFound, "User not found")
          }
        }
      }
    }
}
