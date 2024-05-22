package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.User
import com.typesafe.scalalogging.LazyLogging
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository with LazyLogging {

  /**
   * Inserts a new user into the database.
   *
   * @param user The user to be inserted.
   * @return A ConnectionIO containing the number of affected rows.
   */
  override def createUser(user: User): ConnectionIO[Int] = {
    val insertUser: ConnectionIO[Int] =
      sql"""INSERT INTO users (username, email) VALUES (${user.username}, ${user.email})""".update.run
    logger.info(s"Inserting user: $user")
    insertUser
  }

  /**
   * Fetches a user by their ID.
   *
   * @param userId The ID of the user to be fetched.
   * @return An IO containing an Option with the user if found, or None if not found.
   */
  override def fetchUserById(userId: Long): IO[Option[User]] = {
    logger.debug(s"Fetching user by ID: $userId")
    val selectUser: ConnectionIO[Option[User]] =
      sql"""SELECT id, username, email FROM users WHERE id = $userId"""
        .query[User]
        .option

    selectUser.transact(xa).map { userOption =>
      userOption match {
        case Some(user) =>
          logger.info(s"User found: $user")
        case None =>
          logger.warn(s"User with ID $userId not found")
      }
      userOption
    }
  }
}
