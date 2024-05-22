package com.taskmanagement_service.business

import com.taskmanagement_service.model.{Task, User}

import scala.concurrent.Future

trait TaskService {

  def assignTask(task: Task): Future[Int]
}
