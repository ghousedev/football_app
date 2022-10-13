name := """football_app"""
organization := "dev.ghouse"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.10" % "test",
  "com.dimafeng" %% "testcontainers-scala-mongodb" % "0.40.10" % "test",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.7.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "dev.ghouse.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "dev.ghouse.binders._"
