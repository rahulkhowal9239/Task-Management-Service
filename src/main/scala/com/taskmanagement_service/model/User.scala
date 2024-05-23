package com.taskmanagement_service.model


final case class User(userId: Option[String] = None, username: String, email: String)
final case class Task(userId: String, taskName: String, description: String, status: Option[String], createdOn: Option[String])
final case class UpdateTask(description: String, status: String)
final case class ErrorResponse(error: String)
final case class AssignTask(userID: Option[String],taskID: Option[String],userEmail: String, taskName: String,
                            description: String,status: Option[String])