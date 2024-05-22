package com.taskmanagement_service.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.taskmanagement_service.model.Task
import com.taskmanagement_service.business.TaskService
import com.taskmanagement_service.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait TaskRoutes extends JsonSupport with LazyLogging {
  implicit def executionContext: ExecutionContext
  val taskService: TaskService

  /**
   * Route to create a new task.
   */
  def createTaskRoute: Route =
    path("tasks") {
      post {
        entity(as[Task]) { task =>
          onComplete(taskService.assignTask(task)) {
            case Success(taskId) =>
              logger.info(s"Task created with ID: $taskId")
              complete(StatusCodes.Created, s"Task created with ID: $taskId")
            case Failure(exception) =>
              logger.error("Error creating task", exception)
              complete(StatusCodes.InternalServerError, "Error creating task")
          }
        }
      }
    }

  /**
   * Combined routes for task operations.
   */
  def taskRoutes: Route = createTaskRoute
}
