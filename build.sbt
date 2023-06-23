ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "twigs",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.15",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.2.0"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
  )
