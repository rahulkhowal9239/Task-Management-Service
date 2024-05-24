package com.taskmanagement_service.database_config

import pureconfig._

case class DatabaseConfig(driver: String, url: String, user: String, password: String)
case class Config(host: String, port: Int, dbConfig: DatabaseConfig)

/**
 * Configuration object for loading and reading application configurations.
 *
 * This object utilizes PureConfig to load settings from the default configuration source. It includes
 * implicit readers for `DatabaseConfig` and `Config` case classes, which map specific configuration
 * properties to these classes. The `serviceConf` value holds the loaded configuration and throws a
 * `RuntimeException` if loading fails.
 */

object Configuration {
  implicit val databaseConfigReader: ConfigReader[DatabaseConfig] =
    ConfigReader.forProduct4("driver", "url", "user", "password")(DatabaseConfig.apply)
  implicit val configReader: ConfigReader[Config]                 =
    ConfigReader.forProduct3("host", "port", "dbConfig")(Config.apply)

  val serviceConf: Config = ConfigSource.default.load[Config] match {
    case Right(conf) => conf
    case Left(error) =>
      throw new RuntimeException(s"Error loading configuration: $error")
  }
}
