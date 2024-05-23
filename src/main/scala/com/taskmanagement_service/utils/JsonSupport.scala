package com.taskmanagement_service.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.taskmanagement_service.model.{AssignTask, Task, UpdateTask, User}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class TaskList(tasks: List[Task])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat5(Task)
  implicit val taskListFormat: RootJsonFormat[TaskList] = jsonFormat1(TaskList)
  implicit val updateTaskFormat: RootJsonFormat[UpdateTask] = jsonFormat2(UpdateTask)
  implicit val assignTaskFormat: RootJsonFormat[AssignTask] = jsonFormat6(AssignTask)

}
