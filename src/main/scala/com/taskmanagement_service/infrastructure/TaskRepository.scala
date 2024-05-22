package com.taskmanagement_service.infrastructure

import com.taskmanagement_service.model.Task
import doobie.ConnectionIO

trait TaskRepository {

  def createTask(task: Task): ConnectionIO[Int]
}
