package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.{Task, UpdateTask}
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
  override def createTask(task: Task): ConnectionIO[Int] = {
    val insertTask: ConnectionIO[Int] =
      sql"""INSERT INTO tasks (userId, task_name, description, status, created_at)
            VALUES (${task.userId}, ${task.taskName}, ${task.description}, 'PENDING', current_timestamp)""".update.run
    logger.info(s"Inserting task: $task")
    insertTask
  }

  /**
   * Retrieves all tasks for a specified user from the database.
   */
  override def getTasksForUser(userId: Long): ConnectionIO[List[Task]] = {
    sql"""SELECT id, userId, task_name, description, status, created_at
            FROM tasks
            WHERE userId = $userId"""
      .query[Task]
      .to[List]
  }

  /**
   * Retrieves a specific task for a specified user from the database.
   */
  override def getSpecificTaskForUser(userId: Long, taskId: Long): ConnectionIO[Option[Task]] = {
    sql"""SELECT id, userId, task_name, description, status, created_at
            FROM tasks
            WHERE userId = $userId AND id = $taskId"""
      .query[Task]
      .option
  }

  /**
   * Updates a specific task for a specified user in the database.
   */
  override def updateTaskForUser(userId: Long, taskId: Long, updatedTask: UpdateTask): ConnectionIO[Int] = {
    sql"""UPDATE tasks
            SET task_name = ${updatedTask.taskName},
                description = ${updatedTask.description},
                status = ${updatedTask.status}
            WHERE userId = $userId AND id = $taskId"""
      .update
      .run
  }

  /**
   * Deletes a specific task for a specified user from the database.
   */
  override def deleteTaskForUser(userId: Long, taskId: Long): ConnectionIO[Int] = {
    sql"""DELETE FROM tasks
            WHERE userId = $userId AND id = $taskId"""
      .update
      .run
  }
}
