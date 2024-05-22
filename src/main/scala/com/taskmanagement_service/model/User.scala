package com.taskmanagement_service.model

import java.time.LocalDateTime

final case class User(userId: Option[Long] = None, username: String, email: String)
final case class Status(rowsEffected : Int)
case class Task(userId: Long, taskName: String, description: String, status: Option[String], createdOn: Option[String])
