package com.taskmanagement_service.database_config

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DatabaseConnector {

  /**
   * Creates a transactor for connecting to the database using the provided configuration.
   *
   * @param cfg The `DatabaseConfig` containing the database connection details.
   * @return A `Transactor.Aux[IO, Unit]` for managing database connections.
   */
  
  def getTransactor(cfg: DatabaseConfig): Aux[IO, Unit] =
    Transactor.fromDriverManager[IO](
      cfg.driver,
      cfg.url,
      cfg.user,
      cfg.password
    )
}
