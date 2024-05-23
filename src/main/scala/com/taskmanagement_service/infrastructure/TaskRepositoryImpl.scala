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
   */
  override def deleteTaskForUser(userId: String, taskId: String): ConnectionIO[Int] = {
    sql"""DELETE FROM user_tasks
            WHERE user_id = $userId AND task_id = $taskId"""
      .update
      .run
  }

  /**
   * Checks if a user is linked with a task in the database.
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
}
