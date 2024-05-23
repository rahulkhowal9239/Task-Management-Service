package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.{TaskRepositoryImpl, UserRepositoryImpl}
import com.taskmanagement_service.model.{AssignTask, ErrorResponse, Task, UpdateTask, User}
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits.*
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}

/**
 * Implementation of the TaskService trait.
 *
 * @param xa The transactor providing database access.
 * @param ec The execution context for handling asynchronous operations.
 */
class TaskServiceImpl(implicit xa: Transactor[IO], ec: ExecutionContext)
  extends TaskService
    with LazyLogging {

  private val taskRepository: TaskRepositoryImpl = new TaskRepositoryImpl(xa)
  private val userRepository = new UserRepositoryImpl(xa)

  /**
   * Assigns a task to a user.
   *
   * @param userId The ID of the user to whom the task will be assigned.
   * @param task   The task to be assigned.
   * @return A Future containing either an error response or the ID of the created task.
   */
  override def assignTask(userId: String, task: AssignTask): Future[Either[ErrorResponse, Int]] = {
    logger.debug(s"Assigning task: $task")

    val result: IO[Either[ErrorResponse, Int]] = for {
      userExists <- userRepository.userExist(userId)
      taskIdEither <- userExists match {
        case true =>
          taskRepository.fetchTaskId(task.taskName)
        case false =>
          IO.pure(Left(ErrorResponse("User does not exist")))
      }
      result <- (userExists, taskIdEither) match {
        case (true, Right(taskId)) =>
          taskRepository.createTask(userId, taskId, task.description).transact(xa).map(Right(_))
        case (_, Left(error)) =>
          IO.pure(Left(error))
        case (false, _) =>
          IO.pure(Left(ErrorResponse("User does not exist")))
      }
    } yield result

    result.unsafeToFuture()
  }

  /**
   * Retrieves all tasks for a specified user.
   *
   * @param userId The ID of the user.
   * @return A list of tasks assigned to the user.
   */
  override def getTasksForUser(userId: String): Future[Either[ErrorResponse, List[Task]]] = {
      val resultIO: IO[Either[ErrorResponse, List[Task]]] = for {
        userExists <- userRepository.userExist(userId)
        _ <- if (userExists) IO.unit else IO.raiseError(new Exception("User does not exist"))
        tasks <- taskRepository.getAllTasksForUser(userId).transact(xa)
      } yield Right(tasks)

      resultIO.attempt.map {
        case Left(e) => Left(ErrorResponse(e.getMessage))
        case Right(result) => result
      }.unsafeToFuture()
    }

  /**
   * Retrieves a specific task for a specified user.
   *
   * @param userId The ID of the user.
   * @param taskId The ID of the task.
   * @return An optional task if found, None otherwise.
   */
  override def getTaskSpecificForUser(userId: String, taskId: String): Future[Either[ErrorResponse, Task]] = {
    val resultIO: IO[Either[ErrorResponse, Task]] = for {
      userExists <- userRepository.userExist(userId)
      _ <- if (userExists) IO.unit else IO.raiseError(new Exception("User does not exist"))
      taskExists <- taskRepository.taskExists(taskId)
      _ <- if (taskExists) IO.unit else IO.raiseError(new Exception("Task does not exist"))
      taskLinked <- taskRepository.isUserLinkedWithTask(userId, taskId)
      _ <- if (taskLinked) IO.unit else IO.raiseError(new Exception("User is not linked with the task"))
      taskOpt <- taskRepository.getSpecificTaskForUser(userId, taskId).transact(xa)
    } yield taskOpt match {
      case Some(task) => Right(task.head)
      case None => Left(ErrorResponse("Task not found"))
    }

    resultIO.attempt.map {
      case Left(e) => Left(ErrorResponse(e.getMessage))
      case Right(result) => result
    }.unsafeToFuture()
  }

  /**
   * Updates a specific task for a specified user.
   *
   * @param userId      The ID of the user.
   * @param taskId      The ID of the task.
   * @param updatedTask The updated task details.
   * @return The number of rows affected by the update operation.
   */
  override def updateTaskForUser(userId: String, taskId: String, updatedTask: UpdateTask): Future[Either[ErrorResponse, Int]] = {
    val userExistsIO: IO[Boolean] = userRepository.userExist(userId)
    val taskExistsIO: IO[Boolean] = taskRepository.taskExists(taskId)

    val resultIO: IO[Either[ErrorResponse, Int]] = for {
      userExists <- userExistsIO
      taskExists <- taskExistsIO
      result <- if (!userExists) {
        IO.pure(Left(ErrorResponse("User does not exist")))
      } else if (!taskExists) {
        IO.pure(Left(ErrorResponse("Task does not exist")))
      } else {
        taskRepository.updateTaskForUser(userId, taskId, updatedTask)
          .transact(xa)
          .map(Right(_))
      }
    } yield result

    resultIO.handleErrorWith { e =>
      // Map the exception to an appropriate error response
      val errorMessage = e.getMessage
      IO.pure(Left(ErrorResponse(errorMessage)))
    }.unsafeToFuture()
  }

  /**
   * Deletes a specific task for a specified user.
   *
   * @param userId The ID of the user.
   * @param taskId The ID of the task.
   * @return The number of rows affected by the delete operation.
   */
  override def deleteTaskForUser(userId: String, taskId: String): Future[Either[ErrorResponse, Int]] = {
    val resultIO: IO[Either[ErrorResponse, Int]] = for {
      userExists <- userRepository.userExist(userId)
      taskExists <- taskRepository.taskExists(taskId)
      result <- if (!userExists) {
        IO.pure(Left(ErrorResponse("User does not exist")))
      } else if (!taskExists) {
        IO.pure(Left(ErrorResponse("Task does not exist")))
      } else {
        taskRepository.deleteTaskForUser(userId, taskId).transact(xa).map(Right(_))
      }
    } yield result

    resultIO.handleErrorWith { e =>
      // Log the error and return an appropriate ErrorResponse
      val errorMessage = e.getMessage
      logger.error(s"Error deleting task $taskId for user $userId: $errorMessage")
      IO.pure(Left(ErrorResponse(errorMessage)))
    }.unsafeToFuture()
  }
}
