import sbt.*

object Dependencies {

  private val playVersion = "3.0.10"
  private val playTestVersion = "7.0.2"
  private val scalaTestVersion = "3.2.20"
  private val scaffeineVersion = "5.3.0"
  private val catsEffectVersion = "3.7.0"
  private val log4catsVersion = "2.6.0"
  private val logbackVersion = "1.4.11"

  val dependencies = Seq(
    "org.playframework" %% "play" % playVersion,
    "com.github.blemale" %% "scaffeine" % scaffeineVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.typelevel" %% "log4cats-core" % log4catsVersion,
    "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
    "org.scalatestplus.play" %% "scalatestplus-play" % playTestVersion % Test,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )

}
