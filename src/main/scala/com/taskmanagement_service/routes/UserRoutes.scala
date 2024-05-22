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
 * @param userService The user service instance.
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
            case Success(createdUserId) =>
              logger.info(s"User created with ID: $createdUserId")
              complete(StatusCodes.Created, s"User created with ID: $createdUserId")
            case Failure(exception) =>
              logger.error("Error creating user", exception)
              complete(StatusCodes.InternalServerError, "Error creating user")
          }
        }
      }
    }

  /**
   * Route for retrieving a user by ID.
   */
  def getUserByIdRoute: Route =
    path("user") {
      get {
        parameter("id".as[Long]) { userId =>
          onSuccess(userService.retrieveUserById(userId)) {
            case Some(user) =>
              logger.info(s"User with ID $userId found: $user")
              complete(user)
            case None =>
              logger.warn(s"User with ID $userId not found")
              complete(StatusCodes.NotFound, "User not found")
          }
        }
      }
    }

  /**
   * Combined routes for user operations.
   */
  def userRoutes: Route =
    createUserRoute ~ getUserByIdRoute
}
