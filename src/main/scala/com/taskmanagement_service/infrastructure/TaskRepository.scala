package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.{AssignTask, ErrorResponse, Task, UpdateTask}
import doobie.ConnectionIO

trait TaskRepository {

  def createTask(userID: String,taskID: String,
                          description: String): ConnectionIO[Int]
  def getTasksForUser(userId: String): ConnectionIO[List[Task]]

  def getSpecificTaskForUser(userId: String, taskId: String): ConnectionIO[Option[List[Task]]]
  def updateTaskForUser(userId: String, taskId: String, updatedTask: UpdateTask): ConnectionIO[Int]

  def deleteTaskForUser(userId: String, taskId: String): ConnectionIO[Int]

  def isUserLinkedWithTask(userId: String, taskId: String): IO[Boolean]

  def taskExists(taskId: String): IO[Boolean]

  def fetchTaskId(taskName: String): IO[Either[ErrorResponse, String]]

}