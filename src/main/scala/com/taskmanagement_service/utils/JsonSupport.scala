package com.taskmanagement_service.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.taskmanagement_service.model.{Task, User}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat: RootJsonFormat[User] =
    jsonFormat3(User)
  implicit val taskFormat: RootJsonFormat[Task] =
    jsonFormat5(Task)
}
