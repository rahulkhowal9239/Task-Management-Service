package com.taskmanagement_service.model

final case class User(userId: Option[Long] = None, username: String, email: String)
