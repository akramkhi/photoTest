name := """dlp-test"""
organization := "com.disneylandparis"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  guice,
  ehcache,
  javaWs
)
