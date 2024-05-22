package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.TaskRepositoryImpl
import com.taskmanagement_service.model.Task
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits.*
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}

class TaskServiceImpl(implicit xa: Transactor[IO], ec: ExecutionContext)
  extends TaskService
    with LazyLogging {

  private val taskRepository: TaskRepositoryImpl = new TaskRepositoryImpl(xa)

  /**
   * Assigns a task to a user.
   *
   * @param task The task to be assigned.
   * @return The ID of the created task.
   */
  override def assignTask(task: Task): Future[Int] = {
    logger.debug(s"Assigning task: $task")
    taskRepository.createTask(task).transact(xa).unsafeToFuture()
  }
}
