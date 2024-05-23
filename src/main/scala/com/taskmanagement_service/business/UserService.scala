package com.taskmanagement_service.business

import com.taskmanagement_service.model.{ErrorResponse, User}

import scala.concurrent.Future
trait UserService {

  def createUser(user: User): Future[Either[ErrorResponse, Int]]
}
