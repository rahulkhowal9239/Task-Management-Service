package com.taskmanagement_service.infrastructure

import com.taskmanagement_service.model.{Task, UpdateTask}
import doobie.ConnectionIO

trait TaskRepository {

  def createTask(task: Task): ConnectionIO[Int]
  def getTasksForUser(userId: Long): ConnectionIO[List[Task]]
  def getSpecificTaskForUser(userId: Long, taskId: Long): ConnectionIO[Option[Task]]
  def updateTaskForUser(userId: Long, taskId: Long, updatedTask: UpdateTask): ConnectionIO[Int]
  def deleteTaskForUser(userId: Long, taskId: Long): ConnectionIO[Int]
}
