package com.taskmanagement_service.database_config

import cats.effect.IO
import com.taskmanagement_service.database_config.DatabaseConfig
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DatabaseConnector {

  def getTransactor(cfg: DatabaseConfig): Aux[IO, Unit] =
    Transactor.fromDriverManager[IO](
      cfg.driver,
      cfg.url,
      cfg.user,
      cfg.password
    )
}
