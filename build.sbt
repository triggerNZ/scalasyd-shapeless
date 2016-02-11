scalaVersion := "2.11.7"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.2.5"

initialCommands in console := """
import shapeless._
import scalasyd.ones._
"""
