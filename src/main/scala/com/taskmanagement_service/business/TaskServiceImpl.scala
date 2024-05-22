package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.TaskRepositoryImpl
import com.taskmanagement_service.model.{Task, UpdateTask}
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

  /**
   * Assigns a task to a user.
   *
   * @param task The task to be assigned.
   * @return The ID of the created task.
   */
  override def assignTask(task: Task): Future[Int] = {
    logger.debug(s"Assigning task: $task")
    taskRepository.createTask(task).transact(xa).unsafeToFuture()
  }

  /**
   * Retrieves all tasks for a specified user.
   *
   * @param userId The ID of the user.
   * @return A list of tasks assigned to the user.
   */
  override def getTasksForUser(userId: Long): Future[List[Task]] = {
    taskRepository.getTasksForUser(userId).transact(xa).unsafeToFuture()
  }

  /**
   * Retrieves a specific task for a specified user.
   *
   * @param userId The ID of the user.
   * @param taskId The ID of the task.
   * @return An optional task if found, None otherwise.
   */
  override def getTaskSpecificForUser(userId: Long, taskId: Long): Future[Option[Task]] = {
    taskRepository.getSpecificTaskForUser(userId, taskId).transact(xa).unsafeToFuture()
  }

  /**
   * Updates a specific task for a specified user.
   *
   * @param userId      The ID of the user.
   * @param taskId      The ID of the task.
   * @param updatedTask The updated task details.
   * @return The number of rows affected by the update operation.
   */
  override def updateTaskForUser(userId: Long, taskId: Long, updatedTask: UpdateTask): Future[Int] = {
    taskRepository.updateTaskForUser(userId, taskId, updatedTask).transact(xa).unsafeToFuture()
  }

  /**
   * Deletes a specific task for a specified user.
   *
   * @param userId The ID of the user.
   * @param taskId The ID of the task.
   * @return The number of rows affected by the delete operation.
   */
  override def deleteTaskForUser(userId: Long, taskId: Long): Future[Int] = {
    taskRepository.deleteTaskForUser(userId, taskId).transact(xa).unsafeToFuture()
  }
}
