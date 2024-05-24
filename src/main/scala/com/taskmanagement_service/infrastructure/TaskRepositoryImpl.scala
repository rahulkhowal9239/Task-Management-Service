package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.{ErrorResponse, Task, UpdateTask}
import com.typesafe.scalalogging.LazyLogging
import doobie.ConnectionIO
import doobie.implicits.*
import doobie.util.transactor.Transactor

/**
 * Implementation of the TaskRepository trait using Doobie for database access.
 */
class TaskRepositoryImpl(xa: Transactor[IO]) extends TaskRepository with LazyLogging {

  /**
   * Inserts a new task into the database.
   *
   * @param userID The ID of the user who owns the task.
   * @param taskID The ID of the task to be created.
   * @param description The description of the task to be created.
   * @return A `ConnectionIO` representing the number of rows affected.
   */
  override def createTask(userID: String, taskID: String, description: String): ConnectionIO[Int] = {
    val insertTask: ConnectionIO[Int] =
      sql"""INSERT INTO user_tasks (user_id, task_id, description, status, assign_date)
          VALUES ($userID, $taskID, $description, 'PENDING', current_timestamp)""".update.run
    logger.info(s"Inserting task: $insertTask")
    insertTask
  }

  /**
   * Retrieves all tasks for a specified user from the database.
   *
   * @param userId The ID of the user whose tasks are to be retrieved.
   * @return A `ConnectionIO` representing a list of tasks for the specified user.
   */
  override def getTasksForUser(userId: String): ConnectionIO[List[Task]] = {
    sql"""SELECT id, userId, task_name, description, status, created_at
            FROM tasks
            WHERE userId = $userId"""
      .query[Task]
      .to[List]
  }

  /**
   * Retrieves a specific task for a specified user from the database.
   *
   * @param userId The ID of the user whose task is to be retrieved.
   * @param taskId The ID of the task to be retrieved.
   * @return A `ConnectionIO` representing an option containing a list of tasks for the specified user and task ID.
   */
  override def getSpecificTaskForUser(userId: String, taskId: String): ConnectionIO[Option[List[Task]]] = {
    sql"""SELECT t.task_name, ut.user_id, ut.description, ut.status, ut.assign_date
         FROM user_tasks ut
         INNER JOIN tasks t ON ut.task_id = t.task_id
         WHERE ut.user_id = $userId AND ut.task_id = $taskId"""
      .query[Task]
      .to[List]
      .map {
        case Nil => None
        case tasks => Some(tasks)
      }
  }

  /**
   * Updates a specific task for a specified user in the database.
   *
   * @param userId The ID of the user whose task is to be updated.
   * @param taskId The ID of the task to be updated.
   * @param updatedTask An instance of `UpdateTask` containing the updated task details.
   * @return A `ConnectionIO` representing the number of rows affected.
   */
  override def updateTaskForUser(userId: String, taskId: String, updatedTask: UpdateTask): ConnectionIO[Int] = {
    sql"""UPDATE user_tasks
            SET description = ${updatedTask.description},
                status = ${updatedTask.status}
            WHERE user_id = $userId AND task_id = $taskId"""
      .update
      .run
  }

  /**
   * Deletes a specific task for a specified user from the database.
   *
   * @param userId The ID of the user whose task is to be deleted.
   * @param taskId The ID of the task to be deleted.
   * @return A `ConnectionIO` representing the number of rows affected.
   */
  override def deleteTaskForUser(userId: String, taskId: String): ConnectionIO[Int] = {
    sql"""DELETE FROM user_tasks
            WHERE user_id = $userId AND task_id = $taskId"""
      .update
      .run
  }

  /**
   * Checks if a user is linked with a task in the database.
   *
   * @param userId The ID of the user.
   * @param taskId The ID of the task.
   * @return An `IO` representing a boolean value indicating if the user is linked with the task.
   */
  override def isUserLinkedWithTask(userId: String, taskId: String): IO[Boolean] = {
    val action: ConnectionIO[Boolean] =
      sql"""SELECT EXISTS (
             SELECT 1 FROM user_tasks WHERE user_id = $userId AND task_id = $taskId
           )"""
        .query[Boolean]
        .unique

    action.transact(xa)
  }

  /**
   * Checks if a task exists in the database.
   *
   * @param taskId The ID of the task to check.
   * @return An `IO` representing a boolean value indicating if the task exists.
   */
  override def taskExists(taskId: String): IO[Boolean] = {
    val selectTask: ConnectionIO[Option[String]] =
      sql"""SELECT task_id FROM tasks WHERE task_id = $taskId"""
        .query[String]
        .option

    selectTask.transact(xa).map(_.isDefined)
  }

  /**
   * Fetches the task ID by task name from the database.
   *
   * @param taskName The name of the task.
   * @return An `IO` representing either an `ErrorResponse` if the task is not found, or the task ID.
   */
  override def fetchTaskId(taskName: String): IO[Either[ErrorResponse, String]] = {
    val selectTaskId: ConnectionIO[Option[String]] =
      sql"""SELECT task_id FROM tasks WHERE task_name = $taskName"""
        .query[String]
        .option

    selectTaskId.transact(xa).map {
      case Some(taskId) => Right(taskId)
      case None => Left(ErrorResponse(s"Task with name $taskName not found"))
    }
  }

  /**
   * Retrieves all tasks for a given user ID from the database.
   *
   * @param userId The ID of the user whose tasks are to be retrieved.
   * @return A `ConnectionIO` representing a list of tasks for the specified user.
   */
  override def getAllTasksForUser(userId: String): ConnectionIO[List[Task]] = {
    sql"""
         SELECT * FROM tasks WHERE user_id = $userId
       """.query[Task].to[List]
  }
}
