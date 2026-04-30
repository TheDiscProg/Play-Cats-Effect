lazy val commonSettings = Seq(
  scalaVersion := "3.8.3",
  libraryDependencies ++= Dependencies.dependencies,
  scalacOptions ++= Scalac.options
)

lazy val base = (project in file("base"))
  .settings(
    commonSettings,
    name := "Play-Cats-Effect-Base",
    coverageExcludedPackages := Seq(
      "<empty>",
      ".*entities._",
      ".*Algebra.*",
    ).mkString(";"),
    publish / skip := true
  )

lazy val root = (project in file("."))
  .enablePlugins(
    PlayScala,
    ScalafmtPlugin
  )
  .settings(
    commonSettings,
    name := "Play-Cats-Effect",
    Compile / run / javaOptions += "-Dlogback.debug=true",
    Compile / run / javaOptions += "-Dlogback.configurationFile=logback.xml",
    Compile / doc / sources := Seq.empty,
    Compile /scalaSource := baseDirectory.value / "src" / "main" / "scala",
    Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources",
    Test / scalaSource := baseDirectory.value / "src" / "test" / "scala"
  ).aggregate(base)
  .dependsOn(base % "compile->compile;test->test")
