package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.UserRepositoryImpl
import com.taskmanagement_service.model.User
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits.*
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}
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
  override def createUser(user: User): Future[Int] = {
    logger.debug(s"Attempting to create user: $user")
    userRepository.createUser(user).transact(xa).unsafeToFuture()
    }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId The ID of the user to be retrieved.
   * @return A Future containing an Option with the user if found, or None if not found.
   */
  override def retrieveUserById(userId: Long): Future[Option[User]] = {
    logger.debug(s"Fetching user by ID: $userId")
    userRepository.fetchUserById(userId).unsafeToFuture()
    }
  }
