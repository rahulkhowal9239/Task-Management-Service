package com.taskmanagement_service.business

import com.taskmanagement_service.model.User
import scala.concurrent.Future
trait UserService {

  def getUserById(id: Long): Future[Option[User]]

}
