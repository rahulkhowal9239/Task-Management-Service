package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.{ErrorResponse, User}
import com.typesafe.scalalogging.LazyLogging
import doobie.ConnectionIO
import doobie.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

/**
 * Implementation of the UserRepository trait using Doobie for database access.
 *
 * @param xa The transactor providing database access.
 */
class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository with LazyLogging {

  /**
   * Inserts a new user into the database.
   *
   * @param user The user to be inserted.
   * @return A ConnectionIO containing the number of affected rows.
   */
  override def createUser(user: User): ConnectionIO[Int] = {
    val insertUser: ConnectionIO[Int] =
      sql"""INSERT INTO users (user_id, username, email) VALUES (${UUID.randomUUID().toString}, ${user.username}, ${user.email})""".update.run
    logger.info(s"Inserting user: $user")
    insertUser
  }

  /**
   * Fetches a user by their ID.
   *
   * @param userId The ID of the user to be fetched.
   * @return An IO containing an Option with the user if found, or None if not found.
   */
  override def fetchUserById(userId: String): IO[Option[User]] = {
    logger.debug(s"Fetching user by ID: $userId")
    val selectUser: ConnectionIO[Option[User]] =
      sql"""SELECT user_id, username, email FROM users WHERE user_id = $userId"""
        .query[User]
        .option

    selectUser.transact(xa).map { userOption =>
      userOption.foreach(user => logger.info(s"User found: $user"))
      userOption
    }
  }

  /**
   * Checks if a user exists based on their ID.
   *
   * @param userId The ID of the user to check.
   * @return An IO indicating whether the user exists.
   */
  override def userExist(userId: String): IO[Boolean] = {
    fetchUserById(userId).map(_.isDefined)
  }

  /**
   * Fetches the user ID by their email.
   *
   * @param email The email of the user.
   * @return An IO containing either the user ID or an ErrorResponse if the user is not found.
   */
  override def fetchUserIDByEmail(email: String): IO[Either[ErrorResponse, String]] = {
    logger.debug(s"Fetching user ID by email: $email")

    val selectUserId: ConnectionIO[Option[String]] =
      sql"""SELECT user_id FROM users WHERE email = $email"""
        .query[String]
        .option

    selectUserId.transact(xa).map {
      case Some(userId) =>
        logger.info(s"User ID found: $userId")
        Right(userId)
      case None =>
        val errorMessage = s"User with email $email not found"
        logger.warn(errorMessage)
        Left(ErrorResponse(errorMessage))
    }
  }

  /**
   * Checks if an email already exists in the database.
   *
   * @param email The email to check.
   * @return An IO indicating whether the email exists.
   */
  override def checkEmailExists(email: String): IO[Boolean] = {
    sql"""SELECT EXISTS (SELECT 1 FROM users WHERE email = $email)"""
      .query[Boolean]
      .unique
      .transact(xa)
  }
}
