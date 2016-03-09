scalaVersion := "2.11.7"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.0"

initialCommands in console := """
import shapeless._
import scalasyd.popcount._
"""
