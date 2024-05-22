package com.taskmanagement_service.infrastructure

import cats.data.OptionT
import cats.effect.IO
import com.taskmanagement_service.model.User
import com.typesafe.scalalogging.{LazyLogging, Logger}
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository with LazyLogging {

  private val logger: Logger = Logger(getClass)

  override def fetchUserById(userId: Long): IO[Option[User]] = {
    logger.debug(s"Fetching user by ID: $userId")
    val select =
      sql"""SELECT user_id, username, email FROM users WHERE user_id = $userId"""
        .query[User]
        .option

    val resultIO: IO[Option[User]] = select.transact(xa)
    resultIO.flatMap { result =>
      IO {
        result.foreach(_ => logger.info("User found"))
        result
      }
    }
  }
}
