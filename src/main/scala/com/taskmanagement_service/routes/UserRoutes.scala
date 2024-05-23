package com.taskmanagement_service.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.taskmanagement_service.model.User
import com.taskmanagement_service.business.UserService
import com.taskmanagement_service.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Routes for handling user-related operations.
 *
 * @param userService       The user service instance.
 * @param executionContext The execution context for handling asynchronous operations.
 */
trait UserRoutes extends JsonSupport with LazyLogging {
  implicit def executionContext: ExecutionContext
  val userService: UserService

  /**
   * Route for creating a new user.
   */
  def createUserRoute: Route =
    path("users") {
      post {
        entity(as[User]) { user =>
          onComplete(userService.createUser(user)) {
            case Success(Right(createdUserId)) =>
              logger.info(s"User created with ID: $createdUserId")
              complete(StatusCodes.Created, s"User created with ID: $createdUserId")
            case Success(Left(errorResponse)) =>
              logger.error(s"Error creating user: ${errorResponse.error}")
              complete(StatusCodes.BadRequest, errorResponse.error)
            case Failure(exception) =>
              logger.error("Error creating user", exception)
              complete(StatusCodes.InternalServerError, "Error creating user")
          }
        }
      }
    }

  /**
   * Combined routes for user operations.
   */
  def userRoutes: Route =
    createUserRoute
}
