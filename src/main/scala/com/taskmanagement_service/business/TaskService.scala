package com.taskmanagement_service.business

import com.taskmanagement_service.model.{Task, UpdateTask}

import scala.concurrent.Future

trait TaskService {

  def assignTask(task: Task): Future[Int]
  def getTasksForUser(userId: Long): Future[List[Task]]
  def getTaskSpecificForUser(userId: Long, taskId: Long): Future[Option[Task]]
  def updateTaskForUser(userId: Long, taskId: Long, updatedTask: UpdateTask): Future[Int]
  def deleteTaskForUser(userId: Long, taskId: Long): Future[Int]
}
