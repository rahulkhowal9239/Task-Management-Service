package com.taskmanagement_service.business

import com.taskmanagement_service.model.{AssignTask, ErrorResponse, Task, UpdateTask}

import scala.concurrent.Future

trait TaskService {

  def assignTask(userId: String,task: AssignTask): Future[Either[ErrorResponse, Int]]
  def getTasksForUser(userId: String): Future[List[Task]]
  def getTaskSpecificForUser(userId: String, taskId: String): Future[Either[ErrorResponse, Task]]
  def updateTaskForUser(userId: String, taskId: String, updatedTask: UpdateTask): Future[Either[ErrorResponse, Int]]
  def deleteTaskForUser(userId: String, taskId: String): Future[Either[ErrorResponse, Int]]}
