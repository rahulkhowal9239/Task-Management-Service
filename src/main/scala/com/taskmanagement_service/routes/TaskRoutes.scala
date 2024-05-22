package com.taskmanagement_service.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.taskmanagement_service.model.{Task, UpdateTask}
import com.taskmanagement_service.business.TaskService
import com.taskmanagement_service.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Defines routes for managing tasks.
 */
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
   * Route to retrieve a specific task for a user.
   */
  def getTaskRoute: Route =
    path("users" / LongNumber / "tasks" / LongNumber) { (userId, taskId) =>
      get {
        onComplete(taskService.getTaskSpecificForUser(userId, taskId)) {
          case Success(task) =>
            logger.info(s"Retrieved task $taskId for user $userId: $task")
            complete(StatusCodes.OK, task)
          case Failure(exception) =>
            logger.error(s"Error retrieving task $taskId for user $userId", exception)
            complete(StatusCodes.InternalServerError, "Error retrieving task")
        }
      }
    }

  /**
   * Route to update a specific task for a user.
   */
  def updateTaskRoute: Route =
    path("users" / LongNumber / "tasks" / LongNumber) { (userId, taskId) =>
      put {
        entity(as[UpdateTask]) { updatedTask =>
          onComplete(taskService.updateTaskForUser(userId, taskId, updatedTask)) {
            case Success(_) =>
              logger.info(s"Updated task $taskId for user $userId")
              complete(StatusCodes.NoContent)
            case Failure(exception) =>
              logger.error(s"Error updating task $taskId for user $userId", exception)
              complete(StatusCodes.InternalServerError, "Error updating task")
          }
        }
      }
    }

  /**
   * Route to delete a specific task for a user.
   */
  def deleteTaskRoute: Route =
    path("users" / LongNumber / "tasks" / LongNumber) { (userId, taskId) =>
      delete {
        onComplete(taskService.deleteTaskForUser(userId, taskId)) {
          case Success(_) =>
            logger.info(s"Deleted task $taskId for user $userId")
            complete(StatusCodes.NoContent)
          case Failure(exception) =>
            logger.error(s"Error deleting task $taskId for user $userId", exception)
            complete(StatusCodes.InternalServerError, "Error deleting task")
        }
      }
    }

  /**
   * Combines all task-related routes into one.
   */
  def taskRoutes: Route =
    createTaskRoute ~ getTaskRoute ~ updateTaskRoute ~ deleteTaskRoute
}
