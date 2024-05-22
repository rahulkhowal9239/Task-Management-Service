package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.User
import doobie.ConnectionIO
trait UserRepository {

  def createUser(user: User): ConnectionIO[Int]
  
  def fetchUserById(id: Long): IO[Option[User]]
}
