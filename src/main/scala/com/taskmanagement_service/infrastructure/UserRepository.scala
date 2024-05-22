package com.taskmanagement_service.infrastructure

import cats.effect.IO
import com.taskmanagement_service.model.User
trait UserRepository {

  def fetchUserById(id: Long): IO[Option[User]]
}
