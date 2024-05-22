package com.taskmanagement_service.business

import com.taskmanagement_service.model.User
import scala.concurrent.Future
trait UserService {

  def createUser(user: User): Future[Int]
  def retrieveUserById(id: Long): Future[Option[User]]

}
