ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.3"

// Enable Scalafmt for the project
ThisBuild / scalafmtOnCompile := true

// Scalafix settings
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

// Add common Scala compiler options
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

lazy val root = (project in file("."))
  .settings(
    name := "Task-Management",
    scalafmtOnCompile := true,
    scalafixOnCompile := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
      "org.flywaydb" % "flyway-core" % "7.15.0",
      "com.typesafe.akka" %% "akka-stream" % "2.8.5",
      "com.github.pureconfig" %% "pureconfig-enumeratum" % "0.17.6",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "org.slf4j" % "slf4j-api" % "1.7.36",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test,
      "com.h2database" % "h2" % "1.4.200" % Test
    )
  )

// Enable Scalafix
enablePlugins(ScalafixPlugin)

// Add Scalafix rules
ThisBuild / scalafixConfig := Some(file(".scalafix.conf"))
