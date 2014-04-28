import _root_.sbt._
import _root_.sbt.Classpaths
import _root_.sbt.Keys._

logLevel := Level.Warn

resolvers += Classpaths.typesafeReleases

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")
