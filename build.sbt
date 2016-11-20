name := "ourshows"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5" // or "2.10.4"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.5.0-4",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "org.xerial" % "sqlite-jdbc" % "3.15.1",
  "com.lightbend.play" % "jnotify" % "0.94-play-2"
)

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)

