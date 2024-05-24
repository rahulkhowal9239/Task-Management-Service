package com.taskmanagement_service.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.taskmanagement_service.model.{AssignTask, Task, UpdateTask, User}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


/**
 * Trait providing JSON support for the application models.
 *
 * This trait defines implicit JSON formats for the User, Task, TaskList, UpdateTask, and AssignTask classes,
 * allowing them to be serialized and deserialized using Spray JSON.
 */

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat5(Task)
  implicit val updateTaskFormat: RootJsonFormat[UpdateTask] = jsonFormat2(UpdateTask)
  implicit val assignTaskFormat: RootJsonFormat[AssignTask] = jsonFormat5(AssignTask)

}
