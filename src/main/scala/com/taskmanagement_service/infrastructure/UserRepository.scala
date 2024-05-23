package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.{ErrorResponse, User}
import doobie.ConnectionIO
trait UserRepository {

  def createUser(user: User): ConnectionIO[Int]
  
  def fetchUserById(id: String): IO[Option[User]]

  def userExist(userId: String): IO[Boolean]

  def fetchUserIDByEmail(email: String): IO[Either[ErrorResponse, String]]

  def checkEmailExists(email: String): IO[Boolean]
}

