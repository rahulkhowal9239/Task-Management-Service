package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.UserRepositoryImpl
import com.taskmanagement_service.model.{ErrorResponse, User}
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits.*
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}

/**
 * Implementation of the UserService trait.
 *
 * @param xa The transactor providing database access.
 * @param ec The execution context for handling asynchronous operations.
 */
class UserServiceImpl(implicit xa: Transactor[IO], ec: ExecutionContext)
  extends UserService
    with LazyLogging {

  private val userRepository = new UserRepositoryImpl(xa)

  /**
   * Creates a new user in the system.
   *
   * @param user The user to be created.
   * @return A Future containing the ID of the created user.
   */
  override def createUser(user: User): Future[Either[ErrorResponse, Int]] = {
    if (user.username.isEmpty || user.email.isEmpty) {
      Future.successful(Left(ErrorResponse("Username and email are mandatory fields")))
    } else {
      val resultIO: IO[Either[ErrorResponse, Int]] = for {
        emailExists <- userRepository.checkEmailExists(user.email)
        result <- if (emailExists) {
          IO.pure(Left(ErrorResponse("Email already exists")))
        } else {
          userRepository.createUser(user)
            .transact(xa)
            .map(Right(_))
        }
      } yield result

      resultIO.attempt.flatMap {
        case Left(e) =>
          val errorMessage = e.getMessage
          logger.error(s"Error creating user: $errorMessage")
          IO.pure(Left(ErrorResponse(errorMessage)))
        case Right(result) =>
          IO.pure(result)
      }.unsafeToFuture()
    }
  }
}
