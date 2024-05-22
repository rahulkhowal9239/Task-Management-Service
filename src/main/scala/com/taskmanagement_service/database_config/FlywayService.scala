package com.taskmanagement_service.database_config

import com.taskmanagement_service.database_config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

import scala.util.Try

class FlywayService(config: DatabaseConfig) {

  private[this] val flyway = Flyway
    .configure()
    .dataSource(config.url, config.user, config.password)
    .baselineOnMigrate(true)
    .load()

  def migrateDatabaseSchema(): MigrateResult = Try(flyway.migrate()).getOrElse {
    flyway.repair()
    flyway.migrate()
  }

  def dropDatabase(): Unit = flyway.clean()
}
