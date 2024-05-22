package com.taskmanagement_service.business

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.taskmanagement_service.infrastructure.UserRepositoryImpl
import com.taskmanagement_service.model.User
import com.typesafe.scalalogging.LazyLogging
import doobie.util.transactor.Transactor

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(implicit xa: Transactor[IO], ec: ExecutionContext)
    extends UserService
    with LazyLogging {

  private val userRepositoryRepo = new UserRepositoryImpl(xa)

  override def getUserById(id: Long): Future[Option[User]] =
    userRepositoryRepo.fetchUserById(id).unsafeToFuture()
}
