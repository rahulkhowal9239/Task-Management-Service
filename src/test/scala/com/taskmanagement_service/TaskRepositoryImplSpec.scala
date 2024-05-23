package com.taskmanagement_service

import cats.effect.IO
import com.taskmanagement_service.model.{ErrorResponse, Task, UpdateTask}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import doobie.implicits.*
import doobie.util.transactor.Transactor
import cats.effect.*
import com.taskmanagement_service.model.{ErrorResponse, User}
import doobie.ConnectionIO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.taskmanagement_service.infrastructure.{TaskRepository, TaskRepositoryImpl}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

class TaskRepositoryImplSpec extends AnyFlatSpec with Matchers {

  val testTransactorResource: Resource[IO, HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "",
    "",
    ExecutionContexts.synchronous
  )

  val testTransactor: HikariTransactor[IO] = testTransactorResource.use(IO.pure).unsafeRunSync()
  val taskRepository: TaskRepository = new TaskRepositoryImpl(testTransactor)

  val testUserId: String = "testUserId"
  val testTaskId: String = "testTaskId"
  val testDescription: String = "Test task description"
  val testUpdateTask: UpdateTask = UpdateTask("Updated description", "COMPLETED")
  val testTaskName: String = "Test Task"

  "TaskRepositoryImpl" should "insert a new task into the database" in {
    val result: IO[Int] = taskRepository.createTask(testUserId, testTaskId, testDescription).transact(testTransactor)
  }

  it should "retrieve all tasks for a specified user from the database" in {
    val result: IO[List[Task]] = taskRepository.getTasksForUser(testUserId).transact(testTransactor)
  }

  it should "retrieve a specific task for a specified user from the database" in {
    val result: IO[Option[List[Task]]] = taskRepository.getSpecificTaskForUser(testUserId, testTaskId).transact(testTransactor)
  }

  it should "update a specific task for a specified user in the database" in {
    val result: IO[Int] = taskRepository.updateTaskForUser(testUserId, testTaskId, testUpdateTask).transact(testTransactor)
  }

  it should "delete a specific task for a specified user from the database" in {
    val result: IO[Int] = taskRepository.deleteTaskForUser(testUserId, testTaskId).transact(testTransactor)
  }

  it should "check if a user is linked with a task in the database" in {
    val result: IO[Boolean] = taskRepository.isUserLinkedWithTask(testUserId, testTaskId)
  }

  it should "check if a task exists in the database" in {
    val result: IO[Boolean] = taskRepository.taskExists(testTaskId)
  }

  it should "fetch the task ID by task name from the database" in {
    val result: IO[Either[ErrorResponse, String]] = taskRepository.fetchTaskId(testTaskName)
  }
}
