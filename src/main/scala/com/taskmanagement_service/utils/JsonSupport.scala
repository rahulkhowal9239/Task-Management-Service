package com.taskmanagement_service.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.taskmanagement_service.model.User
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat: RootJsonFormat[User] =
    jsonFormat3(User)

}
