package com.taskmanagement_service

import cats.effect.*
import com.taskmanagement_service.model.{ErrorResponse, User}
import doobie.ConnectionIO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.taskmanagement_service.infrastructure.{UserRepository, UserRepositoryImpl}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

class UserRepositoryImplSpec extends AnyFlatSpec with Matchers {
  
  val testTransactorResource: Resource[IO, HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "",
    "",
    ExecutionContexts.synchronous 
  )

  val testTransactor: HikariTransactor[IO] = testTransactorResource.use(IO.pure).unsafeRunSync()
  val testUser = User(Some("test"),"testUser", "test@example.com")
  val testUserId = "test,UserId"
  val testEmail = "test@example.com"
  val userRepository: UserRepository = new UserRepositoryImpl(testTransactor)

  "UserRepositoryImpl" should "insert a new user into the database" in {
    val result: ConnectionIO[Int] = userRepository.createUser(testUser)
  }

  it should "fetch a user by their ID" in {
    val result: IO[Option[User]] = userRepository.fetchUserById(testUserId)
  }

  it should "check if a user exists based on their ID" in {
    val result: IO[Boolean] = userRepository.userExist(testUserId)
  }

  it should "fetch the user ID by their email" in {
    val result: IO[Either[ErrorResponse, String]] = userRepository.fetchUserIDByEmail(testEmail)
  }

  it should "check if an email already exists in the database" in {
    val result: IO[Boolean] = userRepository.checkEmailExists(testEmail)
  }
}
