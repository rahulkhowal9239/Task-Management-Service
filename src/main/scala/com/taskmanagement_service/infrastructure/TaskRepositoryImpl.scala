package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.Task
import com.typesafe.scalalogging.LazyLogging
import doobie.ConnectionIO
import doobie.implicits.*
import doobie.util.transactor.Transactor

class TaskRepositoryImpl(xa: Transactor[IO]) extends TaskRepository with LazyLogging {

  override def createTask(task: Task): ConnectionIO[Int] = {
    val insertTask: ConnectionIO[Int] =
      sql"""INSERT INTO tasks (userId, task_name, description, status, created_at)
            VALUES (${task.userId}, ${task.taskName}, ${task.description}, 'PENDING', current_timestamp)""".update.run
    logger.info(s"Inserting task: $task")
    insertTask
  }
}

